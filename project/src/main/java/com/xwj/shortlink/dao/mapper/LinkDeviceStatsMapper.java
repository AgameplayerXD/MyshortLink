package com.xwj.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.shortlink.dao.entity.LinkDeviceStatsDO;
import org.apache.ibatis.annotations.Param;


/**
 * (LinkDeviceStats)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-05 20:52:31
 */
public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {
    void shortLInkDeviceStats(@Param("linkDeviceStats") LinkDeviceStatsDO requestParam);
}

