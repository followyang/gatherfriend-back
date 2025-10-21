package com.yang.gatherfriendsback.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yang.gatherfriendsback.common.ErrorCode;
import com.yang.gatherfriendsback.constant.UserConstant;
import com.yang.gatherfriendsback.exception.BusinessException;
import com.yang.gatherfriendsback.mapper.UserTeamMapper;
import com.yang.gatherfriendsback.model.domain.Team;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.domain.UserTeam;
import com.yang.gatherfriendsback.model.request.TeamMatchCarAddRequest;
import com.yang.gatherfriendsback.model.request.TeamQueryRequest;
import com.yang.gatherfriendsback.model.vo.TeamUserVO;
import com.yang.gatherfriendsback.model.vo.UserVO;
import com.yang.gatherfriendsback.service.TeamService;
import com.yang.gatherfriendsback.mapper.TeamMapper;
import com.yang.gatherfriendsback.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author liuya
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2025-10-13 00:43:01
*/

@Service
public class TeamServiceImpl extends  ServiceImpl<TeamMapper, Team>
            implements TeamService {

       @Autowired
        private TeamMapper teamMapper;

       @Autowired
        private UserService userService;

       @Autowired
       private UserTeamMapper userTeamMapper;

       @Autowired
       private RedisTemplate<String, Object> redisTemplate;

        //创建队伍
        @Override
        @Transactional
        public Long addTeam( Team team, HttpServletRequest  request) {
            User loginUser = userService.getLoginUser(request);

            if (team == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (loginUser == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN);
            }
            Long userId = loginUser.getId();
            team.setUserId(userId);
            team.setAvatar(loginUser.getAvatarUrl());

            //插入数据返回id
            teamMapper.insert(team);

            //插入数据后 team.getId() 获取到插入数据的id
            Long result = team.getId();

            Integer userRole = loginUser.getUserRole();

            UserTeam userTeam = new UserTeam();
            //管理员具有所有权限，包括创建者，加入队伍者
            if (userRole == UserConstant.ADMIN_ROLE) {
                userTeam.setRole(2);
            } else {
                userTeam.setRole(1);
            }
            userTeam.setUserId(userId);
            userTeam.setTeamId(result);
            userTeam.setStatus(0);
            userTeamMapper.insert(userTeam);
            return result;
        }

        //查询队伍
        @Override
        public List<TeamUserVO> searchTeam(TeamQueryRequest teamQueryRequest, User loginUser) {
            if (teamQueryRequest == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            String description = teamQueryRequest.getDescription();
            String name = teamQueryRequest.getName();
            List<String> tags = teamQueryRequest.getTags();
            LocalDateTime expireTime = teamQueryRequest.getExpireTime();
            Integer status = teamQueryRequest.getStatus();


            Long pageNum = teamQueryRequest.getPageNum();
            Long pageSize = teamQueryRequest.getPageSize();

            QueryWrapper<Team> queryWrapper = new QueryWrapper();
            if(name!= null&&!name.equals("")){
                queryWrapper.like("name",name);
            }
            if(description!= null&&!description.equals("")){
                queryWrapper.like("description",description);
            }
            if(tags!= null&&tags.size()>0){
                queryWrapper.like("tags", String.join(",", tags));
            }

            if(teamQueryRequest.getMaxNum()!= null&&teamQueryRequest.getMaxNum()>0){
                Long maxNum = Long.valueOf(teamQueryRequest.getMaxNum());
                queryWrapper.eq("maxNum",maxNum);
            }
            LocalDateTime now = LocalDateTime.now();
            if(expireTime!= null&&expireTime.isAfter(now)){
                queryWrapper.gt("expireTime",expireTime);
            }
            Integer userRole = loginUser.getUserRole();
            // 用户不是管理员，不能搜索到私密房间
            if(userRole!= UserConstant.ADMIN_ROLE){
                if(status!= null&&status==3){
                    queryWrapper.eq("status",status);
                }
                if(status!= null&&status==0){
                    queryWrapper.eq("status",status);
                }
            }
            else{
                if (status != null) {
                    queryWrapper.eq("status", status);
                }
            }
            List<Team> teamList = teamMapper.selectList(queryWrapper);
            if(CollectionUtils.isEmpty(teamList)){
                return new ArrayList<>();
            }
            List<TeamUserVO> teamUserVOList = teamList.stream().map(team -> {
                Long teamUserId = team.getUserId();
                if(teamUserId == null){
                    throw  new BusinessException( ErrorCode.SYSTEM_ERROR);
                }

                User user = userService.getById(teamUserId);
                TeamUserVO teamUserVO = new TeamUserVO();
                BeanUtils.copyProperties(team,teamUserVO);
                teamUserVO.setTags(team.getTags());
                //用户信息脱敏
                if(user != null){
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(user,userVO);
                    teamUserVO.setCreateUser(userVO);
                }
                //获取已加入的队伍人数
                QueryWrapper<UserTeam> userTeamQueryWrapper1 = new QueryWrapper<>();
                userTeamQueryWrapper1.eq("teamId",team.getId());

                teamUserVO.setHasJoinNum(Math.toIntExact(userTeamMapper.selectCount(userTeamQueryWrapper1)));

                //判断当前用户是否已加入该队伍
                Long userId = loginUser.getId();
                QueryWrapper<UserTeam> userTeamQueryWrapper2 = new QueryWrapper<>();
                userTeamQueryWrapper2.eq("teamId",team.getId()).isNotNull("userId").eq("userId",userId);
                List<UserTeam> userTeamList = userTeamMapper.selectList(userTeamQueryWrapper2);
                teamUserVO.setHasJoin(false);
                if(userTeamList.size()>0){
                    teamUserVO.setHasJoin(true);
                }


                return teamUserVO;
            }).collect(Collectors.toList());

            return teamUserVOList;
        }

        /*
        * 根据id获取队伍信息
        * */
    @Override
    public Team getTeamById(Long id) {
            return teamMapper.selectById(id);
    }

    @Override
    public Integer updateTeam(Team team) {
       return teamMapper.updateById( team);
    }

    @Override
    public boolean joinTeam(Long teamId, User loginUser) {

        if(teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId).eq("userId",userId);

        if(userTeamMapper.selectOne(queryWrapper) != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能重复加入");
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setStatus(0);
        userTeam.setJoinTime(new Date());
        userTeamMapper.insert(userTeam);


        return true;
    }

    @Override
    public boolean quitTeam(Long teamId, User loginUser) {
        if(teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId).eq("userId",userId);
        UserTeam userTeam = userTeamMapper.selectOne(queryWrapper);
        if(userTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入该队伍");
        }
        userTeamMapper.delete(queryWrapper);
        return true;
    }

    /*
    * 发布拼车队伍
    * */
    @Override
    public Long addCarTeam(TeamMatchCarAddRequest teamMatchCarAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();

        BeanUtils.copyProperties(teamMatchCarAddRequest,team);

        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = loginUser.getId();
        team.setUserId(userId);
        team.setAvatar(loginUser.getAvatarUrl());

        //插入数据返回id
        teamMapper.insert(team);

        //插入数据后 team.getId() 获取到插入数据的id
        Long result = team.getId();

        Integer userRole = loginUser.getUserRole();

        UserTeam userTeam = new UserTeam();
        //管理员具有所有权限，包括创建者，加入队伍者
        if (userRole == UserConstant.ADMIN_ROLE) {
            userTeam.setRole(2);
        } else {
            userTeam.setRole(1);
        }
        userTeam.setUserId(userId);
        userTeam.setTeamId(result);
        userTeam.setStatus(0);
        userTeamMapper.insert(userTeam);
        Double longitude = Double.parseDouble(teamMatchCarAddRequest.getLongitude());
        Double latitude = Double.parseDouble(teamMatchCarAddRequest.getLatitude());

        //添加队伍id到地理位置
        redisTemplate.opsForGeo().add("carpool:hosts",new Point(longitude,latitude),result);
        return result;
    }


}
