package com.xwj.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.shortlink.common.constant.RedisKeyConstant;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import com.xwj.shortlink.dao.mapper.ShortLinkMapper;
import com.xwj.shortlink.dto.req.RecycleBinSaveReqDTO;
import com.xwj.shortlink.service.RecycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecycleServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleService {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 将短链接移至回收站
     *
     * @param requestParam 包含gid和full short URL
     */
    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShortLinkDO::getGid, requestParam.getGid());
        updateWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
        updateWrapper.eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO saveBinDO = ShortLinkDO.builder()
                .enableStatus(1)
                .build();
        baseMapper.update(saveBinDO, updateWrapper);
        stringRedisTemplate.delete(String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
    }
}
