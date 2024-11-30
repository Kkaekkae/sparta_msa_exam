package com.sparta.msa_exam.auth.application;


import com.sparta.msa_exam.auth.application.dtos.AuthResponse;
import com.sparta.msa_exam.auth.application.dtos.SignInRequest;
import com.sparta.msa_exam.auth.application.dtos.SignUpRequest;
import com.sparta.msa_exam.auth.domain.User;
import com.sparta.msa_exam.auth.domain.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SecretKey secretKey;
    private final PasswordEncoder passwordEncoder;
    @Value("${spring.application.name}")
    private String issuer;
    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;


    public AuthService(UserRepository userRepository, @Value("${service.jwt.secret-key}") String secretKey, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse createAccessToken(final SignInRequest request) {
        // 먼저, 회원이 가입되어 있는 회원인지 확인합니다.
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> AuthResponse.of(Jwts.builder()  // user.getId() 로 JWT 토큰을 생성합니다.
                        .claim("user_id", user.getId())
                        .issuer(issuer)
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                        .signWith(secretKey, SignatureAlgorithm.HS512)
                        .compact())
                ).orElseThrow();
    }


    // 회원 존재 여부 검증 비즈니스 로직
    public Boolean verifyUser(final Long id) {
        // userId 로 User 를 조회 후 isPresent() 로 존재유무를 리턴함
        return userRepository.findById(id).isPresent();
    }

    // 회원 가입 비즈니스 로직
    @Transactional
    public void createUser(final SignUpRequest request) {
        // userId 로 User Entity 를 생성 후 DB에 저장함
        userRepository.save(User.create(request.getUsername(), passwordEncoder.encode(request.getPassword())));
    }
}
