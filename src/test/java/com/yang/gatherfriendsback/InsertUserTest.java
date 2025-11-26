package com.yang.gatherfriendsback;

import cn.hutool.core.date.StopWatch;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InsertUserTest {

    @Autowired
    private UserService userService;


    //批量插入
    @Test
    void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
         final List<User> listUser = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            User user = new User ();
            user.setUsername ("假的家");
            user.setUserAccount ("fakeLiuYang");
            user.setAvatarUrl ("https://636f-codenav-8grj8px77560176-1256624210.tcb.qcloud.la/img/Logo.png");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags(List.of("aaaa","bbb","ccc"));
            user.setUserStatus(0);
            user.setUserRole(0);
        }
       userService.saveBatch(listUser);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    //异步插入
    @Test
    void doCurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int Max_Value = 10000;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            List<User> listUser = new ArrayList<>();

            int j=0;
            while(true){
                User user = new User ();
                user.setUsername ("假的家");
                user.setUserAccount ("fakeLiuYang");
                user.setAvatarUrl ("https://636f-codenav-8grj8px77560176-1256624210.tcb.qcloud.la/img/Logo.png");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags(List.of("aaaa","bbb","ccc"));
                user.setUserStatus(0);
                user.setUserRole(0);
                listUser.add(user);
                j++;
                if(j%Max_Value==0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->{
                userService.saveBatch(listUser,10000);
            });
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


    //自定义线程池插入
    @Test
    void doExecutorInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        final int Max_Value = 10000;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(1000));
        stopWatch.start();
        for (int i = 0; i < 10; i++) {
            List<User> listUser = new ArrayList<>();

            int j=0;
            while(true){
                User user = new User ();
                user.setUsername ("假的家");
                user.setUserAccount ("fakeLiuYang");
                user.setAvatarUrl ("https://636f-codenav-8grj8px77560176-1256624210.tcb.qcloud.la/img/Logo.png");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags(List.of("aaaa","bbb","ccc"));
                user.setUserStatus(0);
                user.setUserRole(0);
                listUser.add(user);
                j++;
                if(j%Max_Value==0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->{
                userService.saveBatch(listUser,10000);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
