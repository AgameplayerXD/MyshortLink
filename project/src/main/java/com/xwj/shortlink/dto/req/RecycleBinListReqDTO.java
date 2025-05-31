package com.xwj.shortlink.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import lombok.Data;

import java.util.List;

@Data
public class RecycleBinListReqDTO extends Page<ShortLinkDO> {
    private List<String> gidList;
}
