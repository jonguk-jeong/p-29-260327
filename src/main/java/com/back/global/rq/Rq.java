package com.back.global.rq;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;

@Component
@RequestScope // 각 HTTP 요청마다 새로운 Rq 객체가 생성되고, 요청이 끝나면 해당 객체는 소멸됩니다.
@RequiredArgsConstructor
public class Rq {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final MemberService memberService;

    public void addCookie(String name, String value) {

        Cookie cookie = new Cookie(name, value);
        cookie.setPath(("/"));
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");

        response.addCookie(
                cookie
        );
    }

    public Member getActor() {

        String authorizationHeader = request.getHeader("Authorization");

        String apiKey;
        if (authorizationHeader != null) {
            // 헤더 방식
            if (!authorizationHeader.startsWith("Bearer ")) {
                throw new ServiceException("401-2", "잘못된 형식의 인증데이터입니다.");
            }

            apiKey = authorizationHeader.replace("Bearer ", "");
        } else {
            // 쿠키 방식
            apiKey = request.getCookies() == null ? ""
                    : Arrays.stream(request.getCookies())
                      .filter(cookie -> cookie.getName().equals("apiKey"))
                      .map(Cookie::getValue)
                      .findFirst()
                      .orElse("");

        }

        if(apiKey.isBlank()) {
            throw new ServiceException("401-3", "인증 정보가 존재하지 않습니다.");
        }

        return memberService.findByApiKey(apiKey).orElseThrow(
                () -> new ServiceException("401-1", "유효하지 않은 API 키입니다.")
        );
    }

    public void deleteCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
