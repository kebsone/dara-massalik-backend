package com.daramassalik.backend.config;

import com.daramassalik.backend.auth.AppUser;
import com.daramassalik.backend.auth.AppUserRepository;
import com.daramassalik.backend.auth.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * Optional one-time bootstrap for the first administrator.
 * Disabled by default. Existing accounts are never modified by this initializer.
 */
@Configuration
public class DataInitializer {

    @Bean
    @ConditionalOnProperty(name = "app.bootstrap-admin.enabled", havingValue = "true")
    CommandLineRunner bootstrapAdmin(
            AppUserRepository repository,
            PasswordEncoder encoder,
            @Value("${app.bootstrap-admin.email:}") String configuredEmail,
            @Value("${app.bootstrap-admin.password:}") String configuredPassword,
            @Value("${app.bootstrap-admin.first-name:Administrateur}") String firstName,
            @Value("${app.bootstrap-admin.last-name:Dara Massalik}") String lastName,
            @Value("${app.bootstrap-admin.phone:}") String phone
    ) {
        return args -> {
            if (!StringUtils.hasText(configuredEmail) || !StringUtils.hasText(configuredPassword)) {
                throw new IllegalStateException(
                        "Admin bootstrap is enabled but APP_BOOTSTRAP_ADMIN_EMAIL or APP_BOOTSTRAP_ADMIN_PASSWORD is missing."
                );
            }
            if (configuredPassword.length() < 16) {
                throw new IllegalStateException(
                        "APP_BOOTSTRAP_ADMIN_PASSWORD must contain at least 16 characters."
                );
            }

            String email = configuredEmail.trim().toLowerCase(Locale.ROOT);
            if (repository.findByEmailIgnoreCase(email).isPresent()) {
                // Critical: never reset an existing administrator's password on restart.
                return;
            }

            AppUser user = new AppUser();
            user.setEmail(email);
            user.setPasswordHash(encoder.encode(configuredPassword));
            user.addRole(Role.ADMIN);
            user.setActive(true);
            user.setFirstName(StringUtils.hasText(firstName) ? firstName.trim() : "Administrateur");
            user.setLastName(StringUtils.hasText(lastName) ? lastName.trim() : "Dara Massalik");
            if (StringUtils.hasText(phone)) {
                user.setPhone(phone.trim());
            }
            repository.save(user);
        };
    }
}
