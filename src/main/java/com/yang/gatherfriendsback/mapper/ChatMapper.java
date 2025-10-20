package com.yang.gatherfriendsback.mapper;


import com.yang.gatherfriendsback.model.domain.Chat;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


/**
* @author liuya
* @description 针对表【chat(聊天消息表)】的数据库操作Mapper
* @createDate 2025-10-17 16:00:07
*/

@Mapper
public interface ChatMapper extends BaseMapper<Chat> {

}




