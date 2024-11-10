package com.fancier.missingyou.common.constant;

/**
 * 用户常量
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 * 
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "fancier";
}
