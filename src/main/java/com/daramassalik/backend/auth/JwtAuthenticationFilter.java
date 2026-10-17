package com.daramassalik.backend.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AppUserRepository userRepository;
    private final PermissionService permissionService;

    public JwtAuthenticationFilter(JwtService jwtService, AppUserRepository userRepository, PermissionService permissionService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return CorsUtils.isPreFlightRequest(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.parse(header.substring(7));
                Long userId = Long.valueOf(claims.getSubject());
                userRepository.findById(userId).filter(AppUser::isActive).ifPresent(user -> {
                    List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>(user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                            .toList());
                    permissionService.getEffectivePermissions(user).stream()
                            .map(permission -> new SimpleGrantedAuthority("PERM_" + permission.name()))
                            .forEach(authorities::add);

                    var authentication = new UsernamePasswordAuthenticationToken(
                            String.valueOf(user.getId()), null, authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
