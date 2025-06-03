package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xwj.shortlink.common.databsase.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (TLinkAccessStats)表实体类
 * 统计用户基础访问数据实体类
 *
 * @author makejava
 * @since 2025-06-02 16:59:57
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_link_access_stats")
public class LinkAccessStatsDO extends BaseDO {
    //ID@TableId
    private Long id;

    //完整短链接
    private String fullShortUrl;
    //日期
    private Date date;
    //访问量
    private Integer pv;
    //独立访客数
    private Integer uv;
    //独立IP数
    private Integer uip;
    //小时
    private Integer hour;
    //星期
    private Integer weekday;


}

