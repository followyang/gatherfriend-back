package com.yang.gatherfriendsback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yang.gatherfriendsback.model.domain.AiMessage;
import com.yang.gatherfriendsback.service.AiMessageService;
import com.yang.gatherfriendsback.mapper.AiMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author liuya
* @description 针对表【ai_message(聊天消息表)】的数据库操作Service实现
* @createDate 2025-12-10 18:55:52
*/
@Service
public class AiMessageServiceImpl extends ServiceImpl<AiMessageMapper, AiMessage>
    implements AiMessageService{

}




