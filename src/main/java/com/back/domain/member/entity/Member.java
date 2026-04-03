package com.back.domain.member.entity;

import com.back.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Column(unique = true)
    private String username;
    private String password;
    private String nickname;
    @Column(unique = true)
    private  String apiKey;

    public Member (String username, String password, String nickname, String apiKey) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.apiKey = apiKey;
    }

    public Member(int id, String username, String nickname) {
        this.setId(id);
        this.username = username;
        this.nickname = nickname;
    }

    // 멤버의 상태값을 정책에 따라 결정
    // 우리가 글 작성시 보이는 이름을 nickname으로 할지 username으로 할지 여기서 바꾸면 됨
    // 추상화 한 것!
    public String getName() {
        return nickname;
    }

    public boolean isAdmin() {
        return "admin".equals(username);
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        if (isAdmin()) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }
}
