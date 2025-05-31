package com.xwj.shortlink.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkProjectCreateReqDTO {

    //域名
    private String domain;

    //原始链接
    private String originUrl;

    //分组标识
    private String gid;

    //创建类型 0：接口创建 1：控制台创建
    private Integer createdType;
    //创建类型 0：启用 1：未启用
    private Integer enableStatus;
    //有效期类型 0：永久有效 1：自定义
    private Integer validDateType;
    //有效期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;
    //描述
    private String describe;
}
