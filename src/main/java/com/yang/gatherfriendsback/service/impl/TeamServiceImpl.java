package com.yang.gatherfriendsback.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yang.gatherfriendsback.common.ErrorCode;
import com.yang.gatherfriendsback.constant.UserConstant;
import com.yang.gatherfriendsback.enums.TeamStatusEnum;
import com.yang.gatherfriendsback.exception.BusinessException;
import com.yang.gatherfriendsback.mapper.UserMapper;
import com.yang.gatherfriendsback.mapper.UserTeamMapper;
import com.yang.gatherfriendsback.model.domain.Team;
import com.yang.gatherfriendsback.model.domain.User;
import com.yang.gatherfriendsback.model.domain.UserTeam;
import com.yang.gatherfriendsback.model.request.TeamJoinRequest;
import com.yang.gatherfriendsback.model.request.TeamMatchCarAddRequest;
import com.yang.gatherfriendsback.model.request.TeamQueryRequest;
import com.yang.gatherfriendsback.model.vo.TeamUserVO;
import com.yang.gatherfriendsback.model.vo.UserVO;
import com.yang.gatherfriendsback.service.TeamService;
import com.yang.gatherfriendsback.mapper.TeamMapper;
import com.yang.gatherfriendsback.service.UserService;
import com.yang.gatherfriendsback.service.UserTeamService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author liuya
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2025-10-13 00:43:01
*/

@Service
@Slf4j
public class TeamServiceImpl extends  ServiceImpl<TeamMapper, Team>
            implements TeamService {

       @Autowired
        private TeamMapper teamMapper;

       @Autowired
        private UserService userService;

       @Autowired
       private UserTeamMapper userTeamMapper;

       @Autowired
       private UserTeamService userTeamService;

       @Autowired
       private UserMapper userMapper;

       @Autowired
       private RedissonClient redissonClient;

       @Autowired
       private RedisTemplate<String, Object> redisTemplate;

       private static final String GEO_KEY = "carpool:hosts";
       private static final String EXPIRE_PREFIX = "carpool:hosts:expire:";



    //创建队伍
        @Override
        @Transactional
        public Long createTeam(Team team, HttpServletRequest request) {
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
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        LocalDateTime expireTime = team.getExpireTime();
        if (expireTime!=null&&expireTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (teamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }


        //该用户已加入的队伍数量 数据库查询所以放到下面，减少查询时间
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入5个队伍");
        }

        RLock lock = redissonClient.getLock("gather:lock:join_team");
        while(true){
            try {
                if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                //不能重复加入已加入的队伍
                userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("userId", userId);
                userTeamQueryWrapper.eq("teamId", teamId);
                long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
                if (hasUserJoinTeam > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
                }
                //已加入队伍的人数
                userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
                if (teamHasJoinNum >= team.getMaxNum()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                }


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
            } catch (InterruptedException e) {
                log.error("join_team error");
                throw new RuntimeException(e);
            }
            finally {
                //释放掉自己的锁
                if(lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
        }
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
        LocalDateTime expireTime = teamMatchCarAddRequest.getExpireTime();

        //添加地理位置房主，已teamId分类
        addCarTeamLocation(longitude,latitude,result,expireTime);

        return result;
    }

    /*
    * 获取拼车匹配的队伍
    * */
    @Override
    public List<TeamUserVO> getMatchCar(String longitude, String latitude, User loginUser) {
        Double longitude1 = Double.parseDouble(longitude);
        Double latitude1 = Double.parseDouble(latitude);

        double radius = 1;//1km
        List<Long> teamIds = findNearestHost(longitude1, latitude1, radius);

        log.info("teamId{}",!teamIds.isEmpty()?teamIds.size():null);

        List<TeamUserVO> teamUserVOList = List.of();
        if (teamIds.size() > 0 && teamIds != null) {
            List<Team> teams = teamMapper.selectBatchIds(teamIds);
            teamUserVOList = teams.stream().map(team -> {
                TeamUserVO teamUserVO = new TeamUserVO();
                BeanUtils.copyProperties(team, teamUserVO);
                teamUserVO.setTags(team.getTags());

                User user = userMapper.selectById(team.getUserId());
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                userVO.setTags(user.getTags());
                teamUserVO.setCreateUser(userVO);
                teamUserVO.setCreateUser(userVO);
                return teamUserVO;
            }).toList();
        }
        return teamUserVOList ;
    }


    //添加队伍id到地理位置 设置过期时间 等业务
    public  void addCarTeamLocation(Double longitude ,Double latitude ,Long teamId, LocalDateTime expireTime){

        redisTemplate.opsForGeo().add("carpool:hosts",new Point(longitude,latitude),teamId);

        //设置过期key
        String expireKey = EXPIRE_PREFIX+teamId;
        redisTemplate.opsForValue().set(expireKey,expireTime);

        //设置过期key的过期时间
        redisTemplate.expire(expireKey, Timestamp.valueOf(expireTime).getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);


    }
   /**
     * 查找最近的房主（自动过滤已过期的成员）
     */
    public List<Long> findNearestHost(double userLon, double userLat, double radius) {

        // 2. 定义搜索范围：以用户位置为中心，radius 公里为半径
        Distance distance = new Distance(radius, Metrics.KILOMETERS);

        // 3. 执行地理搜索：获取范围内的队伍，按距离升序，最多返回10条
        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = redisTemplate.opsForGeo().search(
                GEO_KEY,
                GeoReference.fromCoordinate(userLon, userLat),
                distance,
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoSearchArgs().limit(20)
        );

        // 遍历搜索结果，找到第一个未过期的队伍
        List<Long> teamIds = new ArrayList<>();

        // 4. 处理搜索结果：过滤过期队伍，返回最近的有效队伍
        if (geoResults == null || geoResults.getContent().isEmpty()|| geoResults.getContent().size() == 0) {
            return List.of(); // 范围内无任何队伍
        }

        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> result : geoResults.getContent()) {
            RedisGeoCommands.GeoLocation<Object> location = result.getContent();
            // 修复类型转换问题：直接转换为Long而不是String
            Long teamId;
            Object nameObj = location.getName();
            
            // 根据实际类型进行转换
            if (nameObj instanceof String) {
                try {
                    teamId = Long.parseLong((String) nameObj);
                } catch (NumberFormatException e) {
                    // 无效的teamId格式，从GEO中清理并继续
                    redisTemplate.opsForGeo().remove(GEO_KEY, nameObj);
                    continue;
                }
            } else if (nameObj instanceof Long) {
                teamId = (Long) nameObj;
            } else {
                // 不支持的类型，从GEO中清理并继续
                redisTemplate.opsForGeo().remove(GEO_KEY, nameObj);
                continue;
            }

            // 检查队伍是否过期：判断过期标记key是否存在
            String expireKey = EXPIRE_PREFIX + teamId;
            if (redisTemplate.hasKey(expireKey)) {
                // 未过期，直接返回该队伍（因已按距离排序，第一个有效即为最近）
                teamIds.add(teamId);
            } else {
                // 已过期，从GEO中清理该队伍，继续检查下一个
                redisTemplate.opsForGeo().remove(GEO_KEY, nameObj);
            }
        }

        // 5. 所有搜索到的队伍都已过期，返回
        return teamIds;
}
    /**
     * 定时清理已过期的房主
     */
    @Scheduled(cron = "0 * * * * ?")
    public void cleanExpiredHosts() {
        System.out.println("开始清理已过期的队伍房主...");
        // 使用 ZSCAN 遍历有序集合（GEO 底层的 Sorted Set）
        Cursor<ZSetOperations.TypedTuple<Object>> cursor = redisTemplate.opsForZSet().scan(GEO_KEY, ScanOptions.NONE);
        while (cursor.hasNext()) {
            ZSetOperations.TypedTuple<Object> tuple = cursor.next();
            Long hostId = (Long) tuple.getValue();
            System.out.println("Redis中的值: " + hostId + "，类型: " + hostId.getClass().getName());
            String expireKey = EXPIRE_PREFIX + hostId;

            if (!redisTemplate.hasKey(expireKey)) {
                redisTemplate.opsForGeo().remove(GEO_KEY, hostId);
                teamMapper.deleteById(hostId);
            }


        }
    }

}
