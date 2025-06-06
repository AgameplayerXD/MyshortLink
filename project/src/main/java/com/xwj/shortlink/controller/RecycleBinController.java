package com.xwj.shortlink.controller;

import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.common.convention.result.Results;
import com.xwj.shortlink.dto.req.RecycleBinListReqDTO;
import com.xwj.shortlink.dto.req.RecycleBinRecoverReqDTO;
import com.xwj.shortlink.dto.req.RecycleBinRemoveReqDTO;
import com.xwj.shortlink.dto.req.RecycleBinSaveReqDTO;
import com.xwj.shortlink.dto.resp.PageResultVO;
import com.xwj.shortlink.dto.resp.ShortLinkPageRespDTO;
import com.xwj.shortlink.service.RecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleService recycleService;

    /**
     * 将短链接保存至回收站
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站
     *
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<PageResultVO<ShortLinkPageRespDTO>> listRecycleBin(RecycleBinListReqDTO requestParam) {
        return Results.success(recycleService.listRecycleBin(requestParam));
    }

    /**
     * 恢复短链接
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        recycleService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 移除短链接
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        recycleService.removeRecycleBin(requestParam);
        return Results.success();
    }
}
