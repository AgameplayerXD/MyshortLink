package com.xwj.shortlink.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.common.convention.result.Results;
import com.xwj.shortlink.remote.ShortLinkActualRemoteService;
import com.xwj.shortlink.remote.dto.req.ShortLinkCreateReqDTO;
import com.xwj.shortlink.remote.dto.req.ShortLinkUpdateReqDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkCreateRespDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Result<Page<ShortLinkPageRespDTO>> shortLinkRemotePage(@RequestParam("gid") String gid, @RequestParam("current") Long current, @RequestParam("size") Long size) {
        return shortLinkActualRemoteService.shortLinkProjectPage(gid, current, size);
    }

    /**
     * 调用中台来创建短链接
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> shortLinkRemoteCreate(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkActualRemoteService.shortLinkProjectCreate(requestParam);
    }

    /**
     * 调用中台修改短链接
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> shortLinkRemoteUpdate(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkActualRemoteService.updateShortLink(requestParam);
        return Results.success();
    }

}
