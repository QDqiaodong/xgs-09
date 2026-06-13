# Docker 镜像拉取问题排查指南

## ⚠️ 问题现象

构建时出现以下错误：
```
failed to solve: Unavailable: error reading from server: EOF
load metadata for docker.io/library/xxx: error reading from server: EOF
```

**原因**: 无法访问 Docker Hub，本机 Docker 未配置镜像加速器。

---

## ✅ 已自动为你配置

### 1. Docker 全局镜像加速器
已修改 `~/.docker/daemon.json`，添加了以下镜像源：
```json
{
  "registry-mirrors": [
    "https://dockerproxy.com",
    "https://hub-mirror.c.163.com",
    "https://mirror.ccs.tencentyun.com"
  ]
}
```

### 2. 项目级镜像源配置
在 [.env](.env) 中可配置 `DOCKER_REGISTRY` 变量，统一为所有镜像添加前缀。

---

## 🔧 解决步骤（必须执行）

### 第一步：重启 Docker Desktop

**必须重启 Docker Desktop 才能让镜像加速器生效！**

1. 点击菜单栏 Docker 图标 → 选择 `Restart`
2. 等待 Docker 完全重启（大约 30-60 秒）

### 第二步：验证镜像加速器是否生效

```bash
# 查看 Docker 信息，确认 Registry Mirrors 已配置
docker info | grep -A 5 "Registry Mirrors"
```

正常输出应包含：
```
Registry Mirrors:
  https://dockerproxy.com/
  https://hub-mirror.c.163.com/
  https://mirror.ccs.tencentyun.com/
```

### 第三步：测试拉取镜像

```bash
# 测试拉取一个简单镜像
docker pull --platform linux/amd64 alpine:latest
```

如果成功，说明镜像加速器已生效。

### 第四步：重新构建项目

```bash
# 清理之前的失败构建缓存
docker compose down -v

# 重新构建启动
docker compose up --build -d
```

---

## 🚨 如果还是不行

### 方案一：使用项目级镜像前缀

修改 [.env](.env)，去掉注释，使用某个可用的镜像源：

```env
# 尝试1: dockerproxy（免费，无需登录）
DOCKER_REGISTRY=dockerproxy.com

# 尝试2: 网易镜像
DOCKER_REGISTRY=hub-mirror.c.163.com

# 尝试3: 腾讯云
DOCKER_REGISTRY=mirror.ccs.tencentyun.com
```

修改后重新构建：
```bash
docker compose build --no-cache
docker compose up -d
```

### 方案二：配置阿里云镜像加速器（最稳定，推荐）

1. 登录阿里云: https://cr.console.aliyun.com/
2. 进入「镜像加速器」页面
3. 复制你的专属加速器地址（格式: `https://xxxxxx.mirror.aliyuncs.com`）
4. 修改 `~/.docker/daemon.json`，添加该地址
5. 重启 Docker Desktop

### 方案三：手动拉取基础镜像

如果某个镜像实在拉不下来，可以尝试手动 pull：

```bash
# 例如拉取 node 镜像
docker pull node:18-alpine

# 拉取 nginx 镜像
docker pull nginx:alpine

# 拉取 mysql 镜像
docker pull mysql:8.0

# 拉取 redis 镜像
docker pull redis:7-alpine
```

---

## 📋 常用命令

```bash
# 查看 Docker 系统信息
docker info

# 查看已配置的镜像源
docker info | grep "Registry Mirrors" -A 10

# 清理构建缓存
docker builder prune -f

# 清理所有悬空镜像
docker image prune -f

# 完全重置（慎用，会删除所有本地镜像）
docker system prune -a
```

---

## 🔍 检查网络

如果所有镜像源都无法使用，检查网络：

```bash
# 测试能否访问 Docker Hub
ping -c 3 hub.docker.com

# 测试某个镜像源
ping -c 3 dockerproxy.com
```

如果是公司网络，可能需要配置代理。
