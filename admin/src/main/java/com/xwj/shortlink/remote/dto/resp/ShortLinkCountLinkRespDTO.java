package com.xwj.shortlink.remote.dto.resp;

import lombok.Data;

/**
 * 查询当前分组下有多少短链接的响应实体
 */
@Data
public class ShortLinkCountLinkRespDTO {
    /**
     * 分组 id
     */
    private String gid;
    /**
     * 当前分组下的短链接数量
     */
    private Integer ShortLinkCount;
}
