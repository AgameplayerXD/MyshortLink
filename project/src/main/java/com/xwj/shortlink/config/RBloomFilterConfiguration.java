package com.xwj.shortlink.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 防止短链接创建时，海量查询数据库的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> linkCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("linkCreateCachePenetrationBloomFilter");
        //创建一个可以容纳一亿用户名映射的位数组，允许出错的概率为 0.1%
        cachePenetrationBloomFilter.tryInit(100000000, 0.001);
        return cachePenetrationBloomFilter;
    }
}