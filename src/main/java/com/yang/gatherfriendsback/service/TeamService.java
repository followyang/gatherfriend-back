package com.yang.gatherfriendsback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yang.gatherfriendsback.model.domain.Team;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.request.TeamJoinRequest;
import com.yang.gatherfriendsback.model.request.TeamMatchCarAddRequest;
import com.yang.gatherfriendsback.model.request.TeamQueryRequest;
import com.yang.gatherfriendsback.model.vo.TeamUserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
* @author liuya
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-10-13 00:43:01
*/

@Service
public interface TeamService extends IService<Team> {


    /**
     * @param team:
    	 * @param request:
      * @return Long
     * @author liuya
     * @description 创建队伍
     * @date 2025/11/19 下午3:48
     */

        @Transactional
        Long createTeam(Team team, HttpServletRequest request);

    /**
         * 查询队伍
         * @param teamQueryRequest
         * @param loginUser
         * @return
         */
        List<TeamUserVO> searchTeam(TeamQueryRequest teamQueryRequest, User loginUser);


        /**
         * 根据id获取队伍信息
         * @return
         */
    Team getTeamById(Long id);

    /**
     * 更新队伍信息
     *
     * @param team
     * @return
     */
    Integer updateTeam(Team team);

    /**
     * 更新队伍信息
     *
     * 加入队伍
     * @return
     */
    boolean joinTeam(Long teamId, User loginUser);

    /**
     * 退出队伍
     *
     * @return
     */
    boolean quitTeam(Long teamId, User loginUser);

    /*
    * 创建拼车队伍
    * */
    Long addCarTeam(TeamMatchCarAddRequest teamMatchCarAddRequest, HttpServletRequest request);

    /*
    * 获取拼车队伍
    * */
    List<TeamUserVO> getMatchCar(String longitude, String latitude, User loginUser);


}

