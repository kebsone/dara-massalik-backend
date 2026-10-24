package com.daramassalik.backend.config;

import com.daramassalik.backend.auth.AppUser;
import com.daramassalik.backend.auth.AppUserRepository;
import com.daramassalik.backend.auth.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

/**
 * Optional first-start administrator creation for production.
 * The account is created only when the email does not exist; an existing
 * account's password and roles are never rewritten by this bootstrap.
 */
@Configuration
@Profile("prod")
public class ProductionAdminBootstrap {
    private static final Logger log = LoggerFactory.getLogger(ProductionAdminBootstrap.class);

    @Bean
    CommandLineRunner bootstrapInitialAdmin(
            AppUserRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.email:}") String configuredEmail,
            @Value("${app.bootstrap-admin.password:}") String configuredPassword,
            @Value("${app.bootstrap-admin.first-name:Administrateur}") String firstName,
            @Value("${app.bootstrap-admin.last-name:Dara Massalik}") String lastName
    ) {
        return args -> {
            boolean hasEmail = configuredEmail != null && !configuredEmail.isBlank();
            boolean hasPassword = configuredPassword != null && !configuredPassword.isBlank();

            if (!hasEmail && !hasPassword) {
                log.info("No production bootstrap administrator configured.");
                return;
            }
            if (!hasEmail || !hasPassword) {
                throw new IllegalStateException(
                        "Set both BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD, or leave both unset."
                );
            }
            if (configuredPassword.length() < 12) {
                throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD must contain at least 12 characters.");
            }

            String email = configuredEmail.trim().toLowerCase(Locale.ROOT);
            if (repository.findByEmailIgnoreCase(email).isPresent()) {
                log.info("Bootstrap administrator {} already exists; no account changes were made.", email);
                return;
            }

            AppUser admin = new AppUser();
            admin.setEmail(email);
            admin.setPasswordHash(passwordEncoder.encode(configuredPassword));
            admin.setFirstName(firstName == null || firstName.isBlank() ? "Administrateur" : firstName.trim());
            admin.setLastName(lastName == null || lastName.isBlank() ? "Dara Massalik" : lastName.trim());
            admin.setActive(true);
            admin.addRole(Role.ADMIN);
            repository.save(admin);
            log.warn("Created initial administrator {}. Remove the bootstrap variables from the host now.", email);
        };
    }
}
