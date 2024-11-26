package com.fancier.missingyou.auth.config;

import com.fancier.missingyou.auth.interceptor.UserInfoInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final UserInfoInterceptor userInfoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截器: 拦截请求，获取用户信息，存入 ThreadLocal
        registry.addInterceptor(userInfoInterceptor)
                .excludePathPatterns("/**")
                .order(0);
    }

}