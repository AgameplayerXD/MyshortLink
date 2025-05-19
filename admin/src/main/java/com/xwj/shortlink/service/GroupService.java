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
    void addShotLinkGroup(String name);

    List<GroupRespDTO> listShotLinkGroup();

    void modifyShotLinkGroupName(GroupModifyReqDTO requestParam);

    List<GroupRespDTO> sortShotLinkGroup(List<GroupSortReqDTO> requestParam);

    void deleteShortLinkGroup(String gid);
}
