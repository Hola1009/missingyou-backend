package com.fancier.missingyou.auth.interceptor;

import com.fancier.missingyou.auth.service.UserService;
import com.fancier.missingyou.auth.util.UserHolder;
import com.fancier.missingyou.common.model.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截请求，获取用户信息，存入 ThreadLocal 中，供后续使用
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

@Component
@RequiredArgsConstructor
public class UserInfoInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 1. 将用户信息存入 ThreadLocal 中
        UserVO userVO = userService.getLoginUserVO(request);
        UserHolder.saveUser(userVO);
        // 有用户，则放行
        return true;
    }


    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {
        UserHolder.removeUser();
    }
}

