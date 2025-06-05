package com.xwj.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.shortlink.dao.entity.LinkOsStatsDO;
import org.apache.ibatis.annotations.Param;


/**
 * (LinkOsStats)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-03 19:37:25
 */
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {
    void shortLinkOsState(@Param("linkOsStats") LinkOsStatsDO requestParam);
}

