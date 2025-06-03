package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xwj.shortlink.common.databsase.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (TLinkLocaleStats)表实体类
 *
 * @author makejava
 * @since 2025-06-03 15:20:58
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_link_locale_stats")
public class LinkLocaleStatsDO extends BaseDO {
    //ID@TableId
    private Long id;

    //完整短链接
    private String fullShortUrl;
    //日期
    private Date date;
    //访问量
    private Integer cnt;
    //省份名称
    private String province;
    //市名称
    private String city;
    //城市编码
    private String adcode;
    //国家标识
    private String country;

}

