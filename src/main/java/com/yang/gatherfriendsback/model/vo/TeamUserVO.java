package com.yang.gatherfriendsback.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍和用户信息封装类（脱敏）
 *
 * @author yang
 */
@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = 163478861968488713L;
    /**
     * id
     */
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
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
    private UserVO createUser;

    /**
     * 已加入的用户数
     */
    private Integer hasJoinNum;

    /*
    * 已加入用户Id
    * */
    private List< Long> joinUserIds;

    /*
    * 标签
    * */

    private List<String> tags;

    /**
     * 队伍头像
     */
    private String avatar;
    /**
     * 是否已加入队伍
     */
    private boolean hasJoin = false;
}