
package com.yang.gatherfriendsback.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 队伍
 * @TableName team
 */
@TableName(value ="team", autoResultMap = true)
@Data
public class Team {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
    @TableLogic(value = "0", delval = "1")
    @TableField("isDelete")
    private Integer isDelete;
    /**
     * 标签 json 列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}


