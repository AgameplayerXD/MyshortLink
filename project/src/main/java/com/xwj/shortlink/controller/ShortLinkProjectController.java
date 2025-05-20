package com.xwj.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.common.convention.result.Results;
import com.xwj.shortlink.dto.req.ShortLinkProjectCreateReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkProjectPageReqDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectPageRespDTO;
import com.xwj.shortlink.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 中台管理短链接控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkProjectController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接接口
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkProjectCreateRespDTO> ShortLinkProjectCreate(@RequestBody ShortLinkProjectCreateReqDTO requestParam) {
        return Results.success(shortLinkService.ShortLinkProjectCreate(requestParam));
    }

    /**
     * 短链接分页查询接口
     *
     * @return
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkProjectPageRespDTO>> ShortLinkProjectPage(ShortLinkProjectPageReqDTO requestParam) {
        return Results.success(shortLinkService.ShortLinkProjectPage(requestParam));
    }
}
