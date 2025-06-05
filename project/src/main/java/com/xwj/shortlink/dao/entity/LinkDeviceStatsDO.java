package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (LinkDeviceStats)表实体类
 *
 * @author makejava
 * @since 2025-06-05 20:52:31
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_link_device_stats")
public class LinkDeviceStatsDO {
    //ID@TableId
    private Long id;

    //完整短链接
    private String fullShortUrl;
    //日期
    private Date date;
    //访问量
    private Integer cnt;
    //访问设备
    private String device;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;
    //删除标识 0：未删除 1：已删除
    private Integer delFlag;


}

