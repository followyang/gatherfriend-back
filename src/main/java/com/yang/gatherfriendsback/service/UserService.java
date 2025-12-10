package com.yang.gatherfriendsback.service;

import com.yang.gatherfriendsback.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yang.gatherfriendsback.model.request.UserQueryRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liuya
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-10-02 14:58:59
*/
@Service
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     *
     * @param user
     * @param loginUser
     * @return
     */
    User updateUser(User user, User loginUser);

    /**
     * 搜索用户
     *
     * @return
     */
    List< User> searchUser(UserQueryRequest userQueryRequest , User loginUser);

    List<User> matchUsers(Long num, User loginUser);
}

