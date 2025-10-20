package com.yang.gatherfriendsback.websoket;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.yang.gatherfriendsback.config.GetHttpSessionConfig;
import com.yang.gatherfriendsback.constant.UserConstant;
import com.yang.gatherfriendsback.model.domain.Chat;
import com.yang.gatherfriendsback.model.domain.Team;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.domain.UserTeam;
import com.yang.gatherfriendsback.model.request.MessageRequest;
import com.yang.gatherfriendsback.model.vo.ChatMessageVO;
import com.yang.gatherfriendsback.model.vo.UserVO;
import com.yang.gatherfriendsback.service.ChatService;
import com.yang.gatherfriendsback.service.TeamService;
import com.yang.gatherfriendsback.service.UserService;
import com.yang.gatherfriendsback.service.UserTeamService;
import com.yang.gatherfriendsback.service.impl.ChatServiceImpl;
import com.yang.gatherfriendsback.service.impl.UserTeamServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import cn.hutool.core.date.DateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfig.class)
@Component
@Slf4j
public class ChatEndpoint {
    // 存储在线用户的会话，key为用户id，value为会话
    private static final Map<Long, Session> ONLINE_USER_SESSIONS = new ConcurrentHashMap<>();

    private static ApplicationContext applicationContext;
    private UserTeamServiceImpl userTeamServiceImpl;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        ChatEndpoint.applicationContext = applicationContext;
    }

    private UserService getUserService() {
        return applicationContext.getBean(UserService.class);
    }

    private ChatService getChatService() {
        return applicationContext.getBean(ChatService.class);
    }
    private TeamService getTeamService() {
        return applicationContext.getBean(TeamService.class);
    }

    private UserTeamService getUserTeamService() {return applicationContext.getBean(UserTeamService.class);}

    //建立连接时
    @OnOpen
    public void onOpen(Session session) {
        try {
            // 从会话属性中获取HttpSession
            HttpSession httpSession = (HttpSession) session.getUserProperties().get(HttpSession.class.getName());
            User user = (User) httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
            if (user != null) {
                // 将用户会话存储起来
                ONLINE_USER_SESSIONS.put(user.getId(), session);
                log.info("用户连接成功: {}, 当前在线人数: {}", user.getId(), ONLINE_USER_SESSIONS.size());
            } else {
                log.warn("用户未登录，关闭连接");
                try {
                    session.close();
                } catch (IOException e) {
                    log.error("关闭未登录用户连接失败", e);
                }
            }
        } catch (Exception e) {
            log.error("用户连接处理异常", e);
            try {
                session.close();
            } catch (IOException ioException) {
                log.error("关闭异常连接失败", ioException);
            }
        }
    }

    //接收客户端消息
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            log.info("收到消息: {}", message);

            // 从会话属性中获取HttpSession
            HttpSession httpSession = (HttpSession) session.getUserProperties().get(HttpSession.class.getName());
            User user = (User) httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);

            // 解析消息
            MessageRequest messageRequest = new Gson().fromJson(message, MessageRequest.class);
            if (messageRequest == null) {
                log.warn("消息解析失败，消息内容: {}", message);
                return;
            }

            Long fromId = messageRequest.getFromId();
            Long toId = messageRequest.getToId();
            String text = messageRequest.getText();
            Integer type = messageRequest.getType();
            Long teamId = messageRequest.getTeamId();

            log.info("解析消息成功 - fromId: {}, toId: {}, toTeamId{} text: {}, type: {}", fromId, toId, teamId, text, type);

            if (user == null) {
                log.warn("用户未登录");
                return;
            }

            if (type == 1) {
                sendMessageToUser(fromId, toId, text);
            }
            if (type == 2) {
                sendMessageToTeam(fromId, teamId, text);
            }


        } catch (Exception e) {
            log.error("处理消息异常: {}", e.getMessage(), e);
            // 不关闭连接，只是记录错误
        }
    }

    private void sendMessageToUser(Long fromId, Long toId, String text) {
        try {
            log.info("开始发送消息 - fromId: {}, toId: {}, text: {}", fromId, toId, text);

            // 获取用户信息
            UserService userService = getUserService();
            User toUser = userService.getById(toId);
            if (toUser == null) {
                log.warn("目标用户 {} 不存在", toId);
                return;
            }

            User fromUser = userService.getById(fromId);
            if (fromUser == null) {
                log.warn("发送者用户 {} 不存在", fromId);
                return;
            }

            //存入数据库
            Chat chat = new Chat();
            chat.setFromId(fromId);
            chat.setToId(toId);
            chat.setText(text);
            chat.setMessageType("text");
            chat.setChatType(1);
            chat.setIsRead(0);
            chat.setCreateTime(DateUtil.date(System.currentTimeMillis()));
            ChatService chatService = getChatService();
            chatService.save(chat);

            // 获取目标用户会话
            Session toSession = ONLINE_USER_SESSIONS.get(toId);
            if (toSession == null) {
                log.warn("目标用户 {} 不在线，只存数据库", toId);
                return;
            }

            if (!toSession.isOpen()) {
                log.warn("目标用户 {} 会话已关闭，存数据库，移除会话", toId);
                ONLINE_USER_SESSIONS.remove(toId);
                return;
            }

            // 封装消息
            ChatMessageVO chatMessageVO = new ChatMessageVO();

            UserVO targetFromUser = new UserVO();
            BeanUtils.copyProperties(fromUser, targetFromUser);
            chatMessageVO.setFromUser(targetFromUser);

            UserVO targetToUser = new UserVO();
            BeanUtils.copyProperties(toUser, targetToUser);
            chatMessageVO.setToUser(targetToUser);

            chatMessageVO.setText(text);
            String sendTime = DateUtil.date(System.currentTimeMillis()).toString();
            String date = sendTime.split(" ")[0];
            String time = sendTime.split(" ")[1];
            chatMessageVO.setCreateTime(date + " " + time);


            if (fromId.equals(toId)) {
                chatMessageVO.setIsMy(true);
            } else {
                chatMessageVO.setIsMy(false);
            }

            chatMessageVO.setMessageType("text");
            chatMessageVO.setChatType(1);

            if (fromUser.getUserRole() == UserConstant.ADMIN_ROLE) {
                chatMessageVO.setIsAdmin(true);
            } else {
                chatMessageVO.setIsAdmin(false);
            }


            chatMessageVO.setTeamId(null);

            // 修复JSON序列化问题
            String jsonChatMessageVO = new Gson().toJson(chatMessageVO);
            log.info("准备发送消息: {}", jsonChatMessageVO);

            // 发送消息
            toSession.getBasicRemote().sendText(jsonChatMessageVO);
            log.info("消息发送成功 - 目标用户: {}", toId);

        } catch (Exception e) {
            log.error("用户发送消息异常 - fromId: {}, toId: {}, text: {}, 错误: {}", fromId, toId, text, e.getMessage(), e);
        }
    }

    private void sendMessageToTeam(Long fromId, Long teamId, String text) {
        try {
            log.info("开始发送消息 - fromId: {}, teamId: {}, text: {}", fromId, teamId, text);
            Chat chat = new Chat();
            chat.setFromId(fromId);
            chat.setTeamId(teamId);
            chat.setText(text);
            chat.setMessageType("text");
            chat.setChatType(2);
            chat.setIsRead(0);
            chat.setCreateTime(DateUtil.date(System.currentTimeMillis()));

            ChatService chatService = getChatService();
            chatService.save(chat);

            TeamService teamService = getTeamService();
            Team team = teamService.getTeamById(teamId);
            if (team == null) {
                log.warn("群组 {} 不存在", teamId);
                return;
            }
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId", teamId).isNotNull("userId").ne("userId", fromId);
            UserTeamService userTeamService = getUserTeamService();
            List<UserTeam> userTeamList = userTeamService.list(queryWrapper);

            List<Long> userIdList =userTeamList.stream().map(UserTeam::getUserId).toList();

            UserService userService = getUserService();
            User fromUser = userService.getById(fromId);
             for (Long userId : userIdList) {
                 Session toSession = ONLINE_USER_SESSIONS.get(userId);

                 User toUser = userService.getById(userId);
                 if (toUser == null) {
                     log.warn("用户 {} 不存在", userId);
                     continue;
                 }
                 if (toSession == null) {
                     log.warn("用户 {} 不在线，只存数据库", userId);
                     continue;
                 }
                 if (!toSession.isOpen()) {
                     log.warn("用户 {} 会话已关闭，存数据库，移除会话", userId);
                     ONLINE_USER_SESSIONS.remove(userId);
                     continue;
                 }
                 ChatMessageVO chatMessageVO = new ChatMessageVO();
                 UserVO toUserVO = new UserVO();
                 BeanUtils.copyProperties(toUser, toUserVO);
                 chatMessageVO.setToUser(toUserVO);

                 UserVO fromUserVO = new UserVO();
                 BeanUtils.copyProperties(fromUser, fromUserVO);
                 chatMessageVO.setFromUser(fromUserVO);
                 chatMessageVO.setText(text);
                 String sendTime = DateUtil.date(System.currentTimeMillis()).toString();
                 String date = sendTime.split(" ")[0];
                 String time = sendTime.split(" ")[1];
                 chatMessageVO.setCreateTime(date + " " + time);
                 chatMessageVO.setMessageType("text");
                 chatMessageVO.setChatType(2);
                 chatMessageVO.setTeamId(teamId);

                 //发送给除自己以外的人
                 chatMessageVO.setIsMy(false);

                 if(toUser.getUserRole() == UserConstant.ADMIN_ROLE){
                     chatMessageVO.setIsAdmin(true);
                 }
                 else{
                     chatMessageVO.setIsAdmin(false);
                 }

                 String jsonChatMessageVO = new Gson().toJson(chatMessageVO);
                 toSession.getBasicRemote().sendText(jsonChatMessageVO);
             }
        } catch (Exception e) {
            log.error("群聊发送消息异常 - fromId: {}, teamId:{} text: {}, 错误: {}", fromId,teamId , text, e.getMessage(), e);
        }
    }

    //链接关闭时
    @OnClose
    public void onClose(Session session) {
        try {
            // 从会话属性中获取HttpSession
            HttpSession httpSession = (HttpSession) session.getUserProperties().get(HttpSession.class.getName());
            User user = (User) httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
            if (user != null) {
                // 移除用户会话
                ONLINE_USER_SESSIONS.remove(user.getId());
                log.info("用户 {} 断开连接，当前在线人数: {}", user.getId(), ONLINE_USER_SESSIONS.size());
            }
        } catch (Exception e) {
            log.error("处理连接关闭异常", e);
        }
    }

    //链接出错时
    @OnError
    public void onError(Session session, Throwable throwable) {
        try {
            // 从会话属性中获取HttpSession
            HttpSession httpSession = (HttpSession) session.getUserProperties().get(HttpSession.class.getName());
            User user = (User) httpSession.getAttribute(UserConstant.USER_LOGIN_STATE);
            if (user != null) {
                ONLINE_USER_SESSIONS.remove(user.getId());
                log.error("用户 {} WebSocket连接出错: {}", user.getId(), throwable.getMessage(), throwable);
            } else {
                log.error("WebSocket连接出错: {}", throwable.getMessage(), throwable);
            }
        } catch (Exception e) {
            log.error("处理WebSocket错误异常", e);
        }
    }

}

