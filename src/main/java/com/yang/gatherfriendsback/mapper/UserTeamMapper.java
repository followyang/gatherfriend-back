package com.yang.gatherfriendsback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yang.gatherfriendsback.model.domain.UserTeam;
import org.apache.ibatis.annotations.Mapper;

/**
* @author liuya
* @description 针对表【user_team(用户-队伍关联表)】的数据库操作Mapper
* @createDate 2025-10-13 15:47:11
* @Entity generator.domain.UserTeam
*/
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {


}




