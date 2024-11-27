package com.fancier.missingyou.common.constant;

/**
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
public interface RedisConstant {

    /**
     * 用户签到记录的 Redis Key 前缀
     */
    String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signIns";

    /**
     * 获取用户签到记录的 Redis key
     *
     */
    static String getUserSignInRedisKey(int year, long userId) {
        return String.format("%s:%s:%s", USER_SIGN_IN_REDIS_KEY_PREFIX, year, userId);
    }

}
