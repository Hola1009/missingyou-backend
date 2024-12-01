package com.fancier.missingyou.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fancier.missingyou.common.model.entity.User;
import org.springframework.util.DigestUtils;

import static com.fancier.missingyou.common.constant.UserConstant.SALT;

public interface UserMapper extends BaseMapper<User> {
    int insert(User record);


    default User checkUser(String userAccount, String userPassword) {
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(StringUtils.isNotBlank(userAccount), User::getUserAccount, userAccount)
                .eq(StringUtils.isNotBlank(encryptPassword), User::getUserPassword, encryptPassword);

        return this.selectOne(wrapper);
    }
}