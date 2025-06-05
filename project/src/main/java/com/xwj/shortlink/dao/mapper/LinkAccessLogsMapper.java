package com.xwj.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.shortlink.dao.entity.LinkAccessLogsDO;
import com.xwj.shortlink.dao.entity.LinkAccessStatsDO;
import com.xwj.shortlink.dto.req.ShortLinkStatsReqDTO;


/**
 * (TLinkAccessLogs)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-03 20:31:01
 */
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {
    /**
     * 通过短链接来查找对应的pv、uv、uip监控数据
     *
     * @param requestParam
     * @return
     */
    LinkAccessStatsDO findPvUvUidStatsByShortLink(ShortLinkStatsReqDTO requestParam);
}

