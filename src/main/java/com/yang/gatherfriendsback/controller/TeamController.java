package com.yang.gatherfriendsback.controller;

import com.yang.gatherfriendsback.common.BaseResponse;
import com.yang.gatherfriendsback.common.ErrorCode;
import com.yang.gatherfriendsback.common.ResultUtils;

import com.yang.gatherfriendsback.exception.BusinessException;
import com.yang.gatherfriendsback.model.domain.Team;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.request.TeamMatchCarAddRequest;
import com.yang.gatherfriendsback.model.request.TeamQueryRequest;

import com.yang.gatherfriendsback.model.vo.TeamUserVO;
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
@RequestMapping("/team")
@Tag(name = "队伍接口")
public class TeamController {
    private static final Logger log = LoggerFactory.getLogger(TeamController.class);
    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;
    //创建队伍
    @Operation(summary = "创建队伍")
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody Team team, HttpServletRequest request) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long result = teamService.createTeam(team, request);
        return ResultUtils.success( result);
    }

    @Operation(summary = "查询队伍")
    @PostMapping("/search")
    public BaseResponse<List<TeamUserVO>> searchTeam(@RequestBody TeamQueryRequest teamQueryRequest , HttpServletRequest request) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        List<TeamUserVO> result = teamService.searchTeam(teamQueryRequest, loginUser);
        return ResultUtils.success(result);
    }

    @Operation(summary = "根据id获取队伍")
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(@RequestParam Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getTeamById(id);
        return ResultUtils.success(team);
    }
    @Operation(summary = "更新队伍")
    @PostMapping("/update")
    public BaseResponse<Integer> updateTeam(@RequestBody Team team) {
        if (team == null || team.getId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer result = teamService.updateTeam(team);
        return ResultUtils.success(result);
    }

    @Operation(summary = "加入队伍")
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestParam Long teamId , HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam( teamId, loginUser);
        return ResultUtils.success(result);
    }

    @Operation(summary = "退出队伍")
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestParam Long teamId , HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam( teamId, loginUser);
        return ResultUtils.success(result);
    }
    //创建队伍
    @Operation(summary = "拼车队伍添加")
    @PostMapping("/addMatchCar")
    public BaseResponse<Long> addMatchCar(@RequestBody TeamMatchCarAddRequest teamMatchCarAddRequest, HttpServletRequest request) {
        if (teamMatchCarAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long result = teamService.addCarTeam(teamMatchCarAddRequest, request);
        return ResultUtils.success( result);
    }



    @Operation(summary = "拼车队伍获取")
    @GetMapping("/getMatchCar")
    public BaseResponse<List<TeamUserVO>> getMatchCar(@RequestParam String longitude ,  @RequestParam String latitude , HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<TeamUserVO> result = teamService.getMatchCar( longitude, latitude, loginUser);

        return ResultUtils.success( result);
    }

}
