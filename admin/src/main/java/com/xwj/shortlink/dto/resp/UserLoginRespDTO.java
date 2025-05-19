package com.xwj.shortlink.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录接口响应参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRespDTO {
    private String token;
}
