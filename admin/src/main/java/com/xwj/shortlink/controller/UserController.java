package com.xwj.shortlink.controller;

import com.xwj.shortlink.common.convention.result.Result;
import com.xwj.shortlink.common.convention.result.Results;
import com.xwj.shortlink.dto.req.UserLoginReqDTO;
import com.xwj.shortlink.dto.req.UserReqDTO;
import com.xwj.shortlink.dto.resp.ActualUserRespDTO;
import com.xwj.shortlink.dto.resp.UserLoginRespDTO;
import com.xwj.shortlink.dto.resp.UserRespDTO;
import com.xwj.shortlink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名来查询用户信息，手机号码脱敏展示
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名来查询用户信息，信息未脱敏
     */
    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    public Result<ActualUserRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getActualUserByUsername(username));
    }

    /**
     * 判断用户名是否已经存在
     */
    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 注册新用户
     */
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 用户登陆
     */
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户是否登录和token 是否有效
     */
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username,@RequestParam("token") String token){
        return Results.success(userService.checkLogin(username,token));
    }

    /**
     * 退出登录
     *
     * @param username
     * @param token
     * @return
     */
    @DeleteMapping("/api/short-link/admin/v1/user/logout")
    public Result<Void> logout(@RequestHeader("username") String username, @RequestHeader("token") String token) {
        userService.logout(username,token);
        return Results.success();
    }
}
