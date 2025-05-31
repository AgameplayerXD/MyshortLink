package com.xwj.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.common.convention.result.Results;
import com.xwj.shortlink.remote.ShortLinkActualRemoteService;
import com.xwj.shortlink.remote.dto.req.ShortLinkRemoteCreateReqDTO;
import com.xwj.shortlink.remote.dto.req.ShortLinkRemoteUpdateReqDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkProjectPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接后管调用中台控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkRemoteController {
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    /**
     * 调用中台来进行短链接分页查询
     *
     * @return
     */

    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkProjectPageRespDTO>> shortLinkRemotePage(String gid, Long current, Long pageSize) {
        return shortLinkActualRemoteService.shortLinkProjectPage(gid, current, pageSize);
    }

    /**
     * 调用中台来创建短链接
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkProjectCreateRespDTO> shortLinkRemoteCreate(@RequestBody ShortLinkRemoteCreateReqDTO requestParam) {
        return shortLinkActualRemoteService.shortLinkProjectCreate(requestParam);
    }

    /**
     * 调用中台修改短链接
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> shortLinkRemoteUpdate(@RequestBody ShortLinkRemoteUpdateReqDTO requestParam) {
        shortLinkActualRemoteService.updateShortLink(requestParam);
        return Results.success();
    }

}
