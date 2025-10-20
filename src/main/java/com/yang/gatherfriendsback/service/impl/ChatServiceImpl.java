package com.yang.gatherfriendsback.service.impl;


import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yang.gatherfriendsback.mapper.ChatMapper;
import com.yang.gatherfriendsback.mapper.UserMapper;
import com.yang.gatherfriendsback.mapper.UserTeamMapper;
import com.yang.gatherfriendsback.model.domain.Chat;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.domain.UserTeam;
import com.yang.gatherfriendsback.model.vo.ChatMessageVO;
import com.yang.gatherfriendsback.model.vo.UserVO;
import com.yang.gatherfriendsback.service.ChatService;
import com.yang.gatherfriendsback.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author liuya
* @description 针对表【chat(聊天消息表)】的数据库操作Service实现
* @createDate 2025-10-17 16:00:07
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private UserTeamMapper userTeamMapper;

    @Override
    public List<ChatMessageVO> listUserAges(Long fromId, Long toId) {

        User fromUser = userMapper.selectById(fromId);
        UserVO fromUserVO = new UserVO();
        BeanUtils.copyProperties(fromUser, fromUserVO);

        User toUser =  userMapper.selectById(toId);
        UserVO toUserVO = new UserVO();
        BeanUtils.copyProperties(toUser, toUserVO);

        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromId", fromId).eq("toId", toId)
                .or()
                .eq("fromId", toId).eq("toId", fromId);
        queryWrapper.last("order by createTime asc");
        List<Chat> chatList = chatMapper.selectList(queryWrapper);

        List<ChatMessageVO> chatMessageVOList = chatList.stream().map(chat -> {
            ChatMessageVO chatMessageVO = new ChatMessageVO();
            chatMessageVO.setFromUser(fromUserVO);
            chatMessageVO.setToUser(toUserVO);
            BeanUtils.copyProperties(chat, chatMessageVO);
            chatMessageVO.setIsMy(chat.getFromId()==fromId);
            String sendTime = DateUtil.format(chat.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
            chatMessageVO.setCreateTime(sendTime);
            return chatMessageVO;
        }).toList();

        return chatMessageVOList;
     }

     /*
     * 群聊历史
     * */
    @Override
    public List<ChatMessageVO> listTeamAges(Long fromId, Long teamId) {

        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        queryWrapper.last("order by createTime asc");
        List<Chat> chatList = chatMapper.selectList(queryWrapper);


        List<ChatMessageVO> chatMessageVOList = chatList.stream().map(chat -> {

        Long chatFromId = chat.getFromId();

        User fromUser =  userMapper.selectById(chatFromId);
        UserVO fromUserVO = new UserVO();
        BeanUtils.copyProperties(fromUser, fromUserVO);

        ChatMessageVO chatMessageVO = new ChatMessageVO();
        if(chatFromId.equals(fromId)){
            chatMessageVO.setIsMy(true);
        }
        else{
            chatMessageVO.setIsMy(false);
        }
        chatMessageVO.setFromUser(fromUserVO);
        BeanUtils.copyProperties(chat, chatMessageVO);
        String sendTime = DateUtil.format(chat.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
        chatMessageVO.setCreateTime(sendTime);
        if(fromUser.getUserRole()!=1){
           chatMessageVO.setIsAdmin( true);
        }else{
            chatMessageVO.setIsAdmin(false);
        }

        return chatMessageVO;
        }).toList();
        return chatMessageVOList;
    }

}




