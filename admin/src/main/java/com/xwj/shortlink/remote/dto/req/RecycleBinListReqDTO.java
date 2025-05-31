package com.xwj.shortlink.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

@Data
public class RecycleBinListReqDTO extends Page {
    private List<String> gidList;
}
