package com.xwj.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.shortlink.dao.entity.LinkAccessLogs;
import com.xwj.shortlink.dao.entity.LinkAccessStatsDO;
import com.xwj.shortlink.dto.req.ShortLinkStatsReqDTO;


/**
 * (TLinkAccessLogs)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-03 20:31:01
 */
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogs> {

    LinkAccessStatsDO findPvUvUidStatsByShortLink(ShortLinkStatsReqDTO requestParam);
}

