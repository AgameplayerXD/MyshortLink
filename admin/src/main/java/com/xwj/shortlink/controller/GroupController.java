package com.xwj.shortlink.controller;

import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.common.convention.result.Results;
import com.xwj.shortlink.dto.req.GroupAddReqDTO;
import com.xwj.shortlink.dto.req.GroupModifyReqDTO;
import com.xwj.shortlink.dto.req.GroupSortReqDTO;
import com.xwj.shortlink.dto.resp.GroupRespDTO;
import com.xwj.shortlink.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理系统短链接分组控制层
 */
@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    /**
     * 新增短链接分组
     *
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> addShotLinkGroup(@RequestBody GroupAddReqDTO requestParam) {
        groupService.addShotLinkGroup(requestParam.getName());
        return Results.success();
    }

    /**
     * 查询当前用户所创建的短链接分组
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<GroupRespDTO>> listShotLinkGroup() {
        return Results.success(groupService.listShotLinkGroup());
    }

    /**
     * 修改当前用户所创建的短链接分组
     * @return
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> modifyShotLinkGroupName(@RequestBody GroupModifyReqDTO requestParam) {
        groupService.modifyShotLinkGroupName(requestParam);
        return Results.success();
    }

    /**
     * 对短链接分组进行排序
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<List<GroupRespDTO>> sortShotLinkGroup(@RequestBody List<GroupSortReqDTO> requestParam) {
        return Results.success(groupService.sortShotLinkGroup(requestParam));
    }

    /**
     * 删除当前组下的短链接
     *
     * @param gid
     * @return
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteShortLinkGroup(@RequestParam("gid") String gid){
        groupService.deleteShortLinkGroup(gid);
        return Results.success();
    }

}
