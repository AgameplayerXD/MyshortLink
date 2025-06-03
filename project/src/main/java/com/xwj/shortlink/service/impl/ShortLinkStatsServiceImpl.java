package com.xwj.shortlink.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.shortlink.dao.entity.LinkAccessStatsDO;
import com.xwj.shortlink.dao.mapper.LinkAccessStatsMapper;
import com.xwj.shortlink.service.ShortLinkStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 统计短链接基础状态实现层
 */
@Service
@Slf4j
public class ShortLinkStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStatsDO> implements ShortLinkStatsService {
}
