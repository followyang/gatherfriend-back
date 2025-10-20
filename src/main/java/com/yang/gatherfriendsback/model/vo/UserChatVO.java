package com.yang.gatherfriendsback.model.vo;

import lombok.Data;

import java.util.Date;

/*
* 返回用户对话时的信息
* */
@Data
public class UserChatVO {
    /**
     * id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;



    private static final long serialVersionUID = 1L;
}
