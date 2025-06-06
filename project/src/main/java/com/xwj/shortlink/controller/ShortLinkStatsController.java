package com.xwj.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.common.convention.result.Results;
import com.xwj.shortlink.dto.req.ShortLinkGroupStatsReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkStatsReqDTO;
import com.xwj.shortlink.dto.resp.ShortLinkStatsRespDTO;
import com.xwj.shortlink.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {
    private final ShortLinkStatsService shortLinkStatsService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.oneShortLinkStats(requestParam));
    }

//    /**
//     * 访问分组短链接指定时间内监控数据
//     */
//    @GetMapping("/api/short-link/v1/stats/group")
//    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
//        return Results.success(shortLinkStatsService.groupShortLinkStats(requestParam));
//    }
//
//    /**
//     * 访问单个短链接指定时间内访问记录监控数据
//     */
//    @GetMapping("/api/short-link/v1/stats/access-record")
//    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
//        return Results.success(shortLinkStatsService.shortLinkStatsAccessRecord(requestParam));
//    }
//
//    /**
//     * 访问分组短链接指定时间内访问记录监控数据
//     */
//    @GetMapping("/api/short-link/v1/stats/access-record/group")
//    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
//        return Results.success(shortLinkStatsService.groupShortLinkStatsAccessRecord(requestParam));
//    }
}
