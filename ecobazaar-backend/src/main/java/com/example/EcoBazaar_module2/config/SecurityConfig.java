package com.example.EcoBazaar_module2.config;

import com.example.EcoBazaar_module2.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * PasswordEncoder bean — BCrypt hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager bean — required for Spring Security's authentication flow.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Register CorsFilter with HIGHEST_PRECEDENCE so CORS headers are added
     * before Spring Security filter chain and authentication checks occur.
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // 1. Explicitly permit all HTTP OPTIONS preflight requests first
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Public endpoints — no token required
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/uploads/**",
                                "/api/auth/**",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        // 3. Product creation — only SELLER or ADMIN roles
                        .requestMatchers(HttpMethod.POST, "/api/products/add").hasAnyAuthority("SELLER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyAuthority("SELLER", "ADMIN")

                        // 4. All other endpoints are permitted (controllers handle their own auth checks)
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed frontend origins (supports patterns for dynamic localhost ports & Vercel deployment subdomains)
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://*.vercel.app",
                "https://eco-bazaar-mg.vercel.app",
                "https://eco-bazaar-virid.vercel.app"
        ));

        // All standard HTTP methods including OPTIONS and HEAD
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));

        // Allow all headers (Authorization, Content-Type, Accept, Origin, X-Requested-With, etc.)
        configuration.setAllowedHeaders(List.of("*"));

        // Exposed headers
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "Content-Disposition"));

        // Allow credentials (cookies / auth headers)
        configuration.setAllowCredentials(true);

        // Cache preflight for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}