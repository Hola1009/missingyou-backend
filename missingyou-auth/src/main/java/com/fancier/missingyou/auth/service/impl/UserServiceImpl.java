package com.fancier.missingyou.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fancier.missingyou.common.constant.CommonConstant;
import com.fancier.missingyou.common.mapper.UserMapper;
import com.fancier.missingyou.common.model.dto.user.UserQueryRequest;
import com.fancier.missingyou.common.model.entity.User;
import com.fancier.missingyou.common.model.enums.UserRoleEnum;
import com.fancier.missingyou.common.model.vo.LoginUserVO;
import com.fancier.missingyou.auth.mapstruct.UserConvert;
import com.fancier.missingyou.auth.service.UserService;
import com.fancier.missingyou.auth.util.DeviceUtils;
import com.fancier.missingyou.common.model.vo.UserVO;
import com.fancier.missingyou.common.expection.BusinessException;
import com.fancier.missingyou.common.expection.ErrorCode;
import com.fancier.missingyou.common.expection.ThrowUtils;
import com.fancier.missingyou.common.util.SqlUtils;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Collectors;

import static com.fancier.missingyou.common.constant.UserConstant.SALT;
import static com.fancier.missingyou.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务层接口
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 参数校验
        Preconditions.checkArgument(!StringUtils.isAnyBlank(userAccount, userPassword, checkPassword),
                "参数为空");

        Preconditions.checkArgument(userAccount.length() >= 4,
                 "用户账号过短");

        Preconditions.checkArgument(userPassword.length() >= 8 && checkPassword.length() >= 8,
                 "用户密码过短");

        Preconditions.checkArgument(userPassword.equals(checkPassword),
                "两次输入的密码不一致");

        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);

            Preconditions.checkArgument(count <= 0, "账号重复");
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);

            Preconditions.checkArgument(saveResult, "注册失败，数据库错误");

            return user.getId();
        }
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request httpServletRequest
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 参数校验
        Preconditions.checkArgument(!StringUtils.isAnyBlank(userAccount, userPassword),
                 "参数为空");
        Preconditions.checkArgument(userAccount.length() >= 4,
                "用户账号过短");
        Preconditions.checkArgument(userPassword.length() >= 8,
                "用户密码过短");

        User user = this.baseMapper.checkUser(userAccount, userPassword);

        // 用户不存在
        Preconditions.checkArgument(user != null, "用户不存在或密码错误");

        // 3. 记录用户的登录态
        // 使用 Sa-Token 登录，并指定设备，同端登录互斥
        StpUtil.login(user.getId(), DeviceUtils.getRequestDevice(request));
        StpUtil.getSession().set(USER_LOGIN_STATE, user);
        // 转 LoginUserVO 脱敏
        return UserConvert.INSTANCE.DO2LoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request http request
     * @return result
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object loginUserId = StpUtil.getLoginIdDefaultNull();
        ThrowUtils.throwIf(loginUserId == null, new BusinessException(ErrorCode.NOT_LOGIN_ERROR));
        User currentUser = this.getById((String) loginUserId);

        ThrowUtils.throwIf(currentUser == null, new BusinessException(ErrorCode.NOT_LOGIN_ERROR));

        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request http request
     * @return Response
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录（基于 Sa-Token 实现）
        Object loginUserId = StpUtil.getLoginIdDefaultNull();
        if (loginUserId == null) {
            return null;
        }
        return this.getById((String) loginUserId);
    }

    /**
     * 是否为管理员
     *
     * @param request request
     * @return Response
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        // 基于 Sa-Token 改造
        User user  = (User)  StpUtil.getSession().get(USER_LOGIN_STATE);
        return isAdmin(user);
    }

    /**
     * 获取当前登录用户信息
     */
    @Override
    public UserVO getLoginUserVO(HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);
        return UserConvert.INSTANCE.DO2UserVO(loginUser);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request http request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        StpUtil.checkLogin();
        // 移除登录态
        StpUtil.logout();
        return true;
    }

    /**
     * 用户分页查询
     *
     */
    @Override
    public Page<User> pageQuery(UserQueryRequest userQueryRequest) {
        // 空参数检查
        Preconditions.checkArgument(userQueryRequest != null, "请求参数为空");

        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();

        return this.page(new Page<>(current, size),
                this.getQueryWrapper(userQueryRequest));
    }

    /**
     * 分页获取用户封装列表
     *
     */
    @Override
    public Page<UserVO> VOPageQuery(UserQueryRequest userQueryRequest) {
        // 空参数检查
        Preconditions.checkArgument(userQueryRequest != null, "请求参数为空");

        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Page<User> userPage = this.pageQuery(userQueryRequest);

        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());

        // DO 列表转 VO 列表
        List<UserVO> userVOS = userPage.getRecords().stream()
                .map(UserConvert.INSTANCE::DO2UserVO)
                .collect(Collectors.toList());

        return userVOPage.setRecords(userVOS);
    }

    /**
     * 组装 wrapper
     */
    private Wrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        // 空参数检查
        Preconditions.checkArgument(userQueryRequest != null, "请求参数为空");

        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String username = userQueryRequest.getUsername();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(id != null, User::getId, id)
                .eq(StringUtils.isNotBlank(unionId), User::getUnionId, unionId)
                .eq(StringUtils.isNotBlank(mpOpenId), User::getMpOpenId, mpOpenId)
                .eq(StringUtils.isNotBlank(userRole), User::getUserRole, userRole)
                .like(StringUtils.isNotBlank(userProfile), User::getUserProfile, userProfile)
                .like(StringUtils.isNotBlank(username), User::getUsername, username);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


}
