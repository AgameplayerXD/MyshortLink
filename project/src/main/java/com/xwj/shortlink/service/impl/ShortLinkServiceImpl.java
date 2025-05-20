package com.xwj.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.shortlink.common.convention.exception.ServiceException;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import com.xwj.shortlink.dao.mapper.ShortLinkMapper;
import com.xwj.shortlink.dto.req.ShortLinkProjectCreateReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkProjectPageReqDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectPageRespDTO;
import com.xwj.shortlink.service.ShortLinkService;
import com.xwj.shortlink.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> linkCreateCachePenetrationBloomFilter;

    /**
     * 完成短链接的创建，可以通过调用接口 api 来创建，也可以控制台手动进行创建
     *
     * @param requestParam
     * @return
     */
    @Override
    public ShortLinkProjectCreateRespDTO ShortLinkProjectCreate(ShortLinkProjectCreateReqDTO requestParam) {
        //通过原始链接来生成短链接
        //先生成 6 位的 Suffix
        String shortUri = generateSuffix(requestParam);
        String fullShortUrl = StrBuilder.create(requestParam.getDomain())
                .append("/")
                .append(shortUri)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .shortUri(shortUri)
                .fullShortUrl(fullShortUrl)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .build();
        try {
            baseMapper.insert(shortLinkDO);
        } catch (DuplicateKeyException e) {
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShortLinkDO::getOriginUrl, fullShortUrl);
            ShortLinkDO hasShortLink = baseMapper.selectOne(queryWrapper);
            if (!Objects.isNull(hasShortLink)) {
                //如果确实在数据库中查到了数据，就说明并不是布隆过滤器误判
                log.warn("短链接入库异常");
                throw new ServiceException("已经存在该短链接");
            }
        }
        linkCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return BeanUtil.copyProperties(shortLinkDO, ShortLinkProjectCreateRespDTO.class);
    }

    /**
     * 分页查询短链接
     *
     * @return
     */
    @Override
    public IPage<ShortLinkProjectPageRespDTO> ShortLinkProjectPage(ShortLinkProjectPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLinkDO::getGid, requestParam.getGid());
        queryWrapper.eq(ShortLinkDO::getEnableStatus, 0);
        queryWrapper.eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkProjectPageReqDTO shortLinkProjectPageReqDTO = baseMapper.selectPage(requestParam, queryWrapper);
        System.out.println(shortLinkProjectPageReqDTO.getRecords());
        return shortLinkProjectPageReqDTO.convert(shortLinkDO -> BeanUtil.toBean(shortLinkDO, ShortLinkProjectPageRespDTO.class));
    }

    /**
     * 根据传入的原始链接来创建短链接
     *
     * @param requestParam
     * @return
     */
    private String generateSuffix(ShortLinkProjectCreateReqDTO requestParam) {
        //检查短链接是否冲突
        int generateTimes = 0;
        String shortUri;
        while (true) {
            if (generateTimes > 10) {
                throw new ServiceException("短链接频繁创建，请稍后再试");
            }
            shortUri = HashUtil.hashToBase62(requestParam.getOriginUrl() + System.currentTimeMillis());
            //如果生成的短链接不冲突，则生成成功，跳出循环
            if (!linkCreateCachePenetrationBloomFilter.contains(StrBuilder.create(requestParam.getDomain())
                    .append("/")
                    .append(shortUri)
                    .toString())) {
                break;
            }
            //生成的短链接冲突，再生成一次
            generateTimes++;
        }
        return shortUri;
    }
}
