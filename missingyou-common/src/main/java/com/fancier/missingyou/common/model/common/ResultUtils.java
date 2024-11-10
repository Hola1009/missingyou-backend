package com.fancier.missingyou.common.model.common;


import com.fancier.missingyou.common.expection.ErrorCode;

/**
 * 返回工具类
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 * 
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data data object
     * @param <T> type
     * @return r
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode the error code
     * @return r
     */
    public static BaseResponse<Object> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code the error code
     * @param message the error message
     * @return r
     */
    public static BaseResponse<Object> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode the error code
     * @return the error message
     */
    public static BaseResponse<Object> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
