package com.sparta.msa_exam.auth.application.dtos;

import lombok.*;

// 유저 로그인 시 응답 객체 입니다.
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class AuthResponse {
    private String accessToken;

    public static AuthResponse of(String accessToken) {
        return AuthResponse.builder().accessToken(accessToken).build();
    }
}
