package com.coursecompass.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ResendEmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendInviteEmail(String toEmail, String studentName, String tutorName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String html = """
                <div style="font-family:sans-serif;max-width:480px;margin:0 auto;padding:32px;">
                    <h2 style="color:#6366f1;">You've been invited to CourseCompass! 🎓</h2>
                    <p>Hi <strong>%s</strong>,</p>
                    <p><strong>%s</strong> has added you as a student on CourseCompass — a platform to manage your tutoring sessions and payments.</p>
                    <p style="margin:24px 0;">
                        <a href="http://localhost:5173"
                           style="background:#6366f1;color:#fff;padding:12px 24px;border-radius:8px;text-decoration:none;font-weight:600;">
                            Join CourseCompass
                        </a>
                    </p>
                    <p style="color:#9ca3af;font-size:13px;">If you weren't expecting this, you can ignore this email.</p>
                </div>
                """.formatted(studentName, tutorName);

            Map<String, Object> body = Map.of(
                    "from", "CourseCompass <onboarding@resend.dev>",
                    "to", new String[]{toEmail},
                    "subject", tutorName + " has added you as a student on CourseCompass",
                    "html", html
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity("https://api.resend.com/emails", entity, String.class);
            System.out.println("✅ Invite email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send invite email: " + e.getMessage());
        }
    }
}