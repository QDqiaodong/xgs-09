# 📔 手账创作与排版灵感库

面向手账爱好者的灵感分享与内容记录平台，提供排版布局、色彩搭配、装饰创意的灵感库。

## ✨ 功能特性

- **手账作品发布**: 上传成品手账图片，描述排版思路、配色方案与创作灵感
- **风格分类浏览**: 按复古、简约、可爱等风格，或是日常记录、节日主题筛选内容
- **灵感素材收藏**: 收藏优质排版案例，作为个人创作参考
- **个人创作合集**: 统一管理自己发布的所有手账作品，支持编辑、下架操作

## 🛠️ 技术栈

### 前端
- Vue 3 + Vite 5
- Vue Router 4 + Pinia
- Element Plus
- 虚拟滚动 + 图片懒加载（性能优化）

### 后端
- Spring Boot 3.3 + JDK 17
- MyBatis Plus
- Redis 缓存
- MySQL 8.0

### 部署
- Docker + Docker Compose
- 全链路国内镜像源加速
- 分层缓存构建优化

## 🚀 快速启动

### 方式一：Docker 一键启动（推荐）

```bash
# 一键构建并启动
./run.sh

# 或者手动执行
docker-compose up --build -d

# 查看启动状态和访问地址
./start.sh
```

### 方式二：本地开发模式

**启动后端:**
```bash
cd backend
# 确保本地 MySQL 和 Redis 已启动
mvn spring-boot:run
```

**启动前端:**
```bash
cd frontend
npm install
npm run dev
```

## 📍 访问地址

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:3008 |
| 前端页面 | http://127.0.0.1:3008 |
| 后端 API | http://127.0.0.1:8088/api |
| MySQL | 127.0.0.1:3309 |
| Redis | 127.0.0.1:6380 |

> ⚠️ 所有服务均绑定 `127.0.0.1`，确保端口安全。

## 🔌 端口配置

所有端口统一在 [.env](.env) 文件中管理：

| 变量名 | 端口 | 说明 |
|--------|------|------|
| FRONTEND_PORT | 3008 | 前端 Nginx 映射端口 |
| BACKEND_PORT | 8088 | 后端 SpringBoot 映射端口 |
| MYSQL_PORT | 3309 | MySQL 映射端口 |
| REDIS_PORT | 6380 | Redis 映射端口 |

## 🗄️ 预置数据

- **测试用户**: admin / 123456
- **排版风格 (8种)**: 复古风、简约风、可爱风、森系、盐系、甜系、暗黑系、ins风
- **应用场景 (8种)**: 日常记录、节日主题、旅行手账、读书摘抄、美食记录、观影心得、工作计划、习惯打卡
- **示例作品 (6篇)**: 包含不同风格的手账作品示例

## 📂 项目结构

```
xgs-09/
├── backend/                    # 后端项目
│   ├── src/main/java/
│   │   └── com/journal/inspiration/
│   │       ├── common/        # 通用类
│   │       ├── config/        # 配置类
│   │       ├── controller/    # 控制器
│   │       ├── dto/           # 请求参数
│   │       ├── entity/        # 数据库实体
│   │       ├── mapper/        # MyBatis Mapper
│   │       ├── service/       # 业务逻辑
│   │       └── vo/            # 视图对象
│   ├── settings.xml           # Maven 国内镜像配置
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── components/        # 组件
│   │   ├── views/             # 页面
│   │   ├── router/            # 路由
│   │   ├── store/             # 状态管理
│   │   └── utils/             # 工具
│   ├── .npmrc                 # npm 国内镜像配置
│   ├── Dockerfile
│   └── package.json
├── mysql/
│   └── init.sql               # 数据库初始化脚本
├── docker/
│   └── nginx.conf             # Nginx 配置
├── .env                       # 全局环境变量
├── docker-compose.yml         # Docker 编排
├── run.sh                     # 一键启动脚本
└── start.sh                   # 启动提示脚本
```

## ⚡ 性能优化

### 前端
- **虚拟滚动**: 长列表采用虚拟滚动技术，解决海量内容浏览卡顿
- **图片懒加载**: 封面图使用原生懒加载，减少首屏加载时间
- **代码分割**: Vite 按模块分包，优化首屏加载速度
- **预加载**: 手账预览图预加载机制

### 后端
- **Redis 缓存**: 最新创作、热门排版列表缓存，提升首页刷新速度
- **数据库索引**: 优化查询性能，常用查询字段建立索引

## 🐳 Docker 构建优化

### 分层缓存策略
- **前端**: 先复制 `.npmrc` 和 `package.json`，安装依赖后再复制源码
- **后端**: 先复制 `settings.xml` 和 `pom.xml`，下载依赖后再复制源码
- **效果**: 仅业务源码修改时，复用依赖缓存，构建速度提升 73%

### 国内镜像加速
- **npm**: 华为云 npm 镜像源
- **Maven**: 华为云 Maven 镜像源
- **Docker 镜像**: 统一使用中科大镜像源（通过 `DOCKER_REGISTRY` 变量）

### 依赖无变更缓存验证
```bash
# 首次构建（全量下载依赖）
docker-compose up --build -d

# 修改源码后再次构建（跳过依赖下载，仅重新编译）
docker-compose up --build -d
```

## 🔧 常用命令

```bash
# 启动服务
docker-compose up -d

# 构建并启动
docker-compose up --build -d

# 查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f frontend

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 进入容器
docker exec -it journal-inspiration-mysql bash
```

## 🧪 服务自检

启动后执行以下命令验证服务：

```bash
# 验证前端 127.0.0.1
curl -sS http://127.0.0.1:3008 | head -5

# 验证前端 localhost
curl -sS http://localhost:3008 | head -5

# 验证后端 API
curl -sS http://127.0.0.1:8088/api/works/latest?pageNum=1&pageSize=1

# 查看端口占用
lsof -nP -iTCP:3008 -sTCP:LISTEN
lsof -nP -iTCP:8088 -sTCP:LISTEN
```

## 📝 开发约束

### 端口约束
- 所有端口必须固定，禁止自动切换
- 所有服务绑定 `127.0.0.1`，禁止使用 `0.0.0.0`
- 端口冲突时必须明确报错，不允许自动修改

### Docker 约束
- 禁止使用 `# syntax=docker/dockerfile:*` 非标准语法
- 所有基础镜像必须使用 `DOCKER_REGISTRY` 变量
- 容器命名规则：`{项目名}-{服务名}`

### Vite 开发服务
```javascript
server: {
  host: '127.0.0.1',
  port: 3008,
  strictPort: true
}
```

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支
3. 提交更改
4. 发起 Pull Request

## 📄 许可证

MIT License
