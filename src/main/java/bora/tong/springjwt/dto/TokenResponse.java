package bora.tong.springjwt.dto;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {}