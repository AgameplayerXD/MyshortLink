package com.xwj.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.shortlink.common.biz.UserContext;
import com.xwj.shortlink.common.convention.exception.ClientException;
import com.xwj.shortlink.dao.entity.GroupDO;
import com.xwj.shortlink.dao.mapper.GroupMapper;
import com.xwj.shortlink.dto.req.GroupModifyReqDTO;
import com.xwj.shortlink.dto.req.GroupSortReqDTO;
import com.xwj.shortlink.dto.resp.GroupRespDTO;
import com.xwj.shortlink.service.GroupService;
import com.xwj.shortlink.util.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 短链接分组业务实现层
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    /**
     * 添加短链接分组
     *
     * @param groupName 分组名字
     */
    @Override
    public void addShotLinkGroup(String groupName) {
        String gid = RandomGenerator.generateRandom();
        GroupDO group = GroupDO.builder()
                .gid(gid)
                .sortOrder(0)
                .name(groupName)
                .username(UserContext.getUsername())
                .build();
        save(group);
    }

    /**
     * 根据登录的用户来查询其创建的短链接分组
     *
     * @return
     */
    @Override
    public List<GroupRespDTO> listShotLinkGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getUsername, UserContext.getUsername());
        queryWrapper.eq(GroupDO::getDelFlag, 0);
        queryWrapper.orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = list(queryWrapper);
        return BeanUtil.copyToList(groupDOS, GroupRespDTO.class);
    }

    /**
     * 修改短链接分组名
     *
     * @param requestParam
     */
    @Override
    public void modifyShotLinkGroupName(GroupModifyReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GroupDO::getUsername, UserContext.getUsername());
        updateWrapper.eq(GroupDO::getGid, requestParam.getGid());
        updateWrapper.eq(GroupDO::getDelFlag, 0);
        updateWrapper.set(GroupDO::getName, requestParam.getName());
        update(updateWrapper);
    }

    /**
     * 根据前端传入的sort order 字段来对对应gid分组的短链接设置排序优先级
     *
     * @param requestParam
     * @return
     */
    @Override
    public List<GroupRespDTO> sortShotLinkGroup(List<GroupSortReqDTO> requestParam) {
        List<GroupDO> groupDOS = requestParam.stream()
                .map(groupSortReqDTO -> {
                    LambdaUpdateWrapper<GroupDO> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(GroupDO::getGid, groupSortReqDTO.getGid());
                    updateWrapper.eq(GroupDO::getDelFlag, 0);
                    GroupDO groupDO = getOne(updateWrapper);
                    updateWrapper.set(GroupDO::getSortOrder, groupSortReqDTO.getSortOrder());
                    update(updateWrapper);
                    return groupDO;
                })
                .toList();
        return BeanUtil.copyToList(groupDOS, GroupRespDTO.class);
    }

    @Override
    public void deleteShortLinkGroup(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getGid, gid);
        queryWrapper.eq(GroupDO::getDelFlag, 0);
        if (remove(queryWrapper)) {
            return;
        } else {
            throw new ClientException("该分组已被删除或者分组 ID 错误");
        }
    }
}
