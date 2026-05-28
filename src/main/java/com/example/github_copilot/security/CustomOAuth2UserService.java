package com.example.github_copilot.security;

import com.example.github_copilot.model.Role;
import com.example.github_copilot.model.User;
import com.example.github_copilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String email = extractEmail(oAuth2User, registrationId);
        String name = extractName(oAuth2User, registrationId);
        String providerId = oAuth2User.getAttribute("id") != null
                ? oAuth2User.getAttribute("id").toString()
                : oAuth2User.getAttribute("sub");

        User.AuthProvider provider = registrationId.equalsIgnoreCase("google")
                ? User.AuthProvider.GOOGLE
                : User.AuthProvider.GITHUB;

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .role(Role.ROLE_USER)
                    .authProvider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(newUser);
        } else {
            User user = existingUser.get();
            user.setName(name);
            user.setProviderId(providerId);
            userRepository.save(user);
        }

        return oAuth2User;
    }

    private String extractEmail(OAuth2User oAuth2User, String registrationId) {
        if ("github".equalsIgnoreCase(registrationId)) {
            String email = oAuth2User.getAttribute("email");
            if (email == null) {
                // GitHub may not provide email, use login as fallback
                return oAuth2User.getAttribute("login") + "@github.com";
            }
            return email;
        }
        return oAuth2User.getAttribute("email");
    }

    private String extractName(OAuth2User oAuth2User, String registrationId) {
        if ("github".equalsIgnoreCase(registrationId)) {
            String name = oAuth2User.getAttribute("name");
            return name != null ? name : oAuth2User.getAttribute("login");
        }
        return oAuth2User.getAttribute("name");
    }
}
