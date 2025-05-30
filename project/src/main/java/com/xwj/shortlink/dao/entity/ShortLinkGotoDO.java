package com.xwj.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_goto")
public class ShortLinkGotoDO {
    /**
     * Id
     */
    private Long id;
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 完整短链接：domain+suffix
     */
    private String fullShortUrl;
}
