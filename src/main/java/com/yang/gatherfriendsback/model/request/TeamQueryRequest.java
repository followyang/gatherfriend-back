package com.yang.gatherfriendsback.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeamQueryRequest  implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String name;

    private String description;

    // 最大人数
    private Integer maxNum;
    // 过期时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    private Integer status;

    private List<String> tags;

    private Long pageSize;

    private Long pageNum;

}
