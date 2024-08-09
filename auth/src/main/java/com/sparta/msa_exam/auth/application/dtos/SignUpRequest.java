package com.sparta.msa_exam.auth.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원가입 요청 객체 입니다.
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String userId;
}
