package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xwj.shortlink.common.databsase.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (LinkOsStats)表实体类
 *
 * @author makejava
 * @since 2025-06-03 19:37:25
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_link_os_stats")
public class LinkOsStatsDO extends BaseDO {
    //ID@TableId
    private Long id;

    //完整短链接
    private String fullShortUrl;
    //日期
    private Date date;
    //访问量
    private Integer cnt;
    //操作系统
    private String os;


}

