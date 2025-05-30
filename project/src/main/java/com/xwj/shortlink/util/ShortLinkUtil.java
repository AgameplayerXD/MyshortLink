package com.xwj.shortlink.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.xwj.shortlink.common.constant.ShortLinkConstant;

import java.util.Date;
import java.util.Optional;

/**
 * 短链接工具类
 */
public class ShortLinkUtil {
    /**
     * 获取缓存的有效期时间
     *
     * @param validData 如果传入null，则证明是永久有效，默认的有效期是一个月，如果传入的是日期，则与当前的系统时间做差值计算，计算还剩下多少有效期，然后返回
     * @return 返回还剩下多少有效期，单位是毫秒
     */
    public static Long getCacheValidTime(Date validData) {
        return Optional.ofNullable(validData)
                .map(date1 -> DateUtil.between(new Date(), date1, DateUnit.MS))
                .orElse(ShortLinkConstant.DEFAULT_CACHE_VALID_TIME);

    }
}
