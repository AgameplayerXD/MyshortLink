package com.xwj.shortlink.dto.resp;

import lombok.Data;

/**
 * 查询当前用户所创建的短链接分组响应实体
 */
@Data
public class GroupRespDTO {
    //分组标识
    private String gid;
    //分组名称
    private String name;
    /**
     * 分组下短链接数量
     */
    private Integer shortLinkCount;
    //分组排序
    private Integer sortOrder;
}
