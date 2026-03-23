package com.rzd.user.controller;

import com.rzd.common.dto.UserDTO;
import com.rzd.common.dto.response.UserResponse;
import com.rzd.user.model.dto.response.UserProfileResponse;

import com.rzd.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rzd.user.repository.UserRepository;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserProfileResponse profile = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        UserResponse response = UserResponse.builder()
                .email(user.getEmail())
                .companyName(user.getCompanyName())
                .inn(String.valueOf(user.getInn()))
                .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        UserDTO response = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .companyName(user.getCompanyName())
                .inn(user.getInn())
                .role(user.getRole().name())
                .build();

        return ResponseEntity.ok(response);
    }
}