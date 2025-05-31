/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xwj.shortlink.common.biz;

import com.alibaba.fastjson2.JSON;
import com.xwj.shortlink.common.convention.exception.ClientException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader("username");
        String token = httpServletRequest.getHeader("token");

        String uri = httpServletRequest.getRequestURI();
        // 👇 忽略某些 URL（白名单）
        if (uri.startsWith("/api/short-link/admin/v1/user/login")) {
            filterChain.doFilter(servletRequest, servletResponse);  // 放行不处理
            return;
        }
        if (uri.startsWith("/api/short-link/admin/v1/user")) {
            filterChain.doFilter(servletRequest, servletResponse);  // 放行不处理
            return;
        }
        if (!Objects.isNull(token)) {
            Object userInfoJsonStr = stringRedisTemplate.opsForHash().get("login:" + username, token);
            if (Objects.isNull(userInfoJsonStr)) {
                throw new ClientException("登录失效，请重新登录");
            }
            UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
            UserContext.setUser(userInfoDTO);
            try {
                filterChain.doFilter(servletRequest, servletResponse);
            } finally {
                UserContext.removeUser();
            }
        } else {
            throw new ClientException("登录失效，请重新登录");
        }
    }
}