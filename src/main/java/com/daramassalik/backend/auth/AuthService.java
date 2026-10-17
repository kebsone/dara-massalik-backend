package com.daramassalik.backend.auth;

import com.daramassalik.backend.common.BadCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OtpChallengeRepository otpRepository;
    private final SmsSender smsSender;
    private final PermissionService permissionService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long otpExpirationSeconds;
    private final boolean exposeDevCode;
    private final Environment environment;
    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);
    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            OtpChallengeRepository otpRepository,
            SmsSender smsSender,
            PermissionService permissionService,
            Environment environment,
            @Value("${app.auth.otp.expiration-seconds:300}") long otpExpirationSeconds,
            @Value("${app.auth.otp.expose-dev-code:false}") boolean exposeDevCode) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.otpRepository = otpRepository;
        this.smsSender = smsSender;
        this.environment = environment;
        this.permissionService = permissionService;
        this.otpExpirationSeconds = otpExpirationSeconds;
        this.exposeDevCode = exposeDevCode;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(BadCredentialsException::new);
        if (!user.isActive() || user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException();
        }
        return issueTokens(user);
    }

    @Transactional
    public RequestOtpResponse requestOtp(RequestOtpRequest request) {
        String phone = normalizePhone(request.phoneNumber());
        AppUser user = userRepository.findByPhone(phone).orElse(null);
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));

        otpRepository.findTopByPhoneOrderByCreatedAtDesc(phone).ifPresent(previous -> {
            if (previous.getConsumedAt() == null) previous.setConsumedAt(Instant.now());
        });

        OtpChallenge challenge = new OtpChallenge();
        challenge.setPhone(phone);
        challenge.setUser(user);
        challenge.setCodeHash(passwordEncoder.encode(code));
        challenge.setExpiresAt(Instant.now().plusSeconds(otpExpirationSeconds));
        otpRepository.save(challenge);
        log.info(
                "SMS provider configured by Spring: {}",
                environment.getProperty("app.sms.provider", "logging")
        );
        smsSender.sendOtp(phone, code);
        return new RequestOtpResponse(challenge.getId(), otpExpirationSeconds, user != null,
                exposeDevCode ? code : null);
    }

    @Transactional(noRollbackFor = OtpInvalidException.class)
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String phone = normalizePhone(request.phoneNumber());
        OtpChallenge challenge = otpRepository.findById(request.challengeId())
                .orElseThrow(() -> new OtpInvalidException("Code de vérification invalide."));

        if (!Objects.equals(challenge.getPhone(), phone) || !challenge.isUsable()) {
            throw new OtpInvalidException("Code de vérification expiré ou invalide.");
        }

        challenge.setAttempts(challenge.getAttempts() + 1);
        if (!passwordEncoder.matches(request.code(), challenge.getCodeHash())) {
            throw new OtpInvalidException("Code de vérification invalide.");
        }

        AppUser user = challenge.getUser();
        if (user == null) {
            user = userRepository.findByPhone(phone).orElse(null);
        }
        if (user != null && !user.isActive()) {
            throw new OtpInvalidException("Ce compte est désactivé.");
        }

        if (user == null) {
            if (isBlank(request.firstName()) || isBlank(request.lastName())) {
                throw new OtpInvalidException("Le prénom et le nom sont requis pour créer le compte.");
            }
            user = new AppUser();
            user.setPhone(phone);
            user.setFirstName(request.firstName().trim());
            user.setLastName(request.lastName().trim());
            user.addRole(Role.MEMBER);
            user.setActive(true);
            if (!isBlank(request.email())) {
                ensureEmailAvailable(request.email(), null);
                user.setEmail(request.email().trim().toLowerCase());
            }
            userRepository.save(user);
            challenge.setUser(user);
        } else {
            challenge.setUser(user);
            if (isBlank(user.getFirstName()) && !isBlank(request.firstName())) user.setFirstName(request.firstName().trim());
            if (isBlank(user.getLastName()) && !isBlank(request.lastName())) user.setLastName(request.lastName().trim());
            if (isBlank(user.getEmail()) && !isBlank(request.email())) {
                ensureEmailAvailable(request.email(), user.getId());
                user.setEmail(request.email().trim().toLowerCase());
            }
        }

        challenge.setConsumedAt(Instant.now());
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {
        RefreshToken oldToken = refreshTokenService.rotate(rawRefreshToken);
        return issueTokens(oldToken.getUser());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    public AuthUserResponse toUserResponse(AppUser user) {
        return new AuthUserResponse(user.getId(), user.getPhone(), user.getEmail(),
                user.getFirstName(), user.getLastName(), user.getPrimaryRole(),
                user.getRoles().stream().sorted(java.util.Comparator.comparing(Enum::name)).toList(),
                permissionService.getEffectivePermissions(user).stream().sorted(java.util.Comparator.comparing(Enum::name)).toList());
    }

    private AuthResponse issueTokens(AppUser user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.create(user);
        return new AuthResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                refreshToken,
                toUserResponse(user)
        );
    }

    private void ensureEmailAvailable(String email, Long currentUserId) {
        userRepository.findByEmailIgnoreCase(email.trim())
                .filter(existing -> !existing.getId().equals(currentUserId))
                .ifPresent(existing -> { throw new IllegalArgumentException("Cette adresse e-mail est déjà utilisée."); });
    }

    private String normalizePhone(String phone) {
        String normalized = phone.replaceAll("[\\s().-]", "");
        if (normalized.startsWith("00")) normalized = "+" + normalized.substring(2);
        if (!normalized.startsWith("+")) throw new IllegalArgumentException("Le numéro doit être au format international, par exemple +33612345678.");
        return normalized;
    }

    private boolean isBlank(String value) { return value == null || value.isBlank(); }
}
