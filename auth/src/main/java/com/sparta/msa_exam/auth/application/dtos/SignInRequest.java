package com.sparta.msa_exam.auth.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    String username;
    String password;
}
