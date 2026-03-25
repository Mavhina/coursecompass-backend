package com.coursecompass.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /* @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    } */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // WebSocket handshake — must be permitAll so SockJS can connect
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/ws/info/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/bursaries/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bursaries").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/universities/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/universities").permitAll()
                        .requestMatchers("/api/student/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/student/chats").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/student/groups").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/student/messages/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/fee-fund/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/chats/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/university-guides", "/api/university-guide/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tutor-businesses/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tutors/**").hasAnyRole("USER", "ADMIN", "TUTOR")
                        .requestMatchers("/api/courses/**").hasRole("ADMIN")
                        .requestMatchers("/api/subjects", "/api/subjects/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/student/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/bookings").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/check").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/my-bookings").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/cancel").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/tutor-bookings").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/tutor/pending").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/tutor/pending-count").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/respond").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers("/api/tutor/dashboard/**").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers("/api/tutor/students/**").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tutor/students/search").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tutor/students/add-platform").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tutor/students/add-external").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tutor/businesses").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers("/api/tutor/groups/**").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers("/api/tutor/sessions/**").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers("/api/tutor/messages/broadcast").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers("/api/tutor/messages/group/**").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers("/api/tutor/messages/*/read").hasAnyRole("TUTOR", "ADMIN")
                        .requestMatchers("/api/student/messages/*/read").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers("/api/student/messages/group/*/read").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers("/api/tutor/business/**").hasAnyRole("TUTOR", "ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}