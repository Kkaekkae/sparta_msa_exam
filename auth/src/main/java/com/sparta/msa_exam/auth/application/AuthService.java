package com.sparta.msa_exam.auth.application;


import com.sparta.msa_exam.auth.application.dtos.AuthResponse;
import com.sparta.msa_exam.auth.application.dtos.SignUpRequest;
import com.sparta.msa_exam.auth.domain.User;
import com.sparta.msa_exam.auth.domain.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private final SecretKey secretKey;


    public AuthService(UserRepository userRepository, @Value("${service.jwt.secret-key}") String secretKey) {
        this.userRepository = userRepository;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public AuthResponse createAccessToken(final String userId) {
        // 먼저, 회원이 가입되어 있는 회원인지 확인합니다.
        return userRepository.findByUserId(userId)
                // user.getUserId() 로 JWT 토큰을 생성합니다. - Spring 심화 강의 응용
                .map(user -> AuthResponse.of(Jwts.builder()
                        .claim("user_id", user.getUserId())
                        .issuer(issuer)
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                        .signWith(secretKey, SignatureAlgorithm.HS512)
                        .compact())
                //유저가 존재하지 않는다면 Exception 을 발생 시킵니다.
                ).orElseThrow();
    }


    // 회원 존재 여부 검증 비즈니스 로직
    public Boolean verifyUser(final String userId) {
        // userId 로 User 를 조회 후 isPresent() 로 존재유무를 리턴함
        return userRepository.findByUserId(userId).isPresent();
    }

    // 회원 가입 비즈니스 로직
    @Transactional
    public void createUser(final SignUpRequest request) {
        // userId 로 User Entity 를 생성 후 DB에 저장함
        userRepository.save(User.create(request.getUserId()));
    }
}
