package com.xwj.shortlink.controller;

import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.common.convention.result.Results;
import com.xwj.shortlink.dto.req.RecycleBinSaveReqDTO;
import com.xwj.shortlink.remote.ShortLinkActualRemoteService;
import com.xwj.shortlink.remote.dto.req.RecycleBinListReqDTO;
import com.xwj.shortlink.remote.dto.resp.PageResultVO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkPageRespDTO;
import com.xwj.shortlink.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;
    private final RecycleBinService recycleBinService;

    /**
     * 后管调用中台来将短链接移至回收站
     *
     * @param requestParam 包含gid和full short URL
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        shortLinkActualRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询用户的回收站
     *
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<PageResultVO<ShortLinkPageRespDTO>> pageBinShortLink(RecycleBinListReqDTO requestParam) {
        return Results.success(recycleBinService.pageBinShortLink(requestParam));
    }
}
