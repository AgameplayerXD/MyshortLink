package com.xwj.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.shortlink.dao.entity.LinkLocaleStatsDO;
import org.apache.ibatis.annotations.Param;


/**
 * (TLinkLocaleStats)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-03 15:20:58
 */
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {
    void shortLinkLocalState(@Param("linkLocaleStatsDO") LinkLocaleStatsDO linkLocaleStatsDO);
}

