package com.yang.gatherfriendsback.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yang.gatherfriendsback.model.domain.Chat;
import com.yang.gatherfriendsback.model.vo.ChatMessageVO;

import java.util.List;

/**
* @author liuya
* @description 针对表【chat(聊天消息表)】的数据库操作Service
* @createDate 2025-10-17 16:00:07
*/
public interface ChatService extends IService<Chat> {

    /*
    * 聊天历史
    * */
    List<ChatMessageVO> listUserAges(Long fromId, Long toId);


    /*
    * 群聊历史
    * */
    List<ChatMessageVO> listTeamAges(Long fromId, Long teamId);
}
