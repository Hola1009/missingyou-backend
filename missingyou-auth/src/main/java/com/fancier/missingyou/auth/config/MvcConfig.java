package com.fancier.missingyou.auth.config;

import com.fancier.missingyou.auth.interceptor.UserInfoInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许的路径
                .allowedOrigins("http://localhost:3000") // 指定允许的源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .allowCredentials(true) // 是否允许带上凭证（如 Cookies）
                .allowedHeaders("*"); // 允许的请求头
    }
}