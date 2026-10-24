package com.daramassalik.backend.config;

import com.daramassalik.backend.auth.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Token absent/expiré/invalide sur route protégée => 401.
                // Utilisateur authentifié sans permission => 403.
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health"
                        ).permitAll()

                        // Catalogue public en lecture
                        .requestMatchers(HttpMethod.GET, "/api/shop/products/**").permitAll()

                        // Routes authentifiées
                        .requestMatchers("/api/me/**").authenticated()
                        .requestMatchers("/api/orders/**").authenticated()

                        // Gestion pédagogique
                        .requestMatchers("/api/students/**").hasAuthority("PERM_STUDENT_MANAGE")
                        .requestMatchers("/api/teachers/**").hasAuthority("PERM_TEACHER_MANAGE")
                        .requestMatchers("/api/groups/**").hasAuthority("PERM_GROUP_MANAGE")
                        .requestMatchers("/api/attendance/**").hasAuthority("PERM_ATTENDANCE_MANAGE")
                        .requestMatchers("/api/pedagogical-tracking/**").hasAuthority("PERM_PEDAGOGICAL_TRACKING_MANAGE")

                        // Gestion du dahira
                        .requestMatchers("/api/members/**").hasAuthority("PERM_MEMBER_MANAGE")
                        .requestMatchers("/api/commissions/**").hasAuthority("PERM_COMMISSION_MANAGE")
                        .requestMatchers("/api/activities/**").hasAuthority("PERM_ACTIVITY_MANAGE")
                        .requestMatchers("/api/payments/**").hasAuthority("PERM_PAYMENT_MANAGE")

                        // Administration du catalogue
                        .requestMatchers("/api/shop/products/**").hasAuthority("PERM_PRODUCT_MANAGE")

                        // Administration des utilisateurs et des accès
                        .requestMatchers("/api/users/**").hasAuthority("PERM_USER_MANAGE")
                        .requestMatchers("/api/admin/access/**").hasAuthority("PERM_PERMISSION_MANAGE")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:4200,http://127.0.0.1:4200}") String allowedOrigins
    ) {
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();
        if (origins.isEmpty()) {
            throw new IllegalStateException("At least one CORS origin must be configured.");
        }

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Location"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
