package com.xwj.shortlink.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageUtil {

    /**
     * 将 Page<S> 转换为 Page<T>，其中 S 是原始类型，T 是目标类型
     *
     * @param sourcePage 源分页数据
     * @param mapper     映射函数，从 S 转为 T
     * @param <S>        源数据类型
     * @param <T>        目标数据类型
     * @return 转换后的 Page<T>
     */
    public static <S, T> Page<T> convert(Page<S> sourcePage, Function<? super S, ? extends T> mapper) {
        List<T> convertedRecords = sourcePage.getRecords().stream()
                .map(mapper)
                .collect(Collectors.toList());

        Page<T> targetPage = new Page<>();
        targetPage.setRecords(convertedRecords);
        targetPage.setTotal(sourcePage.getTotal());
        targetPage.setSize(sourcePage.getSize());
        targetPage.setCurrent(sourcePage.getCurrent());
        targetPage.setPages(sourcePage.getPages());

        return targetPage;
    }
}