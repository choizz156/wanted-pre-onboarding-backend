package com.wanted.preonboarding;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private String email;

    private String password;

    @Enumerated(STRING)
    private UserRoles roles = UserRoles.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final Set<Post> posts = new HashSet<>();

    @Builder
    public User(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public void applyEncryptPassword(String encryptedPwd) {
        this.password = encryptedPwd;
    }

    public void addPost(final Post post) {
        posts.add(post);
    }
}
