package com.yang.gatherfriendsback.model.vo;/**
 * @projectName: gatherfriends-back
 * @package: com.yang.gatherfriendsback.model.vo
 * @className: AiChatVO
 * @author: ly
 * @description: TODO
 * @date: 2025/12/11 上午9:46
 * @version: 1.0
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.ai.chat.messages.MessageType;

import java.util.Date;

/**
 * @className: AiChatVO
 * @description: TODO
 * @date: 2025/12/11 上午9:46
 */
@Data
public class AiChatVO {


    private static final long serialVersionUID = 1L;


    /**
     * 会话ID
     */

    private String conversationId;

    /**
     * 消息类型
     */


    private MessageType messageType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;



    /**
     * 创建时间
     */
    @TableField(value = "`create_time`", fill = FieldFill.INSERT)
    private Date createTime;


}
