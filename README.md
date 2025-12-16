# GatherFriends Backend

## 项目简介

GatherFriends是一个基于Spring Boot 3.5.6开发的社交匹配与实时聊天系统后端服务。该项目旨在解决用户在寻找志同道合的伙伴、组建团队以及进行实时沟通交流时遇到的困难。

### 问题背景

在现代社交场景中，用户经常面临以下挑战：
- 难以找到具有相同兴趣爱好的伙伴
- 缺乏便捷的团队组建和管理工具
- 实时沟通交流功能不够完善
- 拼车、活动等场景下的临时组队需求无法得到满足

### 核心功能

1. **用户管理**
   - 用户注册、登录、个人信息管理
   - 用户标签系统，支持多标签匹配
   - 用户在线状态管理

2. **团队管理**
   - 创建公开/私有/加密团队
   - 团队搜索与匹配（支持标签、关键词搜索）
   - 加入/退出团队功能
   - 团队信息更新与管理

3. **拼车功能**
   - 基于地理位置（经纬度）的拼车队伍创建
   - 附近拼车队伍查询
   - 拼车队伍管理

4. **实时聊天**
   - WebSocket实现的实时消息推送
   - 支持私聊（1对1）和群聊（团队内）
   - 消息持久化存储
   - 在线用户状态管理

5. **AI集成**
   - 集成阿里云DashScope SDK
   - 支持Spring AI框架
   - AI消息处理功能

### 关键技术

- **后端框架**: Spring Boot 3.5.6
- **持久层框架**: MyBatis-Plus 3.5.7
- **数据库**: MySQL 8.0
- **缓存/会话**: Redis + Redisson 3.23.3
- **实时通信**: WebSocket (Jakarta EE)
- **API文档**: Springdoc OpenAPI + Knife4j 4.5.0
- **工具库**: Hutool 5.8.25, FastJSON2, Gson
- **数据处理**: EasyExcel 3.1.0
- **AI集成**: Spring AI 1.0.0-M6, 阿里云DashScope SDK

### 技术亮点

1. **分页查询**: 使用MyBatis-Plus的PaginationInnerInterceptor实现高效分页
2. **逻辑删除**: 全局逻辑删除配置，数据安全可控
3. **Redis分布式锁**: 使用Redisson实现分布式锁，保证并发安全
4. **WebSocket实时通信**: 基于Jakarta WebSocket实现实时消息推送
5. **Session管理**: Spring Session + Redis实现分布式Session管理
6. **统一异常处理**: 全局异常处理器，统一错误响应格式
7. **参数校验**: 统一的请求参数校验和响应封装

### 预期效果

- **用户体验**: 用户可以快速找到志同道合的伙伴，便捷地组建和管理团队
- **实时性**: 通过WebSocket实现毫秒级的消息推送，提升沟通效率
- **可扩展性**: 基于Spring Boot微服务架构，支持水平扩展
- **性能**: Redis缓存和分页查询优化，支持高并发访问
- **可维护性**: 清晰的代码结构、统一的异常处理和日志记录

### 项目结构

```
gatherfriends-back/
├── src/main/java/com/yang/gatherfriendsback/
│   ├── common/          # 通用响应类和工具类
│   ├── config/          # 配置类（MyBatis-Plus、Redis、WebSocket等）
│   ├── constant/        # 常量定义
│   ├── controller/      # REST API控制器
│   ├── enums/           # 枚举类
│   ├── exception/       # 异常处理
│   ├── mapper/          # MyBatis Mapper接口
│   ├── model/           # 数据模型（domain、request、vo）
│   ├── service/         # 业务逻辑层
│   └── websoket/        # WebSocket端点
└── src/main/resources/
    ├── mapper/          # MyBatis XML映射文件
    └── application.yml  # 应用配置文件
```

### 开发环境

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 快速开始

1. 配置数据库连接（application.yml）
2. 配置Redis连接（application.yml）
3. 运行 `GatherfriendsBackApplication`
4. 访问 `http://localhost:8080/api/doc.html` 查看API文档


