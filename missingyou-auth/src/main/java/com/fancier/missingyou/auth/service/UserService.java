package com.fancier.missingyou.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fancier.missingyou.common.model.dto.user.UserQueryRequest;
import com.fancier.missingyou.common.model.entity.User;
import com.fancier.missingyou.common.model.vo.LoginUserVO;
import com.fancier.missingyou.common.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用户服务层接口
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request http request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @param request http request
     * @return response
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request http request
     * @return response
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request http request
     * @return response
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 获取当前登录用户 VO
     */
    UserVO getLoginUserVO(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user user
     * @return response
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request http request
     * @return response
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 用户分页查询
     */
    Page<User> pageQuery(UserQueryRequest userQueryRequest);

    /**
     * 分页获取用户封装列表
     */
    Page<UserVO> VOPageQuery(UserQueryRequest userQueryRequest);

    /**
     * 添加用户签到记录
     *
     */
    Boolean addUserSignIn(Long userId);

    /**
     * 获取用户某个年份的签到记录
     *
     * @param userId 用户 id
     * @param year   年份（为空表示当前年份）
     * @return 签到记录映射
     */
    List<Integer> getUserSignInRecord(long userId, Integer year);

}
