package com.xwj.shortlink.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户响应参数实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActualUserRespDTO {

    //用户名
    private String username;
    //真实姓名
    private String realName;
    //手机号
    private String phone;
    //邮箱
    private String mail;
}
