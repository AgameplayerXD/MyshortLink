package com.xwj.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.shortlink.dao.entity.LinkAccessStatsDO;
import org.apache.ibatis.annotations.Param;


/**
 * (TLinkAccessStats)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-02 16:59:57
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {
    /**
     * 想数据库中插入短链接基础统计信息
     * 通过gid和full short link URL来定位短链接
     *
     * @param linkAccessStats
     */
    void shortLinkStats(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStats);
}

