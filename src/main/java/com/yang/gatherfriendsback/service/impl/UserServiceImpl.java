package com.yang.gatherfriendsback.service.impl;


import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yang.gatherfriendsback.common.ErrorCode;
import com.yang.gatherfriendsback.constant.UserConstant;
import com.yang.gatherfriendsback.exception.BusinessException;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.request.UserQueryRequest;
import com.yang.gatherfriendsback.service.UserService;
import com.yang.gatherfriendsback.mapper.UserMapper;
import com.yang.gatherfriendsback.utils.AlgorithmUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
            throw new BusinessException(ErrorCode.NOT_LOGIN);
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
    /**
     * @param userQueryRequest:
    	 * @param loginUser:
      * @return List<User>
     * @author liuya
     * @description TODO
     * @date 2025/11/19 下午3:30
     */

    public List<User> searchUser(UserQueryRequest userQueryRequest ,User loginUser) {
        String username = userQueryRequest.getUsername();
        List<String> tags = userQueryRequest.getTags();
        Long pageNum = userQueryRequest.getPageNum();
        Long pageSize = userQueryRequest.getPageSize();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("isDelete", 1).ne("id",loginUser.getId());

        //如果username和tags都为空.查询20条数据
        if (StrUtil.isBlank(username) && (tags==null||tags.isEmpty())) {

            IPage<User> page = new Page<>(pageNum, pageSize);
            IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
            return userPage.getRecords();
        }
        //如果username 不为空
        if (StrUtil.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        //如果tags 不为空
        if (tags != null && !tags.isEmpty()) {
            queryWrapper.and(wrapper -> {
                for (int i = 0; i < tags.size(); i++) {
                    String tag = tags.get(i);
                    wrapper.like("tags", tag);
                    // 最后一个标签不需要加or
                    if (i < tags.size() - 1) {
                        wrapper.or();
                    }
                }
            });
        }
        IPage<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
        return userPage.getRecords();
    }

    @Override
    /**
     * @param num:
    	 * @param loginUser:
      * @return List<User>
     * @author liuya
     * @description TODO
     * @date 2025/11/19 下午3:34
     */
    public List<User> matchUsers(Long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = JSONUtil.toJsonStr(loginUser.getTags());
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = JSONUtil.toJsonStr(user.getTags()) ;
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }



}




