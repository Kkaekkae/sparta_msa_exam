package com.sparta.msa_exam.auth.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private String userId;

    // 유저 생성 메서드
    public static User create(
            final String userId
    ) {
        return User.builder()
                .userId(userId)
                .build();
    }
}
