package com.yang.gatherfriendsback.controller;


import com.yang.gatherfriendsback.common.BaseResponse;
import com.yang.gatherfriendsback.common.ErrorCode;
import com.yang.gatherfriendsback.common.ResultUtils;
import com.yang.gatherfriendsback.exception.BusinessException;
import com.yang.gatherfriendsback.model.domain.Team;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.request.MessageRequest;
import com.yang.gatherfriendsback.model.request.TeamQueryRequest;
import com.yang.gatherfriendsback.model.vo.ChatMessageVO;
import com.yang.gatherfriendsback.model.vo.TeamUserVO;
import com.yang.gatherfriendsback.service.ChatService;
import com.yang.gatherfriendsback.service.TeamService;
import com.yang.gatherfriendsback.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@Tag(name = "聊天接口")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(TeamController.class);

    @Autowired
    private ChatService chatService;
    @Operation(summary = "用户聊天接口")
    @GetMapping("/user")
    public BaseResponse<List<ChatMessageVO>> listUserAges( @RequestParam Long fromId, @RequestParam Long toId) {

        List<ChatMessageVO> result = chatService.listUserAges(fromId, toId);
        return ResultUtils.success(result);
    }

    @Operation(summary = "队伍聊天接口")
    @GetMapping("/team")
    public BaseResponse<List<ChatMessageVO>> listTeamAges( @RequestParam Long fromId, @RequestParam Long teamId) {

        List<ChatMessageVO> result = chatService.listTeamAges(fromId, teamId);
        return ResultUtils.success(result);
    }


}
