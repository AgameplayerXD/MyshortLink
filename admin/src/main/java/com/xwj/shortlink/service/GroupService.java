package com.xwj.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.shortlink.dao.entity.GroupDO;
import com.xwj.shortlink.dto.req.GroupModifyReqDTO;
import com.xwj.shortlink.dto.req.GroupSortReqDTO;
import com.xwj.shortlink.dto.resp.GroupRespDTO;

import java.util.List;

/**
 * 短链接分组业务接口
 */
public interface GroupService extends IService<GroupDO> {
    /**
     * 当前登录用户新增短链接分组
     *
     * @param name
     */
    void addShotLinkGroup(String name);

    /**
     * 注册时候新增短链接分组，需要手动来传入用户名
     *
     * @param username
     * @param name
     */
    void addShotLinkGroup(String username, String name);

    /**
     * 查询短链接分组
     * @return
     */
    List<GroupRespDTO> listShotLinkGroup();

    /**
     * 修改分组的名字
     * @param requestParam
     */
    void modifyShotLinkGroupName(GroupModifyReqDTO requestParam);

    /**
     * 对分组进行排序
     * @param requestParam
     * @return
     */
    List<GroupRespDTO> sortShotLinkGroup(List<GroupSortReqDTO> requestParam);

    /**
     * 删除短链接分组
     * @param gid
     */
    void deleteShortLinkGroup(String gid);
}
