package com.xwj.shortlink.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.dto.req.RecycleBinSaveReqDTO;
import com.xwj.shortlink.remote.dto.req.ShortLinkCreateReqDTO;
import com.xwj.shortlink.remote.dto.req.ShortLinkUpdateReqDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkCreateRespDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkPageRespDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkCountLinkRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign客户端调用中台服务
 */
@FeignClient("short-link-project")
public interface ShortLinkActualRemoteService {
    /**
     * 分页查询短链接
     *
     * @param gid
     * @param current
     * @return
     */
    @GetMapping("/api/short-link/v1/page")
    Result<Page<ShortLinkPageRespDTO>> shortLinkProjectPage(@RequestParam("gid") String gid, @RequestParam("current") Long current, @RequestParam("size") Long size);

    /**
     * 创建短链接
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/create")
    Result<ShortLinkCreateRespDTO> shortLinkProjectCreate(@RequestBody ShortLinkCreateReqDTO requestParam);

    /**
     * 查询分组下短链接的数量
     *
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/count")
    Result<List<ShortLinkCountLinkRespDTO>> countGroupLinkCount(@RequestParam("requestParam") List<String> requestParam);

    /**
     * 修改短链接
     *
     * @param requestParam
     */
    @PostMapping("/api/short-link/v1/update")
    void updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam);

    /**
     * 将短链接移至回收站
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam);
}
