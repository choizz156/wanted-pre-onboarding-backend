package com.wanted.preonboarding.domain.user.entity;

import static lombok.AccessLevel.PROTECTED;

import com.wanted.preonboarding.domain.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private String email;

    @Column(length = 25)
    private String password;

    @Builder
    private User(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public static User create()
}
