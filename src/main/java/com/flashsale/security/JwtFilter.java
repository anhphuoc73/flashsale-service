package com.flashsale.security;

import com.flashsale.user.entity.User;
import com.flashsale.user.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import  lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("AUTH HEADER: " + authHeader);

        // Nếu không có header → cho đi tiếp
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            if (tokenBlackListService.isBlacklisted(token)) {
                System.out.println("BLACKLIST HIT");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            boolean isValid = jwtService.validateToken(token, "access");
            System.out.println("Valid: " + isValid);
            if (!isValid) {
                System.out.println("TOKEN INVALID");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractEmail(token);

            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                System.out.println("USER NOT FOUND");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + user.getRole());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(authority)
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("userId", user.getId());
                System.out.println("AUTHENTICATION SET SUCCESS");
            }

        } catch (Exception e) {
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }
        System.out.println("FINAL AUTH: " +
                SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);

    }
}
