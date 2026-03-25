package com.coursecompass.backend.config;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.security.JwtUtil;
import com.coursecompass.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    System.out.println("🔌 WebSocket CONNECT received");

                    // Try Authorization header first
                    String token = null;
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    System.out.println("🔑 Auth header: " + authHeader);

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                    }

                    // Fallback: try token from URL query param (?token=xxx)
                    if (token == null) {
                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                        if (sessionAttributes != null) {
                            Object queryToken = sessionAttributes.get("token");
                            if (queryToken != null) {
                                token = queryToken.toString();
                                System.out.println("🔑 Token from session attributes: found");
                            }
                        }
                    }

                    if (token != null) {
                        try {
                            String email = jwtUtil.extractEmail(token);
                            User user = userService.findByEmail(email);
                            if (user != null && jwtUtil.validateToken(token, user)) {
                                List<SimpleGrantedAuthority> authorities = List.of(
                                        new SimpleGrantedAuthority("ROLE_" + user.getRole())
                                );
                                UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(
                                                user, null, authorities
                                        );
                                accessor.setUser(auth);
                                System.out.println("✅ WebSocket authenticated: " + email);
                            } else {
                                System.out.println("❌ Token invalid or user not found");
                            }
                        } catch (Exception e) {
                            System.err.println("❌ WebSocket JWT error: " + e.getMessage());
                        }
                    } else {
                        System.out.println("❌ No token found in CONNECT frame");
                    }
                }
                return message;
            }
        });
    }
}