package com.xwj.shortlink.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 短链接分页查询请求实体类
 */
@Data
public class ShortLinkProjectPageReqDTO extends Page {
    /**
     * 分组 ID
     */
    private String gid;

}
