package com.xwj.shortlink.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xwj.shortlink.common.biz.UserContext;
import com.xwj.shortlink.common.convention.exception.ClientException;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.dao.entity.GroupDO;
import com.xwj.shortlink.dao.mapper.GroupMapper;
import com.xwj.shortlink.remote.ShortLinkActualRemoteService;
import com.xwj.shortlink.remote.dto.req.RecycleBinListReqDTO;
import com.xwj.shortlink.remote.dto.resp.PageResultVO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkPageRespDTO;
import com.xwj.shortlink.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {
    private final GroupMapper groupMapper;
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    @Override
    public Result<PageResultVO<ShortLinkPageRespDTO>> pageBinShortLink(RecycleBinListReqDTO requestParam) {
        //通过当前登录用户去查询用户创建的所有gid
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getUsername, UserContext.getUsername());
        List<GroupDO> groupDOS = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOS)) {
            throw new ClientException("该用户没有创建分组");
        }
        List<String> gidList = groupDOS.stream()
                .map(GroupDO::getGid)
                .toList();
        requestParam.setGidList(gidList);
        return shortLinkActualRemoteService.listRecycleBin(requestParam.getGidList(),
                requestParam.getCurrent(),
                requestParam.getSize());
    }
}
