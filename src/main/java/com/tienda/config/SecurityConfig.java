package com.tienda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                // =====================================================
                // PRODUCTOS
                // ADMIN Y VENDEDOR
                // =====================================================

                .requestMatchers("/producto/**")
                .hasAnyRole("ADMIN", "VENDEDOR")

                // =====================================================
                // CATEGORÍAS
                // SOLO ADMIN
                // =====================================================

                .requestMatchers("/categoria/**")
                .hasRole("ADMIN")

                // =====================================================
                // CONSULTAS
                // ADMIN Y VENDEDOR
                // =====================================================

                .requestMatchers("/consultas/listado")
                .hasAnyRole("ADMIN", "VENDEDOR")

                .requestMatchers("/consultas/consultaDerivada")
                .hasAnyRole("ADMIN", "VENDEDOR")

                .requestMatchers("/consultas/consultaJPQL")
                .hasAnyRole("ADMIN", "VENDEDOR")

                .requestMatchers("/consultas/consultaSQL")
                .hasAnyRole("ADMIN", "VENDEDOR")

                // =====================================================
                // PÁGINAS PÚBLICAS
                // =====================================================

                .requestMatchers(
                        "/",
                        "/consultas/*",
                        "/sesion/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/webjars/**"
                ).permitAll()

                // =====================================================
                // CUALQUIER OTRA RUTA
                // =====================================================

                .anyRequest().authenticated()
                )

                // =====================================================
                // LOGIN
                // =====================================================

                .formLogin(form -> form
                        .loginPage("/sesion/listado")
                        .loginProcessingUrl("/sesion/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/sesion/listado?error=true")
                        .permitAll()
                )

                // =====================================================
                // LOGOUT
                // =====================================================

                .logout(logout -> logout
                        .logoutUrl("/sesion/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                );

        return http.build();
    }
}