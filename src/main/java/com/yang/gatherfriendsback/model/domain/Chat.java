package com.yang.gatherfriendsback.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 聊天消息表
 * @TableName chat
 */
@TableName(value ="chat")
@Data
public class Chat {
    /**
     * 聊天记录id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送消息id
     */
    private Long fromId;

    /**
     * 接收消息id
     */
    private Long toId;

    /**
     * 
     */
    private String text;

    /**
     * 聊天类型 1-私聊 2-群聊
     */
    private Integer chatType;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 是否已读 1-已读 2-未读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Long teamId;

    /**
     * 
     */
    private Integer isDelete;
}