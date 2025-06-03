package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (TLinkAccessLogs)表实体类
 *
 * @author makejava
 * @since 2025-06-03 20:31:01
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_access_logs")
public class LinkAccessLogs {
    //ID@TableId
    private Long id;

    //完整短链接
    private String fullShortUrl;
    //用户信息
    private String user;
    //IP
    private String ip;
    //浏览器
    private String browser;
    //操作系统
    private String os;
    //访问网络
    private String network;
    //访问设备
    private String device;
    //地区
    private String locale;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;
    //删除标识 0：未删除 1：已删除
    private Integer delFlag;


}

