package com.xwj.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.common.convention.result.Results;
import com.xwj.shortlink.dto.req.ShortLinkProjectCreateReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkProjectPageReqDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCountLinkRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectPageRespDTO;
import com.xwj.shortlink.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<ShortLinkProjectCreateRespDTO> shortLinkProjectCreate(@RequestBody ShortLinkProjectCreateReqDTO requestParam) {
        return Results.success(shortLinkService.shortLinkProjectCreate(requestParam));
    }

    /**
     * 短链接分页查询接口
     *
     * @return
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkProjectPageRespDTO>> shortLinkProjectPage(ShortLinkProjectPageReqDTO requestParam) {
        return Results.success(shortLinkService.shortLinkProjectPage(requestParam));
    }

    /**
     * 查询分组下有多少短链接
     *
     * @param requestParam 分组 id 的列表
     * @return
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkProjectCountLinkRespDTO>> countGroupLinkCount(@RequestParam("requestParam") List<String> requestParam) {
        return Results.success(shortLinkService.countGroupLinkCount(requestParam));
    }
}
