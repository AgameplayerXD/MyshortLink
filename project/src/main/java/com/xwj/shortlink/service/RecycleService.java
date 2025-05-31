package com.xwj.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import com.xwj.shortlink.dto.req.RecycleBinListReqDTO;
import com.xwj.shortlink.dto.req.RecycleBinRecoverReqDTO;
import com.xwj.shortlink.dto.req.RecycleBinRemoveReqDTO;
import com.xwj.shortlink.dto.req.RecycleBinSaveReqDTO;
import com.xwj.shortlink.dto.resp.PageResultVO;
import com.xwj.shortlink.dto.resp.ShortLinkPageRespDTO;

public interface RecycleService extends IService<ShortLinkDO> {
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    PageResultVO<ShortLinkPageRespDTO> listRecycleBin(RecycleBinListReqDTO requestParam);

    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);
}
