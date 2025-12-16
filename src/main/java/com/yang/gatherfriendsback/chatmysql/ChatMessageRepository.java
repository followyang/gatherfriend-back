package com.yang.gatherfriendsback.chatmysql;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yang.gatherfriendsback.mapper.AiMessageMapper;
import com.yang.gatherfriendsback.model.domain.AiMessage;
import org.springframework.stereotype.Repository;

/**
 * ChatMessageRepository
 * 注意：MyBatis-Plus中没有CrudRepository接口
 * MyBatis-Plus使用的是IService和ServiceImpl
 * 
 * 在MyBatis-Plus 3.5.7版本中：
 * - PaginationInnerInterceptor：分页插件，存在于mybatis-plus-extension包中
 * - IService/ServiceImpl：CRUD服务接口和实现，存在于mybatis-plus-core包中
 * 这两个可以同时使用，因为它们属于不同的功能模块
 */
@Repository
public class ChatMessageRepository extends ServiceImpl<AiMessageMapper, AiMessage> {

}