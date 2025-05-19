package com.xwj.shortlink.dto.req;

import lombok.Data;

/**
 * 修改短链接分组请求参数实体类
 */
@Data
public class GroupModifyReqDTO {
    /**
     * 分组id
     */
    private String gid;
    /**
     * 分组名
     */
    private String name;
}
