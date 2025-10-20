package com.yang.gatherfriendsback.mapper;

import com.yang.gatherfriendsback.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author liuya
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2025-10-02 14:58:59
* @Entity com.yang.gatherfriendsback.model.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




