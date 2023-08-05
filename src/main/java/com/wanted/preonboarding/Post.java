package com.wanted.preonboarding;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Post(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    public void addUser(final User user) {
        this.user = user;
        this.user.addPost(this);
    }

    public void editTitle(final String title) {
        this.title = title;
    }

    public void editContent(final String content) {
        this.content = content;
    }
}
