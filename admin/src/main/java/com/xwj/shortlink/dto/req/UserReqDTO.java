package com.xwj.shortlink.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户功能请求参数
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReqDTO {

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
}
