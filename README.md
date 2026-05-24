# 光缆资源管理系统 — 服务端

光缆资源管理系统的后端服务，为 Android 客户端提供 RESTful API 和空间数据存储。

## 技术栈

- **框架**：Spring Boot 3.5.8（WebFlux 响应式）
- **ORM**：MyBatis-Plus 3.5.9
- **数据库**：PostgreSQL + PostGIS 空间扩展
- **安全**：Spring Security WebFlux
- **语言**：Java 21
- **构建**：Gradle

## 主要接口

所有接口前缀 `/api/v1/`，主要分为三组：

| 模块 | 路径 | 说明 |
|------|------|------|
| 资源点 | `/api/v1/map/resource-point` | 资源点的 CRUD |
| 光缆段 | `/api/v1/map/fiber-segments` | 光缆段创建与空间查询 |
| 用户 | `/api/v1/users` | 注册、登录、信息管理 |

空间数据查询（bbox、中心点半径、类型过滤）通过 `/api/v1/map/query` 处理，利用 PostGIS 的空间函数和 GIST 索引做查询优化。

坐标处理：前端高德地图使用 GCJ-02 坐标系，数据库存 WGS84，后端 `CoordinateConverter` 在写入时做转换。

## 启动

```bash
# 需要先配置 .env 文件中的数据库连接信息
gradlew bootRun
```

依赖 PostgreSQL 14+ 且需要启用 PostGIS 扩展。如果没有远程数据库，可以用 Docker 本地起一个：

```bash
docker run -e POSTGRES_PASSWORD=xxx -p 5432:5432 postgis/postgis:14-3.4
```


## 关于本项目

这是本科毕业设计的服务端部分，客户端仓库见 [Optical_Manage](https://github.com/68815/optical_manage_frontend_android)。

代码放在 GitHub 仅作为存档和展示，不是社区向的开源项目。如果碰巧对你有所帮助那当然很好，但项目本身**不维护、不接收 PR、不承诺持续更新**，请当作一个课程项目的参考代码来看待就好。
