package com.xwj.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import com.xwj.shortlink.dto.req.RecycleBinSaveReqDTO;

public interface RecycleService extends IService<ShortLinkDO> {
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);
}
