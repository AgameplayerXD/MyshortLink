package com.xwj.shortlink.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import com.xwj.shortlink.dto.req.ShortLinkCreateReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkUpdateReqDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCountLinkRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.util.List;

/**
 * 短链接业务接口
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接接口
     *
     * @param requestParam
     * @return
     */
    ShortLinkProjectCreateRespDTO shortLinkProjectCreate(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接接口
     * @return
     */
    Page<ShortLinkProjectPageRespDTO> shortLinkProjectPage(String gid, Long current, Long size);

    /**
     * 查询分组下的短链接数量接口
     * @param requestParam
     * @return
     */
    List<ShortLinkProjectCountLinkRespDTO> countGroupLinkCount(List<String> requestParam);

    /**
     * 修改短链接
     *
     * @param requestParam
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接跳转接口
     *
     * @param shortUri
     * @param request
     * @param response
     */
    void restoreUrl(String shortUri, ServletRequest request, ServletResponse response);
}
