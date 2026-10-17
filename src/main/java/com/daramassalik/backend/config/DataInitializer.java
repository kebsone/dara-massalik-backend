package com.daramassalik.backend.config;

import com.daramassalik.backend.auth.AppUser;
import com.daramassalik.backend.auth.AppUserRepository;
import com.daramassalik.backend.auth.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedAdmin(AppUserRepository repository, PasswordEncoder encoder) {
        return args -> {
            AppUser user = repository.findByEmailIgnoreCase("admin@daramassalik.local").orElseGet(AppUser::new);
            user.setEmail("admin@daramassalik.local");
            user.setPasswordHash(encoder.encode("ChangeMe123!"));
            user.addRole(Role.ADMIN);
            user.setActive(true);
            user.setFirstName("Administrateur");
            user.setLastName("Dara Massalik");
            if (user.getPhone() == null) user.setPhone("+33600000000");
            repository.save(user);
        };
    }
}
