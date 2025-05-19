package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xwj.shortlink.common.databsase.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (TUser0)表实体类
 *
 * @author makejava
 * @since 2025-05-17 20:30:48
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class UserDo extends BaseDO {
    //ID@TableId
    private Long id;

    //用户名
    private String username;
    //密码
    private String password;
    //真实姓名
    private String realName;
    //手机号
    private String phone;
    //邮箱
    private String mail;
    //注销时间戳
    private Long deletionTime;


}

