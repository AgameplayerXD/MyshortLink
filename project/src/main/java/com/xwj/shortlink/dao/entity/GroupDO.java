package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (Group0)表实体类
 *
 * @author makejava
 * @since 2025-06-03 20:10:03
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_group")
public class GroupDO {
    //ID@TableId
    private Long id;

    //分组标识
    private String gid;
    //分组名称
    private String name;
    //创建分组用户名
    private String username;
    //分组排序
    private Integer sortOrder;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;
    //删除标识 0：未删除 1：已删除
    private Integer delFlag;


}

