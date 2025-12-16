
create
database if not exists gatherfriends;

use
    gatherfriends;

-- 用户表
create table if not exists gatherfriends.user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    profile      varchar(1024)                      null comment '简介',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    tags         varchar(1024)                      null comment '标签 json 列表',
    isOnline     tinyint                            null comment '用户是否在线 0：下线，1：上线'
);

INSERT INTO gatherfriends.user (username, userAccount, avatarUrl, gender, userPassword, profile, phone, email, userStatus, createTime, updateTime, isDelete, userRole, tags, isOnline)
VALUES
    ('旭', 'aaaaa', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', 2, 'df22ca4b212cd00566e37834224d1648', '热爱跑步和摄影，记录生活中的美好瞬间', '13800138001', 'aaaaa@example.com', 0, '2025-10-02 18:05:20', '2025-10-16 16:00:29', 0, 1, '[]', NULL),
    ('哇哈哈哈', 'bbbbb', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.Bk9P-YAQk0Dw0fGgNiRkGgAAAA?w=150&h=108&c=7&bgcl=e80b49&r=0&o=6&cb=12&dpr=1.3&pid=13.1', 1, 'df22ca4b212cd00566e37834224d1648', '运动达人，喜欢跑步、摄影和篮球', '13800138002', 'bbbbb@example.com', 0, '2025-10-02 18:38:22', '2025-10-14 20:50:10', 0, 0, '["跑步","摄影","篮球"]', NULL),
    ('不阿布阿布', 'ccccc', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.Bk9P-YAQk0Dw0fGgNiRkGgAAAA?w=150&h=108&c=7&bgcl=e80b49&r=0&o=6&cb=12&dpr=1.3&pid=13.1', 1, 'df22ca4b212cd00566e37834224d1648', '喜欢探索新事物，闲暇时爱阅读和旅行', '13800138003', 'ccccc@example.com', 0, '2025-10-02 18:40:49', '2025-10-14 20:50:10', 0, 0, '["阅读","旅行"]', NULL),
    ('的理解案例', 'ddddd', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.ou5G4WyLFM9H5910xZx_CQAAAA?w=148&h=108&c=7&bgcl=f3cfbf&r=0&o=6&cb=12&dpr=1.3&pid=13.1', 1, 'df22ca4b212cd00566e37834224d1648', '宅家爱好者，喜欢摄影和躺着看剧', '13800138004', 'ddddd@example.com', 0, '2025-10-02 18:41:57', '2025-10-14 20:50:10', 0, 0, '["摄影","躺尸","宅"]', NULL),
    ('绝命毒师好看', 'eeeee', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.ou5G4WyLFM9H5910xZx_CQAAAA?w=148&h=108&c=7&bgcl=f3cfbf&r=0&o=6&cb=12&dpr=1.3&pid=13.1', 1, 'df22ca4b212cd00566e37834224d1648', '影视迷，尤其喜欢《绝命毒师》，也爱美食', '13800138005', 'eeeee@example.com', 0, '2025-10-02 18:43:10', '2025-10-14 20:50:10', 0, 0, '["影视","美食"]', NULL),
    ('看的我停不下来', 'mmmmm', 'https://ts3.tc.mm.bing.net/th/id/OIP-C.R86aJ5pc3Fu1oGj9boecuAAAAA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3', 1, 'df22ca4b212cd00566e37834224d1648', '追剧达人，一旦开始就停不下来，喜欢旅行', '13800138006', 'mmmmm@example.com', 0, '2025-10-02 18:52:35', '2025-10-14 20:50:10', 0, 0, '["追剧","旅行"]', NULL),
    ('用户ggg', 'ggggg', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.Bk9P-YAQk0Dw0fGgNiRkGgAAAA?w=150&h=108&c=7&bgcl=e80b49&r=0&o=6&cb=12&dpr=1.3&pid=13.1', 1, 'df22ca4b212cd00566e37834224d1648', '性格开朗，喜欢和朋友聚会，热爱音乐', '13800138007', 'ggggg@example.com', 0, '2025-10-02 18:54:42', '2025-10-14 20:50:10', 0, 0, '["聚会","音乐"]', NULL),
    ('用户hhh', 'hhhhh', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.ou5G4WyLFM9H5910xZx_CQAAAA?w=148&h=108&c=7&bgcl=f3cfbf&r=0&o=6&cb=12&dpr=1.3&pid=13.1', 1, 'df22ca4b212cd00566e37834224d1648', '安静型选手，喜欢看书和养植物', '13800138008', 'hhhhh@example.com', 0, '2025-10-02 21:49:12', '2025-10-14 20:50:10', 0, 0, '["阅读","养植物"]', NULL),
    ('用户kkk', 'kkkkk', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', 1, 'df22ca4b212cd00566e37834224d1648', '运动爱好者，擅长游泳和健身', '13800138009', 'kkkkk@example.com', 0, '2025-10-02 21:51:13', '2025-10-07 12:43:52', 0, 0, '["游泳","健身"]', NULL),
    ('用户ooo', 'ooooo', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', 1, 'df22ca4b212cd00566e37834224d1648', '创意达人，喜欢画画和做手工', '13800138010', 'ooooo@example.com', 0, '2025-10-02 21:52:47', '2025-10-07 12:43:52', 0, 0, '["画画","手工"]', NULL),
    ('用户ttt', 'ttttt', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', 2, 'df22ca4b212cd00566e37834224d1648', '文艺青年，喜欢诗歌和听古典音乐', '13800138011', 'ttttt@example.com', 0, '2025-10-02 21:58:13', '2025-10-07 12:43:52', 0, 0, '["诗歌","古典音乐"]', NULL),
    ('用户xxx', 'xxxxx', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', 2, 'df22ca4b212cd00566e37834224d1648', '美食博主，喜欢研究各种菜谱并分享', '13800138012', 'xxxxx@example.com', 0, '2025-10-02 22:00:19', '2025-10-07 12:43:51', 0, 0, '["美食","分享"]', NULL),
    ('用户vvv', 'vvvvv', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', 2, 'df22ca4b212cd00566e37834224d1648', '旅行博主，走过很多城市，喜欢记录风景', '13800138013', 'vvvvv@example.com', 0, '2025-10-02 22:02:13', '2025-10-07 12:43:52', 0, 0, '["旅行","摄影"]', NULL),
    ('用户iii', 'iiiii', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', 2, 'df22ca4b212cd00566e37834224d1648', '科技爱好者，喜欢研究数码产品', '13800138014', 'iiiii@example.com', 0, '2025-10-02 22:06:49', '2025-10-07 12:43:52', 0, 0, '["数码","科技"]', NULL),
    ('用户ppp', 'ppppp', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', 2, 'df22ca4b212cd00566e37834224d1648', '宠物奴，家里有一只猫，喜欢撸猫和散步', '13800138015', 'ppppp@example.com', 0, '2025-10-02 22:09:28', '2025-10-07 12:43:52', 0, 0, '["宠物","散步"]', NULL),
    ('adada', 'uuuuu', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', 2, 'df22ca4b212cd00566e37834224d1648', '全能型玩家，跑步、摄影、rap样样行', '13800138016', 'uuuuu@example.com', 0, '2025-10-02 22:28:39', '2025-10-07 12:45:18', 0, 1, '["跑步","摄影","rap"]', NULL);


-- 队伍表
create table if not exists gatherfriends.team
(
    id          bigint auto_increment comment 'id'
        primary key,
    name        varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                      null comment '描述',
    maxNum      int      default 1                 not null comment '最大人数',
    expireTime  datetime                           null comment '过期时间',
    userId      bigint                             null comment '用户id（队长 id）',
    status      int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512)                       null comment '密码',
    tags        varchar(1024)                      null comment '标签 json 列表',
    avatar      varchar(1024)                      null comment '队伍头像',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍';

INSERT INTO gatherfriends.team (name, description, maxNum, expireTime, userId, status, password, tags, avatar, createTime, updateTime, isDelete)
VALUES
    ('周末篮球局', '每周六下午2点体育馆打球，新手友好', 10, '2025-12-31 23:59:59', 1, 0, NULL, '["篮球","羽毛球","乒乓球","跑步","健身","骑行"]', 'https://ts3.tc.mm.bing.net/th/id/OIP-C.R86aJ5pc3Fu1oGj9boecuAAAAA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3', '2025-10-06 00:22:35', '2025-10-14 20:48:34', 0),
    ('读书分享会', '每月一次线下读书交流，分享阅读心得', 15, '2026-01-15 00:00:00', 2, 0, NULL, '["阅读", "文化", "交流"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.Bk9P-YAQk0Dw0fGgNiRkGgAAAA?w=150&h=108&c=7&bgcl=e80b49&r=0&o=6&cb=12&dpr=1.3&pid=13.1', '2025-10-06 00:22:35', '2025-10-14 20:48:34', 0),
    ('城市徒步小队', '每周日早晨城市周边徒步，全程约10公里', 20, '2025-11-30 23:59:59', 3, 0, NULL, '["徒步", "户外", "健身"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.Bk9P-YAQk0Dw0fGgNiRkGgAAAA?w=150&h=108&c=7&bgcl=e80b49&r=0&o=6&cb=12&dpr=1.3&pid=13.1', '2025-10-06 00:22:35', '2025-10-14 20:48:34', 0),
    ('公司篮球队', '仅限本公司员工参与的内部篮球队', 12, '2026-06-30 23:59:59', 5, 1, NULL, '["篮球", "公司", "内部"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.ou5G4WyLFM9H5910xZx_CQAAAA?w=148&h=108&c=7&bgcl=f3cfbf&r=0&o=6&cb=12&dpr=1.3&pid=13.1', '2025-10-06 00:22:35', '2025-10-14 20:48:34', 0),
    ('大学同学聚餐', '毕业5周年同学聚会筹备组', 30, '2025-12-20 23:59:59', 3, 1, NULL, '["同学", "聚会", "餐饮"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.ou5G4WyLFM9H5910xZx_CQAAAA?w=148&h=108&c=7&bgcl=f3cfbf&r=0&o=6&cb=12&dpr=1.3&pid=13.1', '2025-10-06 00:22:35', '2025-10-14 20:48:34', 0),
    ('高级摄影采风', '专业摄影爱好者组队，需审核入队', 8, '2026-03-15 23:59:59', 15, 2, NULL, '["摄影", "户外", "专业"]', 'https://ts3.tc.mm.bing.net/th/id/OIP-C.R86aJ5pc3Fu1oGj9boecuAAAAA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3', '2025-10-06 00:22:35', '2025-10-14 20:48:34', 0),
    ('编程学习小组', '专注算法刷题和技术交流，需密码加入', 12, '2026-02-28 23:59:59', 16, 2, 'code123', '["编程", "学习", "算法"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.Bk9P-YAQk0Dw0fGgNiRkGgAAAA?w=150&h=108&c=7&bgcl=e80b49&r=0&o=6&cb=12&dpr=1.3&pid=13.1', '2025-10-06 00:22:35', '2025-10-14 20:48:34', 0),
    ('绝命毒师', '一起看剧', 4, '2022-01-01 06:04:00', 16, 0, NULL, '["个人","游戏"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.ou5G4WyLFM9H5910xZx_CQAAAA?w=148&h=108&c=7&bgcl=f3cfbf&r=0&o=6&cb=12&dpr=1.3&pid=13.1', '2025-10-07 23:18:38', '2025-10-14 20:48:34', 0),
    ('bad-breaking', '一起来看美剧吧', 9, '2035-10-13 06:24:00', 1, 0, NULL, '["美剧"]', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', '2025-10-13 14:05:19', '2025-10-13 14:05:19', 0),
    ('一起来拼车', '拼车群', 7, '2035-10-13 21:00:00', 1, 0, NULL, '["拼车"]', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', '2025-10-13 14:11:31', '2025-10-13 14:11:31', 0),
    ('pubg', '来来来', 5, '2028-10-13 16:00:00', 1, 0, NULL, '["和平精英"]', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', '2025-10-13 16:27:27', '2025-10-13 16:27:27', 0),
    ('天天拼好饭', '好吃吃吃吃', 3, '2025-10-13 12:00:00', 1, 0, NULL, '["吃吃吃吃"]', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', '2025-10-13 17:03:26', '2025-10-13 17:03:26', 0),
    ('大家好', 'adadada', 0, '2035-10-13 22:00:00', 1, 0, NULL, '["跑步"]', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.dqea9nKVBUFGbxH4PJ0H1gHaE7?w=252&h=183&c=7&r=0&o=7&cb=12&dpr=1.3&pid=1.7&rm=3', '2025-10-13 17:14:15', '2025-10-13 17:14:15', 0),
    ('adada', 'ada', 4, '2025-10-20 23:13:00', 1, 0, NULL, '["拼车","长途出行","节假日出行"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-20 22:13:45', '2025-10-20 22:13:45', 0),
    ('怀化学院', 'gogogo', 3, '2025-10-20 23:59:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-20 22:51:23', '2025-10-20 22:51:23', 0),
    ('走你', '怀化南站->怀化学院东校区', 3, '2025-10-21 23:39:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 15:33:25', '2025-10-21 15:33:25', 0),
    ('走走走', 'gogogo', 2, '2025-10-21 17:36:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 16:45:22', '2025-10-21 16:45:22', 0),
    ('羽毛球', 'adada', 4, '2025-10-21 20:17:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 20:16:11', '2025-10-21 20:16:11', 0),
    ('adadaadadd', 'dadd', 4, '2025-10-21 20:31:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 20:23:21', '2025-10-21 20:23:21', 0),
    ('绝命毒师', '', 3, '2025-10-21 12:00:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 20:56:24', '2025-10-21 20:56:24', 0),
    ('00', 'fsfsf', 2, '2025-10-21 12:00:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 20:57:18', '2025-10-21 20:57:18', 0),
    ('fsfs', 'fsfsf', 4, '2025-10-21 20:59:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 20:57:47', '2025-10-21 20:57:47', 0),
    ('女', 'p', 7, '2025-10-21 21:04:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:03:06', '2025-10-21 21:03:06', 0),
    ('女ll', 'khhj', 6, '2025-10-21 23:03:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:03:45', '2025-10-21 23:03:00', 1),
    ('dada', 'adadda', 4, '2025-10-21 21:17:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:15:59', '2025-10-21 21:15:59', 0),
    ('oooo', 'ddd', 5, '2025-10-21 21:19:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:18:09', '2025-10-21 21:18:09', 0),
    ('oooo', 'ddd', 5, '2025-10-21 21:19:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:18:10', '2025-10-21 21:18:10', 0),
    ('lj', 'ljliji', 3, '2025-10-21 21:24:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:22:52', '2025-10-21 21:30:08', 1),
    ('ada', 'adad', 3, '2025-10-21 21:34:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:32:27', '2025-10-21 21:32:27', 0),
    ('adada', 'dxdad', 2, '2025-10-21 21:38:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:36:34', '2025-10-21 21:36:34', 0),
    ('adad', 'dadad', 3, '2025-10-21 21:44:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:43:02', '2025-10-21 21:43:02', 0),
    ('dada', 'dddd', 2, '2025-10-21 21:48:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-21 21:47:10', '2025-10-21 21:49:00', 1),
    ('wulalalala', 'hhhhhhhh', 2, '2025-10-22 20:25:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-22 16:25:45', '2025-10-22 16:25:45', 0),
    ('一起来拼车', 'adadada', 3, '2025-10-22 21:25:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-10-22 16:32:00', '2025-10-22 16:32:00', 0),
    ('aaa', 'dada', 5, '2025-11-09 00:50:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-09 00:14:56', '2025-11-09 00:14:56', 0),
    ('dada', 'dadada', 4, '2025-11-09 18:17:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-09 00:19:23', '2025-11-09 00:19:23', 0),
    ('sfs', 'sfsf', 4, '2025-11-09 17:19:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-09 00:19:42', '2025-11-09 00:19:42', 0),
    ('ada', 'fa', 4, '2025-11-09 18:19:00', 1, 0, NULL, '[]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-09 00:19:59', '2025-11-09 00:19:59', 0),
    ('ad', 'fafaf', 4, '2025-11-09 22:20:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-09 00:20:31', '2025-11-09 00:20:31', 0),
    ('ada', 'adada', 4, '2025-11-23 21:19:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-23 17:17:12', '2025-11-23 17:17:12', 0),
    ('adada', 'adad', 4, '2025-11-23 23:24:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-23 17:20:55', '2025-11-23 17:20:55', 0),
    ('ada', 'add', 5, '2025-11-23 21:29:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-23 17:22:05', '2025-11-23 17:22:05', 0),
    ('aada', 'adawd', 3, '2025-11-23 22:30:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-23 17:23:38', '2025-11-23 17:23:38', 0),
    ('aaaa', 'lalalalala', 3, '2025-11-25 18:42:00', 1, 0, NULL, '["拼车"]', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.XS6EtBzAmWWa31cfFvcx6QHaHa?w=131&h=180&c=8&rs=1&qlt=90&o=6&cb=12&dpr=1.3&pid=3.1&rm=2', '2025-11-25 10:37:38', '2025-11-25 10:37:38', 0);

create table if not exists gatherfriends.user_team
(
    id       bigint auto_increment comment '关联ID'
        primary key,
    userId   bigint                             not null comment '用户ID',
    teamId   bigint                             not null comment '队伍ID',
    role     tinyint                            null comment '0:普通用户，1:管理员',
    status   tinyint  default 0                 not null comment '状态：0-正常，1-已退出',
    joinTime datetime default CURRENT_TIMESTAMP not null comment '加入时间'
)
    comment '用户-队伍关联表';

INSERT INTO gatherfriends.user_team (userId, teamId, role, status, joinTime)
VALUES
    (2, 1, 0, 0, '2025-10-06 09:00:00'),
    (16, 1, 0, 0, '2025-10-06 14:30:00'),
    (2, 2, 1, 0, '2025-10-06 00:22:35'),
    (3, 2, 0, 0, '2025-10-06 10:15:00'),
    (4, 2, 0, 0, '2025-10-06 16:45:00'),
    (3, 3, 1, 0, '2025-10-06 00:22:35'),
    (5, 3, 0, 0, '2025-10-06 08:30:00'),
    (6, 3, 0, 0, '2025-10-06 11:20:00'),
    (5, 4, 1, 0, '2025-10-06 00:22:35'),
    (7, 4, 0, 0, '2025-10-06 13:00:00'),
    (8, 4, 0, 0, '2025-10-06 15:10:00'),
    (3, 5, 1, 0, '2025-10-06 00:22:35'),
    (9, 5, 0, 0, '2025-10-06 17:00:00'),
    (10, 5, 0, 0, '2025-10-06 19:20:00'),
    (15, 6, 1, 0, '2025-10-06 00:22:35'),
    (1, 6, 0, 0, '2025-10-06 09:45:00'),
    (4, 6, 0, 0, '2025-10-06 14:10:00'),
    (16, 7, 1, 0, '2025-10-06 00:22:35'),
    (5, 7, 0, 0, '2025-10-06 10:30:00'),
    (6, 7, 0, 0, '2025-10-06 16:00:00'),
    (16, 8, 1, 0, '2025-10-07 23:18:38'),
    (5, 8, 0, 0, '2025-10-08 08:20:00'),
    (1, 9, 1, 0, '2025-10-13 14:05:19'),
    (2, 9, 0, 0, '2025-10-13 15:30:00'),
    (3, 10, 0, 0, '2025-10-13 16:00:00'),
    (4, 10, 0, 0, '2025-10-13 18:15:00'),
    (7, 11, 0, 0, '2025-10-13 17:00:00'),
    (8, 11, 0, 0, '2025-10-13 19:30:00'),
    (9, 16, 0, 0, '2025-10-13 18:00:00'),
    (10, 16, 0, 0, '2025-10-13 20:10:00'),
    (1, 17, 1, 0, '2025-10-13 17:14:15'),
    (16, 17, 0, 0, '2025-10-13 18:30:00'),
    (1, 18, 2, 0, '2025-10-16 12:43:28'),
    (1, 1, NULL, 0, '2025-10-19 15:25:05'),
    (3, 1, NULL, 0, '2025-10-19 16:04:15'),
    (1, 2, NULL, 0, '2025-10-19 19:00:00'),
    (1, 3, NULL, 0, '2025-10-19 19:01:05'),
    (2, 3, NULL, 0, '2025-10-19 19:01:10'),
    (1, 19, 2, 0, '2025-10-20 22:13:45'),
    (1, 20, 2, 0, '2025-10-20 22:51:23'),
    (1, 21, 2, 0, '2025-10-21 15:33:25'),
    (1, 22, 2, 0, '2025-10-21 16:45:22'),
    (1, 23, 2, 0, '2025-10-21 20:16:11'),
    (1, 24, 2, 0, '2025-10-21 20:23:39'),
    (1, 25, 2, 0, '2025-10-21 20:56:29'),
    (1, 26, 2, 0, '2025-10-21 20:57:18'),
    (1, 27, 2, 0, '2025-10-21 20:57:47'),
    (1, 28, 2, 0, '2025-10-21 21:03:06'),
    (1, 29, 2, 0, '2025-10-21 21:03:45'),
    (1, 30, 2, 0, '2025-10-21 21:15:59'),
    (1, 31, 2, 0, '2025-10-21 21:18:10'),
    (1, 32, 2, 0, '2025-10-21 21:18:10'),
    (1, 33, 2, 0, '2025-10-21 21:22:52'),
    (1, 34, 2, 0, '2025-10-21 21:32:27'),
    (1, 35, 2, 0, '2025-10-21 21:36:34'),
    (1, 36, 2, 0, '2025-10-21 21:43:02'),
    (1, 37, 2, 0, '2025-10-21 21:47:10'),
    (1, 38, 2, 0, '2025-10-22 16:25:45'),
    (1, 39, 2, 0, '2025-10-22 16:32:00'),
    (1, 40, 2, 0, '2025-11-09 00:14:56'),
    (1, 41, 2, 0, '2025-11-09 00:19:23'),
    (1, 42, 2, 0, '2025-11-09 00:19:42'),
    (1, 43, 2, 0, '2025-11-09 00:19:59'),
    (1, 44, 2, 0, '2025-11-09 00:20:31'),
    (1, 45, 2, 0, '2025-11-23 17:17:12'),
    (1, 46, 2, 0, '2025-11-23 17:20:55'),
    (1, 47, 2, 0, '2025-11-23 17:22:05'),
    (1, 49, 2, 0, '2025-11-25 10:37:38'),
    (2, 49, NULL, 0, '2025-11-25 10:39:44');

-- 标签表（可以不创建，因为标签字段已经放到了用户表中）
create table tag
(
    id         bigint auto_increment comment 'id'
        primary key,
    tagName    varchar(256) null comment '标签名称',
    userId     bigint null comment '用户 id',
    parentId   bigint null comment '父标签 id',
    isParent   tinyint null comment '0 - 不是, 1 - 父标签',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0 not null comment '是否删除',
    constraint uniIdx_tagName
        unique (tagName)
) comment '标签';


create index idx_userId
    on tag (userId);


create table if not exists gatherfirends.ai_message
(
    id              bigint unsigned                      not null comment '主键ID'
        primary key,
    conversation_id varchar(64)                          not null comment '会话ID',
    message_type    varchar(20)                          not null comment '消息类型',
    content         text                                 not null comment '消息内容',
    metadata        text                                 not null comment '元数据',
    create_time     datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint(1) default 0                 not null comment '是否删除 0-未删除 1-已删除'
)
    comment '聊天消息表';
