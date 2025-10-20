package com.yang.gatherfriendsback.model.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * 聊天信息vo
 *
 * @author OchiaMalu
 * @date 2023/06/19
 */
@Data
public class ChatMessageVO implements Serializable {
    /**
     * 形式用户
     */
    private UserVO fromUser;
    /**
     * 用户
     */
    private UserVO toUser;
    /**
     * 团队id
     */
    private Long teamId;
    /**
     * 文本
     */
    private String text;
    /**
     * 是我
     */
    private Boolean isMy = false;
    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 聊天类型
     */
    private Integer chatType;
    /**
     * 是管理
     */
    private Boolean isAdmin = false;
    /**
     * 创建时间
     */
    private String createTime;


    private static final long serialVersionUID = -4722378360550337925L;
}

