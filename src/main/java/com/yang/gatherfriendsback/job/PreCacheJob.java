package com.yang.gatherfriendsback.job;/**
 * @projectName: gatherfriends-back
 * @package: com.yang.gatherfriendsback.job
 * @className: PreCacheJob
 * @author: ly
 * @description: TODO
 * @date: 2025/11/19 下午9:01
 * @version: 1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @className: PreCacheJob
 * @description: TODO
 * @date: 2025/11/19 下午9:01
 */

@Slf4j
public class PreCacheJob {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private final List<Long>  userList = new ArrayList<>();

    @Scheduled(cron = "0 4 0 * * *")
    public void doCacheRecommendUser(){

        RLock lock = redissonClient.getLock("gather:precache:doCache:lock");

        try {
            if(lock.tryLock(0,30000L, TimeUnit.MILLISECONDS)){
                for (Long userId : userList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
                    String redisKey = String.format("ly:user:recommend:%s",userId);
                    ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
                    try{
                            valueOperations.set(redisKey,userPage,30000,TimeUnit.MILLISECONDS);
                    }
                    catch (Exception e){
                        log.error("redis key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            //释放掉自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }

        }
    }


}
