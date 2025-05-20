package com.xwj.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.shortlink.common.constant.RedisCacheConstant;
import com.xwj.shortlink.common.convention.exception.ClientException;
import com.xwj.shortlink.common.enums.UserErrorEnumsCode;
import com.xwj.shortlink.dao.entity.UserDo;
import com.xwj.shortlink.dao.mapper.UserMapper;
import com.xwj.shortlink.dto.req.UserLoginReqDTO;
import com.xwj.shortlink.dto.req.UserReqDTO;
import com.xwj.shortlink.dto.resp.ActualUserRespDTO;
import com.xwj.shortlink.dto.resp.UserLoginRespDTO;
import com.xwj.shortlink.dto.resp.UserRespDTO;
import com.xwj.shortlink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 用户业务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 根据用户名查询用户信息，手机号码脱敏展示
     */
    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDo::getUsername, username);
        UserDo user = getOne(queryWrapper);
        if (Objects.isNull(user)) {
            throw new ClientException(UserErrorEnumsCode.USER_NULL);
        }
        return BeanUtil.copyProperties(user, UserRespDTO.class);
    }

    /**
     * 根据用户名查询用户信息
     */
    @Override
    public ActualUserRespDTO getActualUserByUsername(String username) {
        UserRespDTO userByUsername = getUserByUsername(username);
        return BeanUtil.copyProperties(userByUsername, ActualUserRespDTO.class);
    }

    /**
     * 从布隆过滤器中的位数组中查询用户名，看看是否已经在位数组中存在用户名了
     *
     * @param username
     * @return true:username 已经被布隆过滤器记录过了，可能存在。
     */
    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserReqDTO requestParam) {
        //判断用户名是否已经存在
//        if (hasUsername(requestParam.getUsername())) {
//            throw new ClientException(UserErrorEnumsCode.USER_NAME_EXIST);
//        }
//        //向数据库中存储新注册的用户
//        save(BeanUtil.copyProperties(requestParam, UserDo.class));
//        //向布隆过滤器中存储新注册的用户名
//        userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
        //       TODO 学习分布式锁方案

        //1.查询布隆过滤器中是否已经有了该用户名的记录
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(UserErrorEnumsCode.USER_NAME_EXIST);
        }
        //2.使用分布式锁来存入数据库，防止海量注册同一用户名的请求打入 db
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        if (!lock.tryLock()) {
            throw new ClientException(UserErrorEnumsCode.USER_NAME_EXIST);
        }
        try {
            //存入数据库
            int inserted = baseMapper.insert(BeanUtil.copyProperties(requestParam, UserDo.class));
            if (inserted < 1) {
                throw new ClientException(UserErrorEnumsCode.USER_SAVE_ERROR);
            }
            //存入 Redis 中的布隆过滤器中
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
        } catch (DuplicateKeyException ex) {
            throw new ClientException(UserErrorEnumsCode.USER_EXIST);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 实现用户登录的功能
     *
     * @return
     */
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        //先检查用户名是否不存在
        if (!hasUsername(requestParam.getUsername())) {
            throw new ClientException("用户记录不存在");
        }
        //再检查用户是否已经登录过了,如果登录过了延长token 有效期，并且返回 token
        Map<Object, Object> loginMap = stringRedisTemplate.opsForHash().entries("login:" + requestParam.getUsername());
        if (CollUtil.isNotEmpty(loginMap)) {
            stringRedisTemplate.expire("login:" + requestParam.getUsername(), 30L, TimeUnit.DAYS);
            String token = loginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException(UserErrorEnumsCode.USER_HAS_LOGIN));
            return new UserLoginRespDTO(token);
        }
        //进行登录,把查到的用户信息和 token 存入到 Redis 中
        //1.查询用户信息
        LambdaQueryWrapper<UserDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDo::getUsername, requestParam.getUsername());
        queryWrapper.eq(UserDo::getPassword, requestParam.getPassword());
        queryWrapper.eq(UserDo::getDelFlag, 0);
        UserDo user = getOne(queryWrapper);
        if (Objects.isNull(user)) {
            throw new ClientException("密码错误");
        }
        //2.生成 token
        String token = UUID.randomUUID().toString();
        //3.将 token 和用户对象存入 Redis 中
        /*
         * Hash
         * Key：login:用户名
         * Value：
         *  Key：token标识
         *  Val：JSON 字符串（用户信息）
         */
        stringRedisTemplate.opsForHash().put("login:" + requestParam.getUsername(), token, JSON.toJSONString(user));
        stringRedisTemplate.expire("login:" + requestParam.getUsername(), 30L, TimeUnit.DAYS);
        return new UserLoginRespDTO(token);
    }

    /**
     * 检查用户是否已经登录，还要检查token 是否是有效的 token
     *
     * @return true：用户已经登录而且携带的 token 有效/false：用户未登录或者 token 无效
     */
    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get("login:" + username, token) != null;
    }

    /**
     * 退出登录
     */
    @Override
    public void logout(String username, String token) {
        if (checkLogin(username, token)) {
            stringRedisTemplate.delete("login:" + username);
            return;
        }
        throw new ClientException("用户未登录或者 token 已经过期");
    }
}
