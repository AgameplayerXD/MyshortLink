package com.xwj.shortlink.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import com.xwj.shortlink.dto.req.ShortLinkProjectCreateReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkProjectPageReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkProjectUpdateReqDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCountLinkRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectPageRespDTO;

import java.util.List;

/**
 * 短链接业务接口
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkProjectCreateRespDTO shortLinkProjectCreate(ShortLinkProjectCreateReqDTO requestParam);

    IPage<ShortLinkProjectPageRespDTO> shortLinkProjectPage(ShortLinkProjectPageReqDTO requestParam);

    List<ShortLinkProjectCountLinkRespDTO> countGroupLinkCount(List<String> requestParam);

    /**
     * 修改短链接
     *
     * @param requestParam
     */
    void updateShortLink(ShortLinkProjectUpdateReqDTO requestParam);
}
