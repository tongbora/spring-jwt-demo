package bora.tong.springjwt.controller;

import bora.tong.springjwt.dto.LoginRequest;
import bora.tong.springjwt.dto.TokenResponse;
import bora.tong.springjwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final AuthService authService;

    @PostMapping("/token")
    TokenResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication authenticated = daoAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(), loginRequest.password()));

        String token = authService.generateToken(authenticated);
        return new TokenResponse(token, "Bearer", 3600);   // 3600 = 1 hour, matches the token expiry
    }

    @GetMapping("/hello")
    Map<String, Object> hello(Authentication auth) {
        return Map.of(
                "username", auth.getName(),
                "roles", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());
    }
}