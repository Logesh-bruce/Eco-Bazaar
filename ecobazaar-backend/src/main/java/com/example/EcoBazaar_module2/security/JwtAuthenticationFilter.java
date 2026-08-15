package com.example.EcoBazaar_module2.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Bypass token parsing and validation on CORS preflight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);

                // Extract the actual role from the JWT claims (e.g. "SELLER", "USER", "ADMIN")
                // and map it to both "ROLE_X" and "X" SimpleGrantedAuthorities so both
                // hasRole() and hasAuthority() match seamlessly.
                String rawRole = jwtUtil.getRoleFromToken(token);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (rawRole != null && !rawRole.trim().isEmpty()) {
                    String cleanRole = rawRole.trim().replace("ROLE_", "").toUpperCase();
                    authorities.add(new SimpleGrantedAuthority(cleanRole));
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + cleanRole));
                } else {
                    authorities.add(new SimpleGrantedAuthority("USER"));
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
