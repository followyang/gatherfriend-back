package com.yang.gatherfriendsback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yang.gatherfriendsback.common.ErrorCode;
import com.yang.gatherfriendsback.constant.UserConstant;
import com.yang.gatherfriendsback.exception.BusinessException;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.request.UserQueryRequest;
import com.yang.gatherfriendsback.service.UserService;
import com.yang.gatherfriendsback.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yang.gatherfriendsback.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author liuya
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-10-02 14:58:59
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    String SALT = "yang";

    @Autowired
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        User safetyUser = getSafetyUser(user);

        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.valueOf("request 为空"));
        }
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.valueOf("用户未登录"));
        }
        return loginUser;
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @param loginUser
     * @return
     */
    @Override
    @Transactional
    public User updateUser(User user, User loginUser) {
        if (loginUser == null || user == null) {
            log.info("uesr 或 uesrLogin 参数错误");
            return null;
        }
        Long loginUserId = loginUser.getId();
        //不能更新
        // 判断用户是否为管理员，若不为管理员且不为本人登录则报错
        if (user.getUserRole() != UserConstant.ADMIN_ROLE) {
            if (!loginUserId.equals(user.getId())) {
                throw  new BusinessException(ErrorCode.valueOf("非管理员无权限"));
            }
        }
        //判断用户是否被删除
        if (user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.valueOf("用户被删除"));
        }
        //判断用户状态
        if (user.getUserStatus() == 1) {
            throw new BusinessException(ErrorCode.valueOf("用户被封禁"));
        }
        boolean isUpdate = update().eq("id", user.getId()).update(user);
        if (!isUpdate) {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return this.getById(user.getId());
    }

    @Override
    public List<User> searchUser(UserQueryRequest userQueryRequest ,User loginUser) {
        String username = userQueryRequest.getUsername();
        List<String> tags = userQueryRequest.getTags();
        Long pageNum = userQueryRequest.getPageNum();
        Long pageSize = userQueryRequest.getPageSize();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("isDelete", 1).ne("id",loginUser.getId());

        //如果username和tags都为空.查询20条数据
        if ((username==null||username.equals(""))&& (tags==null||tags.size()==0)) {

            IPage<User> page = new Page<>(pageNum, pageSize);
            IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
            return userPage.getRecords();
        }
        //如果username 不为空
        if (username.equals("")&&username!=null) {
            queryWrapper.like("username", username);
        }
        //如果tags 不为空
        if (tags!=null&&tags.size()!=0) {
           queryWrapper.like("tags", tags);
        }
        IPage<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
        return userPage.getRecords();
    }
}




