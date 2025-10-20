package com.yang.gatherfriendsback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yang.gatherfriendsback.mapper.UserTeamMapper;
import com.yang.gatherfriendsback.model.domain.UserTeam;
import com.yang.gatherfriendsback.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author liuya
* @description 针对表【user_team(用户-队伍关联表)】的数据库操作Service实现
* @createDate 2025-10-13 15:47:11
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




