package com.yang.gatherfriendsback.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

/**
 * 聊天消息表
 * @TableName ai_message
 */
@Data
@Builder
@NoArgsConstructor  // 必须添加无参构造器
@AllArgsConstructor // 可选，方便构建对象
@TableName(value = "ai_message", autoResultMap = true)
public class AiMessage {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 消息类型
     */

    @TableField("message_type")
    private MessageType messageType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 元数据
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    /**
     * 创建时间
     */
    @TableField(value = "`create_time`", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @Version
    @TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    @TableField("`is_delete`")
    @TableLogic
    private boolean isDelete;
}