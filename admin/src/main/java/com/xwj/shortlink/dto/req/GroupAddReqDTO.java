package com.xwj.shortlink.dto.req;

import lombok.Data;

/**
 * 增加短链接分组请求参数
 */
@Data
public class GroupAddReqDTO {
    /**
     * 短链接分组名
     */
    private String name;
}
