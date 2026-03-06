package com.flashsale.config;

import com.flashsale.config.CustomAccessDeniedHandler;
import com.flashsale.config.CustomAuthenticationEntryPoint;
import com.flashsale.security.JwtFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // PRODUCT
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // SESSION - ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/api/sessions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/sessions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/sessions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/sessions/**").hasRole("ADMIN")

                        // FLASH SALE ITEM - ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/api/admin/flash-sale-items/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/flash-sale-items/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/flash-sale-items/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/flash-sale-items/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/flash-sale-items/current").permitAll()

                        // PURCHASE
                        .requestMatchers(HttpMethod.POST, "/api/purchases").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/purchases").hasAnyRole("USER","ADMIN")

                        .anyRequest().authenticated()


                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}