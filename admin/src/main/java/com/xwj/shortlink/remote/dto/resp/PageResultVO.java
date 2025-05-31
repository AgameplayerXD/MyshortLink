package com.xwj.shortlink.remote.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * Page类型的对象返回VO封装
 *
 * @param <T>
 */
@Data
public class PageResultVO<T> {
    private long current;
    private long size;
    private long total;
    private List<T> records;
}
