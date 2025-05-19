package com.xwj.shortlink.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xwj.shortlink.common.serializer.PhoneDesensitizationSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户响应参数实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRespDTO {

    //用户名
    private String username;
    //真实姓名
    private String realName;
    //手机号
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;
    //邮箱
    private String mail;
}
