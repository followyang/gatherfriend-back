package com.yang.gatherfriendsback.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户-队伍关联表
 * @TableName user_team
 */
@TableName(value ="user_team")
@Data
public class UserTeam {
    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("userId")
    private Long userId;

    /**
     * 队伍ID
     */
    @TableField("teamId")
    private Long teamId;

    /**
     * 角色：0-普通成员，1-队长，2-管理员
     */
    private Integer role;

    /**
     * 状态：0-正常，1-已退出
     */
    private Integer status;

    /**
     * 加入时间
     */
    @TableField("joinTime")
    private Date joinTime;

}