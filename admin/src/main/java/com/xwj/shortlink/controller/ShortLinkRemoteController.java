package com.xwj.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.remote.ShortLinkRemoteService;
import com.xwj.shortlink.remote.dto.req.ShortLinkProjectCreateReqDTO;
import com.xwj.shortlink.remote.dto.req.ShortLinkProjectPageReqDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkProjectPageRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接后管调用中台控制层
 */
@RestController
public class ShortLinkRemoteController {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 调用中台来进行短链接分页查询
     *
     * @param requestParam
     * @return
     */

    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkProjectPageRespDTO>> shortLinkRemotePage(ShortLinkProjectPageReqDTO requestParam) {
        return shortLinkRemoteService.ShortLinkProjectPage(requestParam);
    }

    /**
     * 调用中台来创建短链接
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkProjectCreateRespDTO> shortLinkRemoteCreate(@RequestBody ShortLinkProjectCreateReqDTO requestParam) {
        return shortLinkRemoteService.ShortLinkProjectCreate(requestParam);
    }

}
