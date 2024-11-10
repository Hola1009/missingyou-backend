package com.fancier.missingyou.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user")
public class User {
    private Long id;

    private String userAccount;

    private String userPassword;

    private String unionId;

    private String mpOpenId;

    private String username;

    private String userAvatar;

    private String userProfile;

    private String userRole;

    private LocalDateTime editTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDelete;
}