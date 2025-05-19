package com.xwj.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.shortlink.dao.entity.UserDo;
import com.xwj.shortlink.dto.req.UserLoginReqDTO;
import com.xwj.shortlink.dto.req.UserReqDTO;
import com.xwj.shortlink.dto.resp.ActualUserRespDTO;
import com.xwj.shortlink.dto.resp.UserLoginRespDTO;
import com.xwj.shortlink.dto.resp.UserRespDTO;

/**
 * 用户业务接口
 */
public interface UserService extends IService<UserDo> {
    UserRespDTO getUserByUsername(String username);

    ActualUserRespDTO getActualUserByUsername(String username);

    Boolean hasUsername(String username);

    void register(UserReqDTO requestParam);

    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    Boolean checkLogin(String username, String token);

    void logout(String username, String token);
}
