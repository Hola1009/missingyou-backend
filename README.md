# 面询友 项目介绍

> 作者：[fancier](https://github.com/hola1009)

该项目基于我制作的[初始化模板](https://github.com/Hola1009/springboot-backend-prototype)开发

## 项目介绍
面询友是一款基于 Next.js + Spring Boot + Redis + MySQL + Elasticsearch 的面试刷题平台, 运用 Druid + HotKey + Sa-Token + Sentinel 提高了系统的性能和安全性

管理员可以创建题库, 题目和题解; 用户可以注册登录, 分词检索题目, 在线书体查看刷题记录日历图. 此外, 系统使用数据库连接池, 热 key 探索, 缓存, 高级数据结构等提升性能. 通过流量控制, 熔断, 动态 IP 黑白名单那过滤, 同端登录冲突检测, 分级爬虫策略来提升系统和内容的安全性.

## 业务核心流程
![](doc/img.png)

## 项目功能梳理
### 基础功能
- 用户模块
    - 用户注册
    - 用户登陆
    - *管理员* 管理用户 - 增删改查
- 题库模块
    - 查看题库列表
    - 查看题目详情
    - *管理员* 管理题库 - 增删改查
- 题目模块
    - 题目搜索
    - 查看题目详情
    - *管理员* 管理题目 - 增删改查

### 高级功能
- 题目批量管理
    - *管理员* 批量向题库中添加题目
    - *管理员* 批量从题库移除题目
    - *管理员* 批量删除题目
- 分词题目搜索
- 用户刷题记录日历图
- 自动缓存热门题目
- 网站流量控制和熔断
- 动态 IP 黑白名单过滤
- 同端登录冲突检测
- 分级题目反爬虫策略

## 技术选型
- Spring Boot 框架 +  Maven 多模块构建
- MySQL 数据库 + MyBatis-Plus  框架 + Mybatis X
- Redis 分布式缓存 + Caffeine 本地缓存
- Redission 分布式锁 + BitMap + BloomFilter
- 🌟Elasticsearch 搜索引擎
- 🌟Druid 数据库连接池 + 并发编程
- 🌟Sa-Token 权限控制
- 🌟HotKey 热点探索
- 🌟Sentinel 流量控制
- 🌟Nacos 配置中心
- 🌟多角度项目优化: 性能, 安全性, 可用性

## 架构设计
![](doc/img_1.png)