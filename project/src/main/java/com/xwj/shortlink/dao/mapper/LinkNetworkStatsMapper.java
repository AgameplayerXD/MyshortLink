package com.xwj.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.shortlink.dao.entity.LinkNetworkStatsDO;
import org.apache.ibatis.annotations.Param;


/**
 * (TLinkNetworkStats)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-05 21:20:58
 */
public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {
    void shortLinkNetworkStats(@Param("linkNetworkStats") LinkNetworkStatsDO requestParam);
}

