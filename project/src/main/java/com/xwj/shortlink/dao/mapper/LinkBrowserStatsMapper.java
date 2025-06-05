package com.xwj.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.shortlink.dao.entity.LinkBrowserStatsDO;
import org.apache.ibatis.annotations.Param;


/**
 * (LinkBrowserStats)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-05 20:27:52
 */
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {
    void shortLInkBrowserStats(@Param("linkBrowserStats") LinkBrowserStatsDO requestParam);

}

