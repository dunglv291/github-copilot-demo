 package com.example.github_copilot.security;

import com.example.github_copilot.model.User;
import com.example.github_copilot.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${oauth2.redirect-uri:http://localhost:3000/oauth2/callback}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // GitHub fallback
        if (email == null) {
            email = oAuth2User.getAttribute("login") + "@github.com";
        }

        User user = userRepository.findByEmail(email).orElseThrow();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        String targetUrl = redirectUri + "?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
