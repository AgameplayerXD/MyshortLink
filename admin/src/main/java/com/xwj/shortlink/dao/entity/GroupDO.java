package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xwj.shortlink.common.databsase.BaseDO;
import lombok.*;

/**
 * (Group)表实体类
 *
 * @author makejava
 * @since 2025-05-19 13:18:59
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_group")
public class GroupDO extends BaseDO {
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


}

