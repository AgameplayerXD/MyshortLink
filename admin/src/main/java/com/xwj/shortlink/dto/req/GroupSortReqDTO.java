package com.xwj.shortlink.dto.req;

import lombok.Data;

@Data
public class GroupSortReqDTO {
    /**
     * 分组 Id
     */
    private String gid;
    /**
     * 分组排序
     */
    private Integer sortOrder;

}

