package com.yang.gatherfriendsback.controller;/**
 * @projectName: gatherfriends-back
 * @package: com.yang.gatherfriendsback.controller
 * @className: AiController
 * @author: ly
 * @description: TODO
 * @date: 2025/12/10 下午11:02
 * @version: 1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.gatherfriendsback.app.ChatApp;
import com.yang.gatherfriendsback.common.BaseResponse;
import com.yang.gatherfriendsback.common.ResultUtils;
import com.yang.gatherfriendsback.constant.UserConstant;
import com.yang.gatherfriendsback.model.domain.AiMessage;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.request.AiChatListRequest;
import com.yang.gatherfriendsback.model.vo.AiChatVO;
import com.yang.gatherfriendsback.service.AiMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: AiController
 * @description: TODO
 * @date: 2025/12/10 下午11:02
 */
@RestController
@RequestMapping("/ai")
@Tag(name = "ai接口")
public class AiController {

    @Resource
    private ChatApp chatApp;

    @Resource
    private AiMessageService aiMessageService;

    /**
     * @description: 聊天
     * @param message
     * @param request
     * @return: java.lang.String
     * @author: ly
     * @date: 2025/12/10 下午11:02
     */
    @RequestMapping("/chat")
    public BaseResponse<AiChatVO> chat(String message, HttpServletRequest request){
       if(message==null){
           throw new RuntimeException("message不能为空");
       }
        User user = (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        String context = chatApp.doChatWithRag(message, user.getId().toString());
        AiChatVO aiChatVO = new AiChatVO();
        aiChatVO.setConversationId(user.getId().toString());
        aiChatVO.setMessageType(MessageType.ASSISTANT);
        aiChatVO.setContent(context);
        aiChatVO.setCreateTime(new Date());
        return ResultUtils.success(aiChatVO);
    }

    /*
     * @param null:
      * @return null
     * @author liuya
     * @description 获取对话记录
     * @date 2025/12/11 上午9:45
     */
    @PostMapping("/getChatHistory")
    public BaseResponse<List<AiChatVO>> getChatHistory(@RequestBody AiChatListRequest aiChatListRequest,HttpServletRequest request){
        if(aiChatListRequest==null){
            throw new RuntimeException("参数不能为空");
        }
        Long num = aiChatListRequest.getNum();
        User user = (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        // 3. 构建查询条件：按创建时间倒序，分页获取最新的num条
        // Page(当前页, 每页大小)：第一页，每页num条
        Page<AiMessage> page = new Page<>(1, num);
        QueryWrapper<AiMessage> queryWrapper = new QueryWrapper<AiMessage>()
                .eq("conversation_id", user.getId()) // 注意：这里conversation_id存的是user.getId()
                .orderByDesc("create_time"); // 倒序（最新的在前）

        // 4. 分页查询（MyBatis-Plus的分页查询，自动拼接limit，避免SQL注入）
        IPage<AiMessage> aiMessagePage = aiMessageService.page(page, queryWrapper);
        List<AiMessage> aiMessages = aiMessagePage.getRecords();

        // 5. 如果需要返回“从旧到新”的顺序，反转列表（可选）
        Collections.reverse(aiMessages);

        List<AiChatVO> aiChatVOs = aiMessages.stream().map(aiMessage -> {
            Date createTime = aiMessage.getCreateTime();
            MessageType messageType = aiMessage.getMessageType();
            String content = aiMessage.getContent();
            String conversationId = aiMessage.getConversationId();
            AiChatVO aiChatVO = new AiChatVO();
            aiChatVO.setConversationId(conversationId);
            aiChatVO.setMessageType(messageType);
            aiChatVO.setContent(content);
            aiChatVO.setCreateTime(createTime);
            return aiChatVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(aiChatVOs);
    }



}
