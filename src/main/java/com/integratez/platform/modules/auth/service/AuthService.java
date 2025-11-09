package com.integratez.platform.modules.auth.service;

import com.integratez.platform.modules.auth.model.Role;
import com.integratez.platform.modules.auth.model.User;
import com.integratez.platform.modules.auth.model.VerificationToken;
import com.integratez.platform.modules.auth.repository.RoleRepository;
import com.integratez.platform.modules.auth.repository.UserRepository;
import com.integratez.platform.modules.auth.repository.VerificationTokenRepository;
import com.integratez.platform.modules.auth.security.JwtUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {



    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    private String validatePassword(String password) {
        if (!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()) {
            return "Password must contain at least 8 characters including " +
                    "one digit, one uppercase letter, one lowercase letter, and one special character";
        }
        else return "Valid";
    }
    public String login(String username, String password) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        claims.put("email", user.getEmail());
        return jwtUtil.generateToken(username, claims);
    }

    public String register(String email, String username, String password, String roleName) {
        if (userRepository.findByUsername(username).isPresent())
            return "Username already exists";

        if(userRepository.findUserByEmail(email).isPresent())
            return "Email already exists";

      String status=validatePassword(password);
      if(!status.equals("Valid"))
          return status;

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = User.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(role))
                .build();

        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        tokenRepository.save(verificationToken);
       // emailService.sendVerificationEmail(email, token);
        return "User registered successfully";
    }


    public String verifyUser(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        return "Email verified successfully!";
    }
}