package com.xwj.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.shortlink.common.biz.UserContext;
import com.xwj.shortlink.common.convention.exception.ClientException;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.dao.entity.GroupDO;
import com.xwj.shortlink.dao.mapper.GroupMapper;
import com.xwj.shortlink.dto.req.GroupModifyReqDTO;
import com.xwj.shortlink.dto.req.GroupSortReqDTO;
import com.xwj.shortlink.dto.resp.GroupRespDTO;
import com.xwj.shortlink.remote.ShortLinkRemoteService;
import com.xwj.shortlink.remote.dto.resp.ShortLinkRemoteCountLinkRespDTO;
import com.xwj.shortlink.service.GroupService;
import com.xwj.shortlink.util.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 短链接分组业务实现层
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

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
     * 用于注册时新增短链接分组，需要手动指定用户名，无法从登录上下文中获取
     *
     * @param username
     * @param groupName
     */
    @Override
    public void addShotLinkGroup(String username, String groupName) {
        String gid = RandomGenerator.generateRandom();
        GroupDO group = GroupDO.builder()
                .gid(gid)
                .sortOrder(0)
                .name(groupName)
                .username(username)
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
        //得到当前用户所创建的所有分组的 gid 列表
        List<String> gidList = groupDOS.stream()
                .map(GroupDO::getGid)
                .toList();
        //调用中台的查询 gid 下短链接数量的方法，该方法返回 gid 和对应的短链接数量
        Result<List<ShortLinkRemoteCountLinkRespDTO>> listResult = shortLinkRemoteService.countGroupLinkCount(gidList);
        List<ShortLinkRemoteCountLinkRespDTO> gidAndCountList = listResult.getData();
        //填充 GroupRespDTO 响应对象中空缺的短链接数量字段
        List<GroupRespDTO> groupRespDTOS = BeanUtil.copyToList(groupDOS, GroupRespDTO.class);
        groupRespDTOS.forEach(each -> {
            Optional<ShortLinkRemoteCountLinkRespDTO> first = listResult.getData().stream()
                    .filter(item -> Objects.equals(each.getGid(), item.getGid()))
                    .findFirst();
            first.ifPresent(item -> each.setShortLinkCount(item.getShortLinkCount()));
        });
        return groupRespDTOS;
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
