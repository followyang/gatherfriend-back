package com.yang.gatherfriendsback.chatmysql;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yang.gatherfriendsback.model.domain.AiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseChatMemory implements ChatMemory {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void add(String conversationId, List<Message> messages) {
        System.out.println(messages);
        List<AiMessage> aiMessages = messages.stream()
                .map(message -> MessageConverter.toChatMessage(message, conversationId))
                .collect(Collectors.toList());
        
        chatMessageRepository.saveBatch(aiMessages, aiMessages.size());
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {

        LambdaQueryWrapper<AiMessage> queryWrapper = new LambdaQueryWrapper<>();
        // 查询最近的 lastN 条消息
        queryWrapper.eq(AiMessage::getConversationId, conversationId)
                   .orderByDesc(AiMessage::getCreateTime)
                   .last(lastN > 0, "LIMIT " + lastN);
        
        List<AiMessage> chatMessages = chatMessageRepository.list(queryWrapper);
        
        // 按照时间顺序返回
        if (!chatMessages.isEmpty()) {
            Collections.reverse(chatMessages);
        }
        
        return chatMessages
                .stream()
                .map(MessageConverter::toMessage)
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        LambdaQueryWrapper<AiMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiMessage::getConversationId, conversationId);
        chatMessageRepository.remove(queryWrapper);
    }
}