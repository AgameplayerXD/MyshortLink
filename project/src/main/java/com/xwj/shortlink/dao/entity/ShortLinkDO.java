package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xwj.shortlink.common.databsase.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (Link)表实体类
 *
 * @author makejava
 * @since 2025-05-20 14:18:17
 */
@SuppressWarnings("serial")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link")
public class ShortLinkDO extends BaseDO {
    //ID@TableId
    private Long id;

    //域名
    private String domain;
    //短链接
    private String shortUri;
    //完整短链接
    private String fullShortUrl;
    //原始链接
    private String originUrl;
    //点击量
    private Integer clickNum;
    //分组标识
    private String gid;
    //网站图标
    private String favicon;
    //启用标识 0：启用 1：未启用
    private Integer enableStatus;
    //创建类型 0：接口创建 1：控制台创建
    private Integer createdType;
    //有效期类型 0：永久有效 1：自定义
    private Integer validDateType;
    //有效期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;
    //描述
    @TableField("`describe`")
    private String describe;
    //历史PV
    private Integer totalPv;
    //历史UV
    private Integer totalUv;
    //历史UIP
    private Integer totalUip;

    //删除时间戳
    private Long delTime;


}

