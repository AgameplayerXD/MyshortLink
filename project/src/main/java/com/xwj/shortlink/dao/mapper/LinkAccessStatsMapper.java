package com.xwj.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.shortlink.dao.entity.LinkAccessStatsDO;
import com.xwj.shortlink.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


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

    /**
     * 根据短链接获取指定日期内的基础监控数据
     */
    List<LinkAccessStatsDO> listStatsByShortLink(@Param("Param") ShortLinkStatsReqDTO requestParam);
}

