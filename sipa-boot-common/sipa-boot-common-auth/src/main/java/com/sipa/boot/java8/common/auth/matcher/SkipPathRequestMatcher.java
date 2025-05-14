package com.sipa.boot.java8.common.auth.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author zhouxiajie
 * @date 2018/2/6
 */
public class SkipPathRequestMatcher implements RequestMatcher {
    private OrRequestMatcher skipMatchers;

    private RequestMatcher processingMatcher;

    public SkipPathRequestMatcher(List<String> pathsToSkip, String processingPath) {
        List<RequestMatcher> m = pathsToSkip.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList());

        skipMatchers = new OrRequestMatcher(m);
        processingMatcher = new AntPathRequestMatcher(processingPath);
    }

    public SkipPathRequestMatcher(HttpMethod method, List<String> allows, String processingPath) {
        List<RequestMatcher> matchers = new ArrayList<>();
        allows.forEach(allow -> matchers.add(new AntPathRequestMatcher(allow)));
        matchers.add(request -> method.name().equals(request.getMethod()));
        skipMatchers = new OrRequestMatcher(matchers);
        processingMatcher = new AntPathRequestMatcher(processingPath);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !skipMatchers.matches(request) && processingMatcher.matches(request);
    }
}
