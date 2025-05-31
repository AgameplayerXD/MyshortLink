package com.xwj.shortlink.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Page类型的对象返回VO封装
 *
 * @param <T>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResultVO<T> {
    private long current;
    private long size;
    private long total;
    private List<T> records;
}
