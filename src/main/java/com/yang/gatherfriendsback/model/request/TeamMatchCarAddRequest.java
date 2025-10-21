package com.yang.gatherfriendsback.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class TeamMatchCarAddRequest {
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 队伍头像
     */
    private String avatar;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
    /**
     * 标签 json 列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    //精度
    private String longitude;
    //纬度
    private String latitude;
}
