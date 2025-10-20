package com.yang.gatherfriendsback.controller;

import com.yang.gatherfriendsback.common.BaseResponse;
import com.yang.gatherfriendsback.common.ErrorCode;
import com.yang.gatherfriendsback.common.ResultUtils;
import com.yang.gatherfriendsback.constant.UserConstant;
import com.yang.gatherfriendsback.exception.BusinessException;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.request.UserLoginRequest;
import com.yang.gatherfriendsback.model.request.UserQueryRequest;
import com.yang.gatherfriendsback.model.request.UserRegisterRequest;
import com.yang.gatherfriendsback.service.UserService;
import com.yang.gatherfriendsback.websoket.ChatEndpoint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "用户接口")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public BaseResponse<User> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        if (user != null) {
            request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
            log.info("用户信息存入Session {}",request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE));
        }
        return ResultUtils.success(user);
    }
    @Operation(summary = "获取登录用户信息")
    @GetMapping("/get")
    public BaseResponse<User> getUserInfo(HttpServletRequest request) {
        User currentUser = userService.getLoginUser(request);
        User user = userService.getById(currentUser.getId());
        return ResultUtils.success(user);
    }

    @Operation(summary = "用户更新")
    @PostMapping("/update")
    public BaseResponse<User> update(@RequestBody User user, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User updateUser = userService.updateUser(user, loginUser);
        return ResultUtils.success(updateUser);
    }

    //搜索用户
    @Operation(summary = "搜索用户")
    @PostMapping("/search")
    public BaseResponse<List<User>> searchUser(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {

        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        //获取登录用户
        List< User> userList = userService.searchUser(userQueryRequest, loginUser);
        return ResultUtils.success(userList);
    }

}
