package com.xwj.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.shortlink.common.constant.RedisKeyConstant;
import com.xwj.shortlink.common.constant.SystemConstants;
import com.xwj.shortlink.common.convention.exception.ClientException;
import com.xwj.shortlink.common.convention.exception.ServiceException;
import com.xwj.shortlink.common.enums.VailDateTypeEnum;
import com.xwj.shortlink.dao.entity.*;
import com.xwj.shortlink.dao.mapper.*;
import com.xwj.shortlink.dto.req.ShortLinkCreateReqDTO;
import com.xwj.shortlink.dto.req.ShortLinkUpdateReqDTO;
import com.xwj.shortlink.dto.resp.ShortLinkCountLinkRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkCreateRespDTO;
import com.xwj.shortlink.dto.resp.ShortLinkPageRespDTO;
import com.xwj.shortlink.service.ShortLinkService;
import com.xwj.shortlink.util.HashUtil;
import com.xwj.shortlink.util.LinkStatsUtil;
import com.xwj.shortlink.util.PageUtil;
import com.xwj.shortlink.util.ShortLinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.xwj.shortlink.common.constant.RedisKeyConstant.SHORT_LINK_STATS_UIP_KEY;
import static com.xwj.shortlink.common.constant.RedisKeyConstant.SHORT_LINK_STATS_UV_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final RedissonClient redissonClient;
    private final RBloomFilter<String> linkCreateCachePenetrationBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;

    @Value("${short-link.stats.local.amap-key}")
    private String mapApiKey;


    /**
     * 完成短链接的创建，可以通过调用接口 api 来创建，也可以控制台手动进行创建
     *
     * @param requestParam
     * @return
     */
    @Override
    public ShortLinkCreateRespDTO shortLinkProjectCreate(ShortLinkCreateReqDTO requestParam) {
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
                .enableStatus(requestParam.getEnableStatus())
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
        return BeanUtil.copyProperties(shortLinkDO, ShortLinkCreateRespDTO.class);
    }

    /**
     * 分页查询短链接
     *
     * @return
     */
    @Override
    public Page<ShortLinkPageRespDTO> shortLinkProjectPage(String gid, Long current, Long size) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLinkDO::getGid, gid);
        queryWrapper.eq(ShortLinkDO::getEnableStatus, 0);
        Page<ShortLinkDO> page = new Page<>();
        page(page, queryWrapper);
        return PageUtil.convert(page, source -> BeanUtil.copyProperties(source, ShortLinkPageRespDTO.class));
    }

    /**
     * 查询当前分组下的短链接数量
     *
     * @param requestParam gid 的列表
     * @return ShortLinkProjectCountLinkRespDTO 列表其中包含了 gid 和当前分组下的短链接数量
     */
    @Override
    public List<ShortLinkCountLinkRespDTO> countGroupLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("gid as gid ,count(*) as ShortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(maps, ShortLinkCountLinkRespDTO.class);
    }


    //TODO 后期需要优化
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        //TODO originUrl后续要改为不需要手动添加http
        String originUrl = requestParam.getOriginUrl();
        if (!originUrl.startsWith("http://") && !originUrl.startsWith("https://")) {
            originUrl = "http://" + originUrl;
        }
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
            ShortLinkDO updateShortLinkDO = ShortLinkDO.builder()
                    .originUrl(originUrl)
                    .describe(requestParam.getDescribe())
                    .validDateType(requestParam.getValidDateType())
                    .build();
            updateWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
            updateWrapper.eq(ShortLinkDO::getGid, requestParam.getGid());
            updateWrapper.eq(ShortLinkDO::getEnableStatus, 0);
            updateWrapper.set(
                    ShortLinkDO::getValidDate,
                    Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANENT.getType())
                            ? null
                            : requestParam.getValidDate()
            );
            update(updateShortLinkDO, updateWrapper);
        } else {
            //删除link表里的数据
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
            updateWrapper.eq(ShortLinkDO::getGid, hasLink.getGid());
            updateWrapper.eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            //删除路由表里的数据
            LambdaQueryWrapper<ShortLinkGotoDO> gotoDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
            gotoDOLambdaQueryWrapper.eq(ShortLinkGotoDO::getGid, hasLink.getGid());
            gotoDOLambdaQueryWrapper.eq(ShortLinkGotoDO::getFullShortUrl, hasLink.getFullShortUrl());
            shortLinkGotoMapper.delete(gotoDOLambdaQueryWrapper);
            //新增link表里的数据
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .domain(hasLink.getDomain())
                    .originUrl(originUrl)
                    .gid(requestParam.getGid())
                    .createdType(hasLink.getCreatedType())
                    .validDateType(requestParam.getValidDateType())
                    .validDate(requestParam.getValidDate())
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
            shortLinkStats(fullShortUrl, request, response);
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
                shortLinkStats(fullShortUrl, request, response);
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
            shortLinkStats(fullShortUrl, request, response);
            ((HttpServletResponse) response).sendRedirect(originUrl);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取短链接的基础状态
     */
    private void shortLinkStats(String fullShortUrl, ServletRequest request, ServletResponse response) {
        //通过cookie来判断是否需要更新uv
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        AtomicReference<String> uv = new AtomicReference<>();
        //客户端如果没有携带cookie过来，则为客户端创建cookie
        Runnable addCookieTask = () -> {
            uv.set(UUID.fastUUID().toString());
            Cookie uvCookie = new Cookie("uv", uv.get());
            uvCookie.setMaxAge(60 * 60 * 24 * 30);
            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
            ((HttpServletResponse) response).addCookie(uvCookie);
            //这个用户是第一次访问
            uvFirstFlag.set(Boolean.TRUE);
            stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + fullShortUrl, uv.get());
        };
        //如果客户端已经带了cookie过来，说明这个用户不是第一次访问
        if (ArrayUtil.isNotEmpty(cookies)) {
            Arrays.stream(cookies)
                    .filter(each -> StrUtil.equals("uv", each.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresentOrElse(each -> {
                        uv.set(each);
                        Long uvAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + fullShortUrl, uv.get());
                        uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                    }, addCookieTask);
        } else {
            //客户端没有携带cookie，说明是第一次访问
            addCookieTask.run();
        }
        //控制对uip的访问
        String clientIp = LinkStatsUtil.getClientIp((HttpServletRequest) request);
        Long uipAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UIP_KEY + fullShortUrl, clientIp);
        boolean uipFirstFlag = (uipAdded != null && uipAdded > 0L);
        LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                .pv(1)
                .uv(uvFirstFlag.get() ? 1 : 0)
                .uip(uipFirstFlag ? 1 : 0)
                .hour(DateUtil.hour(new Date(), true))
                .weekday(DateUtil.thisWeekOfMonth())
                .date(new Date())
                .fullShortUrl(fullShortUrl)
                .build();
        linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
        //调用高德API来获取地区信息
        Map<String, Object> localParamMap = new HashMap<>();
        localParamMap.put("key", mapApiKey);
        localParamMap.put("ip", clientIp);
        String localResultStr = HttpUtil.get(SystemConstants.AMAP_API, localParamMap);
        JSONObject localObj = JSON.parseObject(localResultStr);
        String infoCode = localObj.getString("infocode");
        if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
            String province = localObj.getString("province");
            boolean unknownFlag = StrUtil.equals("[]", province);
            LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .province(unknownFlag ? "未知" : province)
                    .city(unknownFlag ? "未知" : localObj.getString("city"))
                    .adcode(unknownFlag ? "未知" : localObj.getString("adcode"))
                    .country("中国")
                    .build();
            linkLocaleStatsMapper.shortLinkLocalState(linkLocaleStatsDO);
        } else {
            throw new ClientException("地图数据获取异常");
        }
        String remoteAddr = LinkStatsUtil.getClientIp(((HttpServletRequest) request));
        String device = LinkStatsUtil.getDevice(((HttpServletRequest) request));
        String network = LinkStatsUtil.getNetwork(((HttpServletRequest) request));
        String userBrowser = LinkStatsUtil.getUserBrowser(((HttpServletRequest) request));
        String userOS = LinkStatsUtil.getUserOS(((HttpServletRequest) request));
        LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                .os(userOS)
                .cnt(1)
                .fullShortUrl(fullShortUrl)
                .date(new Date())
                .build();
        linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);
        LinkBrowserStatsDO linkBrowserStats = LinkBrowserStatsDO.builder()
                .browser(userBrowser)
                .date(new Date())
                .fullShortUrl(fullShortUrl)
                .cnt(1)
                .build();
        linkBrowserStatsMapper.shortLInkBrowserStats(linkBrowserStats);
        LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                .user(uv.get())
                .ip(remoteAddr)
                .browser(userBrowser)
                .os(userOS)
                .fullShortUrl(fullShortUrl)
                .build();
        linkAccessLogsMapper.insert(linkAccessLogsDO);
    }

//    //TODO ShortLinkStatsRecordDTO 待使用
//
//    /**
//     * 获取短链接的基础状态
//     */
//    private ShortLinkStatsRecordDTO buildLinkStatsRecordAndSetUser(String fullShortUrl, ServletRequest request, ServletResponse response) {
//        //通过cookie来判断是否需要更新uv
//        AtomicBoolean uvFirstFlag = new AtomicBoolean();
//        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
//        AtomicReference<String> uv = new AtomicReference<>();
//        //客户端如果没有携带cookie过来，则为客户端创建cookie
//        Runnable addCookieTask = () -> {
//            uv.set(UUID.fastUUID().toString());
//            Cookie uvCookie = new Cookie("uv", uv.get());
//            uvCookie.setMaxAge(60 * 60 * 24 * 30);
//            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
//            ((HttpServletResponse) response).addCookie(uvCookie);
//            //这个用户是第一次访问
//            uvFirstFlag.set(Boolean.TRUE);
//            stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + fullShortUrl, uv.get());
//        };
//        //如果客户端已经带了cookie过来，说明这个用户不是第一次访问
//        if (ArrayUtil.isNotEmpty(cookies)) {
//            Arrays.stream(cookies)
//                    .filter(each -> StrUtil.equals("uv", each.getName()))
//                    .findFirst()
//                    .map(Cookie::getValue)
//                    .ifPresentOrElse(each -> {
//                        Long uvAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + fullShortUrl, uv.get());
//                        uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
//                    }, addCookieTask);
//        } else {
//            //客户端没有携带cookie，说明是第一次访问
//            addCookieTask.run();
//        }
//        String remoteAddr = LinkStatsUtil.getClientIp(((HttpServletRequest) request));
//        String device = LinkStatsUtil.getDevice(((HttpServletRequest) request));
//        String network = LinkStatsUtil.getNetwork(((HttpServletRequest) request));
//        String userBrowser = LinkStatsUtil.getUserBrowser(((HttpServletRequest) request));
//        String userOS = LinkStatsUtil.getUserOS(((HttpServletRequest) request));
//        Long uipAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UIP_KEY + fullShortUrl, remoteAddr);
//        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
//        return ShortLinkStatsRecordDTO.builder()
//                .remoteAddr(remoteAddr)
//                .os(userOS)
//                .uv(uv.get())
//                .browser(userBrowser)
//                .currentDate(new Date())
//                .device(device)
//                .network(network)
//                .uvFirstFlag(uvFirstFlag.get())
//                .uipFirstFlag(uipFirstFlag)
//                .build();
//    }

    /**
     * 根据传入的原始链接来创建短链接
     *
     * @param requestParam
     * @return
     */
    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
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
