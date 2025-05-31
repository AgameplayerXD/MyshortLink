package com.xwj.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.shortlink.common.constant.RedisKeyConstant;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import com.xwj.shortlink.dao.mapper.ShortLinkMapper;
import com.xwj.shortlink.dto.req.RecycleBinListReqDTO;
import com.xwj.shortlink.dto.req.RecycleBinRecoverReqDTO;
import com.xwj.shortlink.dto.req.RecycleBinRemoveReqDTO;
import com.xwj.shortlink.dto.req.RecycleBinSaveReqDTO;
import com.xwj.shortlink.dto.resp.PageResultVO;
import com.xwj.shortlink.dto.resp.ShortLinkPageRespDTO;
import com.xwj.shortlink.service.RecycleService;
import com.xwj.shortlink.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.xwj.shortlink.common.constant.RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY;

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

    /**
     * 分页查询回收站
     *
     * @param requestParam 包含用户所创建的gid列表
     */
    @Override
    public PageResultVO<ShortLinkPageRespDTO> listRecycleBin(RecycleBinListReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ShortLinkDO::getGid, requestParam.getGidList());
        queryWrapper.eq(ShortLinkDO::getEnableStatus, 1);
        Page<ShortLinkDO> page = new Page<>();
        page(page, queryWrapper);
        Page<ShortLinkPageRespDTO> results = PageUtil.convert(page, sourcePage -> BeanUtil.toBean(sourcePage, ShortLinkPageRespDTO.class));
        PageResultVO<ShortLinkPageRespDTO> pageRespDTOPageResultVO = new PageResultVO<>();
        pageRespDTOPageResultVO.setCurrent(requestParam.getCurrent());
        pageRespDTOPageResultVO.setTotal(results.getTotal());
        pageRespDTOPageResultVO.setSize(requestParam.getSize());
        pageRespDTOPageResultVO.setRecords(results.getRecords());
        return pageRespDTOPageResultVO;
    }

    /**
     * 从回收站恢复短链接
     *
     * @param requestParam 包含gid和full short URL
     */
    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShortLinkDO::getEnableStatus, 1);
        updateWrapper.eq(ShortLinkDO::getGid, requestParam.getGid());
        updateWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
        ShortLinkDO recoverLink = ShortLinkDO.builder()
                .enableStatus(0)
                .build();
        baseMapper.update(recoverLink, updateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
    }

    /**
     * 从回收站彻底删除短链接
     *
     * @param requestParam 包含gid和full short URL
     */
    @Override
    public void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
        queryWrapper.eq(ShortLinkDO::getGid, requestParam.getGid());
        queryWrapper.eq(ShortLinkDO::getEnableStatus, 1);
        baseMapper.delete(queryWrapper);
    }
}
