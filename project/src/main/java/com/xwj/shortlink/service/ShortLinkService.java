package com.xwj.shortlink.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import com.xwj.shortlink.dto.req.ShortLinkProjectCreateReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkProjectPageReqDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectPageRespDTO;

/**
 * 短链接业务接口
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkProjectCreateRespDTO ShortLinkProjectCreate(ShortLinkProjectCreateReqDTO requestParam);

    IPage<ShortLinkProjectPageRespDTO> ShortLinkProjectPage(ShortLinkProjectPageReqDTO requestParam);
}
