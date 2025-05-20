package com.xwj.shortlink.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import lombok.Data;

/**
 * 短链接分页查询请求实体类
 */
@Data
public class ShortLinkProjectPageReqDTO extends Page<ShortLinkDO> {
    /**
     * 分组 ID
     */
    private String gid;

}
