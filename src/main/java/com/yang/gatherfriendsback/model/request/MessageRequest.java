package com.yang.gatherfriendsback.model.request;

import lombok.Data;

@Data
public class MessageRequest {
    //发送者id
    private Long fromId;

    //接受者id
    private Long toId;

    //群id
    private Long teamId;

    //消息内容
    private String text;

    //消息类型
    private Integer type;

}
