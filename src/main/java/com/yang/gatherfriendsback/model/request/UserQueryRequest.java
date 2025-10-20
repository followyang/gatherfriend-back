package com.yang.gatherfriendsback.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户查询请求
 *
 */
@Data
public class UserQueryRequest implements Serializable {
 private static final long serialVersionUID = 3191241716373120793L;

 private String username;

 private List<String> tags;

 private Long pageSize;

 private  Long pageNum;
}
