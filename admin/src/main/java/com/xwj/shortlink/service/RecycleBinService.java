package com.xwj.shortlink.service;

import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.remote.dto.req.RecycleBinListReqDTO;
import com.xwj.shortlink.remote.dto.resp.PageResultVO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkPageRespDTO;

/**
 * 后管回收站功能接口层
 */
public interface RecycleBinService {
    Result<PageResultVO<ShortLinkPageRespDTO>> pageBinShortLink(RecycleBinListReqDTO requestParam);
}
