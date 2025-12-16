package com.yang.gatherfriendsback.chatmysql;


import com.yang.gatherfriendsback.model.domain.AiMessage;
import org.springframework.ai.chat.messages.*;

import java.util.List;
import java.util.Map;

public class MessageConverter {

    /**
     * 将 Message 转换为 ChatMessage
     */
    public static AiMessage toChatMessage(Message message, String conversationId) {
        return AiMessage.builder()
                .conversationId(conversationId)
                .messageType(message.getMessageType())
                .content(message.getText())
                .metadata(message.getMetadata())
                .build();
    }

    /**
     * 将 AiMessage 转换为 Message
     */
    public static Message toMessage(AiMessage aiMessage) {
        MessageType messageType = aiMessage.getMessageType();
        String text = aiMessage.getContent();
        Map<String, Object> metadata = aiMessage.getMetadata();
        return switch (messageType) {
            case USER -> new UserMessage(text);
            case ASSISTANT -> new AssistantMessage(text, metadata);
            case SYSTEM -> new SystemMessage(text);
            case TOOL -> new ToolResponseMessage(List.of(), metadata);
        };
    }

}
