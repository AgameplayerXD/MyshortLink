package com.xwj.shortlink.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.remote.dto.req.ShortLinkProjectCreateReqDTO;
import com.xwj.shortlink.remote.dto.req.ShortLinkProjectPageReqDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkProjectCreateRespDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkProjectPageRespDTO;
import com.xwj.shortlink.remote.dto.resp.ShortLinkRemoteCountLinkRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接远程中台调用服务
 */
public interface ShortLinkRemoteService {
    /**
     * 分页查询短链接
     *
     * @param requestParam
     * @return
     */
    default Result<IPage<ShortLinkProjectPageRespDTO>> shortLinkProjectPage(ShortLinkProjectPageReqDTO requestParam) {
        Map<String, Object> getParam = new HashMap<>();
        getParam.put("gid", requestParam.getGid());
        getParam.put("current", requestParam.getCurrent());
        getParam.put("size", requestParam.getSize());
        String responseStr = HttpUtil.get("http://localhost:8000/api/short-link/v1/page", getParam);
        return JSON.parseObject(responseStr, new TypeReference<>() {
        });
    }

    /**
     * 创建短链接
     *
     * @param requestParam
     * @return
     */
    default Result<ShortLinkProjectCreateRespDTO> shortLinkProjectCreate(ShortLinkProjectCreateReqDTO requestParam) {
        String responseStr = HttpUtil.post("http://localhost:8000/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(responseStr, new TypeReference<>() {
        });
    }

    default Result<List<ShortLinkRemoteCountLinkRespDTO>> countGroupLinkCount(List<String> requestParam) {
        Map<String, Object> getParam = new HashMap<>();
        getParam.put("requestParam", requestParam);
        String responseStr = HttpUtil.get("http://localhost:8000/api/short-link/v1/count", getParam);
        return JSON.parseObject(responseStr, new TypeReference<>() {
        });
    }
}
