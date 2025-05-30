package com.xwj.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.shortlink.common.constant.RedisKeyConstant;
import com.xwj.shortlink.common.convention.exception.ClientException;
import com.xwj.shortlink.common.convention.exception.ServiceException;
import com.xwj.shortlink.common.enums.VailDateTypeEnum;
import com.xwj.shortlink.dao.entity.ShortLinkDO;
import com.xwj.shortlink.dao.entity.ShortLinkGotoDO;
import com.xwj.shortlink.dao.mapper.ShortLinkGotoMapper;
import com.xwj.shortlink.dao.mapper.ShortLinkMapper;
import com.xwj.shortlink.dto.req.ShortLinkProjectCreateReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkProjectPageReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkProjectUpdateReqDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCountLinkRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkProjectPageRespDTO;
import com.xwj.shortlink.service.ShortLinkService;
import com.xwj.shortlink.util.HashUtil;
import com.xwj.shortlink.util.ShortLinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> linkCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;


    /**
     * 完成短链接的创建，可以通过调用接口 api 来创建，也可以控制台手动进行创建
     *
     * @param requestParam
     * @return
     */
    @Override
    public ShortLinkProjectCreateRespDTO shortLinkProjectCreate(ShortLinkProjectCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        if (!originUrl.startsWith("http://") && !originUrl.startsWith("https://")) {
            originUrl = "http://" + originUrl;
        }
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
                .originUrl(originUrl)
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .describe(requestParam.getDescribe())
                .build();
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException e) {
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO hasShortLink = baseMapper.selectOne(queryWrapper);
            if (!Objects.isNull(hasShortLink)) {
                //如果确实在数据库中查到了数据，就说明并不是布隆过滤器误判
                log.warn("短链接入库异常");
                throw new ServiceException("已经存在该短链接");
            }
        }
        linkCreateCachePenetrationBloomFilter.add(fullShortUrl);
        // 做缓存预热
        stringRedisTemplate.opsForValue().set(String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, fullShortUrl),
                shortLinkDO.getOriginUrl(),
                ShortLinkUtil.getCacheValidTime(requestParam.getValidDate()),
                TimeUnit.MILLISECONDS);
        return BeanUtil.copyProperties(shortLinkDO, ShortLinkProjectCreateRespDTO.class);
    }

    /**
     * 分页查询短链接
     *
     * @return
     */
    @Override
    public IPage<ShortLinkProjectPageRespDTO> shortLinkProjectPage(ShortLinkProjectPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLinkDO::getGid, requestParam.getGid());
        queryWrapper.eq(ShortLinkDO::getEnableStatus, 0);
        queryWrapper.eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkProjectPageReqDTO shortLinkProjectPageReqDTO = baseMapper.selectPage(requestParam, queryWrapper);
        System.out.println(shortLinkProjectPageReqDTO.getRecords());
        return shortLinkProjectPageReqDTO.convert(shortLinkDO -> BeanUtil.toBean(shortLinkDO, ShortLinkProjectPageRespDTO.class));
    }

    /**
     * 查询当前分组下的短链接数量
     *
     * @param requestParam gid 的列表
     * @return ShortLinkProjectCountLinkRespDTO 列表其中包含了 gid 和当前分组下的短链接数量
     */
    @Override
    public List<ShortLinkProjectCountLinkRespDTO> countGroupLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("gid as gid ,count(*) as ShortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(maps, ShortLinkProjectCountLinkRespDTO.class);
    }


    //TODO 后期需要优化
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkProjectUpdateReqDTO requestParam) {
        //先查询所要修改的短链接是否存在
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLinkDO::getGid, requestParam.getOriginGid());
        queryWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
        queryWrapper.eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasLink = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(hasLink)) {
            throw new ClientException("短链接记录不存在");
        }
        //判断是否需要修改 gid，如果需要修改gid，那么则先删除在新增，否则可以直接进行修改
        if (Objects.equals(hasLink.getGid(), requestParam.getGid())) {
            //gid 相等，可以直接修改内容
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
            updateWrapper.eq(ShortLinkDO::getGid, requestParam.getGid());
            updateWrapper.eq(ShortLinkDO::getEnableStatus, 0);
            updateWrapper.set(Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);
            updateWrapper.set(ShortLinkDO::getGid, requestParam.getGid());
            updateWrapper.set(ShortLinkDO::getOriginUrl, requestParam.getOriginUrl());
            updateWrapper.set(ShortLinkDO::getDescribe, requestParam.getDescribe());
            updateWrapper.set(ShortLinkDO::getValidDateType, requestParam.getValidDateType());
            update(updateWrapper);
        } else {
            //删除link表里的数据
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
            updateWrapper.eq(ShortLinkDO::getGid, hasLink.getGid());
            updateWrapper.eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            //删除路由表里的数据
            LambdaQueryWrapper<ShortLinkGotoDO> gotoDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
            gotoDOLambdaQueryWrapper.eq(ShortLinkGotoDO::getId, hasLink.getGid());
            gotoDOLambdaQueryWrapper.eq(ShortLinkGotoDO::getFullShortUrl, hasLink.getFullShortUrl());
            shortLinkGotoMapper.delete(gotoDOLambdaQueryWrapper);
            //新增link表里的数据
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .domain(hasLink.getDomain())
                    .originUrl(requestParam.getOriginUrl())
                    .gid(requestParam.getGid())
                    .createdType(hasLink.getCreatedType())
                    .validDateType(requestParam.getValidDateType())
                    .describe(requestParam.getDescribe())
                    .shortUri(hasLink.getShortUri())
                    .enableStatus(hasLink.getEnableStatus())
                    .totalPv(hasLink.getTotalPv())
                    .totalUv(hasLink.getTotalUv())
                    .totalUip(hasLink.getTotalUip())
                    .fullShortUrl(hasLink.getFullShortUrl())
                    .delTime(0L)
                    .build();
            baseMapper.insert(shortLinkDO);
            //新增路由表里的数据
            ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                    .fullShortUrl(requestParam.getFullShortUrl())
                    .gid(requestParam.getGid())
                    .build();
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        }
    }

    /**
     * 实现短链接的跳转功能
     *
     * @param shortUri
     * @param request
     * @param response
     */
    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        String domain = request.getServerName();
        String fullShortUrl = (StrBuilder.create(domain).append("/").append(shortUri)).toString();
        String originUrl;
        if (!linkCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
            //如果布隆过滤器里没有，则说明短链接不存在
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }
        //需要跳转的网址在缓存中命中，直接跳转返回
        originUrl = stringRedisTemplate.opsForValue().get(String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(originUrl)) {
            ((HttpServletResponse) response).sendRedirect(originUrl);
            return;
        }
        //防止缓存穿透
        String gotoIsNull = stringRedisTemplate.opsForValue().get(String.format(RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoIsNull)) {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }
        //缓存击穿
        RLock lock = redissonClient.getLock(String.format(RedisKeyConstant.LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            //线程进入锁之后检查一下是不是其他线程已经将空缺的缓存补充
            originUrl = stringRedisTemplate.opsForValue().get(String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originUrl)) {
                ((HttpServletResponse) response).sendRedirect(originUrl);
                return;
            }
            gotoIsNull = stringRedisTemplate.opsForValue().get(String.format(RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(gotoIsNull)) {
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }
            //通过fullShortUrl去路由表中查询gid
            LambdaQueryWrapper<ShortLinkGotoDO> gotoDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
            gotoDOLambdaQueryWrapper.eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(gotoDOLambdaQueryWrapper);
            if (Objects.isNull(shortLinkGotoDO)) {
                //确实没有这个短链接,则将“null”存入Redis中，防止缓存穿透
                stringRedisTemplate.opsForValue().set(String.format(RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30L, TimeUnit.SECONDS);
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }
            //拿到gid后，去link表里查询原始链接
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            queryWrapper.eq(ShortLinkDO::getEnableStatus, 0);
            queryWrapper.eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid());
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            //如果shortLinkDO为空，或者短链接已经过期，则直接返回短链接不存在页面，并将“null”存入Redis
            if (Objects.isNull(shortLinkDO) || (shortLinkDO.getValidDate() != null && DateUtil.date().before(shortLinkDO.getValidDate()))) {
                stringRedisTemplate.opsForValue().set(String.format(RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30L, TimeUnit.SECONDS);
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }
            //数据库中存在短链接，只是key已经过期，将value存入Redis中
            originUrl = shortLinkDO.getOriginUrl();
            stringRedisTemplate.opsForValue().set(String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, fullShortUrl),
                    originUrl,
                    ShortLinkUtil.getCacheValidTime(shortLinkDO.getValidDate()),
                    TimeUnit.MILLISECONDS);
            ((HttpServletResponse) response).sendRedirect(originUrl);
        } finally {
            lock.unlock();
        }
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
