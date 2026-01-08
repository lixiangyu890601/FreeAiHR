# FreeHire 部署指南

## 系统要求

| 组件 | 最低要求 | 推荐配置 |
|------|----------|----------|
| CPU | 2核 | 4核 |
| 内存 | 4GB | 8GB |
| 磁盘 | 20GB | 50GB+ |
| 系统 | Ubuntu 20.04+ / CentOS 7+ | Ubuntu 22.04 |

## 中间件依赖

| 中间件 | 版本 | 用途 | 端口 |
|--------|------|------|------|
| PostgreSQL | 15+ | 数据库 | 5432 |
| Redis | 7+ | 缓存 | 6379 |

> **文件存储**：默认使用本地文件系统，可通过配置切换到 OSS 或 MinIO。

---

## 🚀 快速部署（推荐）

### 1. 准备工作

```bash
# 1. 将项目代码上传到服务器
scp -r FreeHR root@your-server:/opt/

# 2. SSH 登录服务器
ssh root@your-server

# 3. 进入部署目录
cd /opt/FreeHR/deploy
```

### 2. 配置环境变量

```bash
# 复制环境变量示例文件
cp env.example .env

# 编辑环境变量（必须修改密码！）
nano .env
```

建议修改的配置：
```bash
DB_PASSWORD=your_secure_db_password
REDIS_PASSWORD=your_secure_redis_password
```

### 3. 一键部署

```bash
# 添加执行权限
chmod +x deploy.sh

# 执行部署
./deploy.sh deploy
```

部署完成后，访问：`http://your-server-ip`

**默认账号**: admin / admin123

---

## 📋 部署命令说明

```bash
# 完整部署（构建 + 启动）
./deploy.sh deploy

# 仅构建镜像
./deploy.sh build

# 启动服务
./deploy.sh start

# 停止服务
./deploy.sh stop

# 重启服务
./deploy.sh restart

# 查看服务状态
./deploy.sh status

# 查看日志
./deploy.sh logs              # 所有日志
./deploy.sh logs backend      # 后端日志
./deploy.sh logs frontend     # 前端日志
./deploy.sh logs -f           # 实时日志
```

---

## 🔧 手动部署（分步骤）

### 步骤1：安装 Docker

```bash
# Ubuntu
curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun

# 启动 Docker
systemctl start docker
systemctl enable docker
```

### 步骤2：准备配置

```bash
cd /opt/FreeHR/deploy

# 复制数据库初始化脚本
cp ../freehire-server/src/main/resources/db/init.sql ./init.sql

# 配置环境变量
cp env.example .env
nano .env
```

### 步骤3：构建并启动

```bash
# 构建镜像
docker compose -f docker-compose.prod.yml build

# 启动服务
docker compose -f docker-compose.prod.yml up -d

# 查看状态
docker compose -f docker-compose.prod.yml ps
```

---

## 方式二：原生安装

### 1. 安装 PostgreSQL

```bash
# 添加官方仓库
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo apt-get update

# 安装 PostgreSQL 15
sudo apt-get install -y postgresql-15

# 配置数据库
sudo -u postgres psql
```

```sql
-- 在 psql 中执行
ALTER USER postgres PASSWORD 'postgres';
CREATE DATABASE freehire;
\q
```

```bash
# 初始化表结构
sudo -u postgres psql -d freehire -f /path/to/init.sql
```

### 2. 安装 Redis

```bash
sudo apt-get install -y redis-server

# 启动 Redis
sudo systemctl start redis-server
sudo systemctl enable redis-server

# 测试连接
redis-cli ping
```

### 3. 创建文件存储目录

```bash
# 创建目录结构
sudo mkdir -p /data/freehire/uploads/{resume,avatar,attachment,common}

# 设置权限
sudo chmod -R 755 /data/freehire
```

---

## 文件存储配置

### 默认：本地存储

```yaml
file:
  storage-type: local
  local:
    base-path: /data/freehire/uploads
    url-prefix: /api/files
```

### 可选：阿里云 OSS

```yaml
file:
  storage-type: oss
  oss:
    endpoint: oss-cn-shanghai.aliyuncs.com
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    bucket-name: your-bucket-name
```

### 可选：MinIO

```yaml
file:
  storage-type: minio
  minio:
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: freehire
```

---

## 连接信息

### PostgreSQL
| 配置项 | 值 |
|--------|-----|
| Host | localhost |
| Port | 5432 |
| Database | freehire |
| Username | postgres |
| Password | postgres |

### Redis
| 配置项 | 值 |
|--------|-----|
| Host | localhost |
| Port | 6379 |
| Password | (无) |

### 文件存储（本地）
| 配置项 | 值 |
|--------|-----|
| 路径 | /data/freehire/uploads |
| 子目录 | resume/, avatar/, attachment/, common/ |
| 访问前缀 | /api/files |

---

## 防火墙配置

```bash
# 如果启用了 UFW 防火墙
sudo ufw allow 5432/tcp  # PostgreSQL
sudo ufw allow 6379/tcp  # Redis
sudo ufw allow 8080/tcp  # 后端服务
sudo ufw allow 5173/tcp  # 前端开发服务
```

---

## 常用命令

### Docker Compose
```bash
# 启动服务
docker compose up -d

# 停止服务
docker compose down

# 查看日志
docker compose logs -f postgres
docker compose logs -f redis

# 重启单个服务
docker compose restart postgres

# 进入容器
docker exec -it freehire-postgres psql -U postgres -d freehire
docker exec -it freehire-redis redis-cli
```

### 原生服务
```bash
# PostgreSQL
sudo systemctl status postgresql
sudo systemctl restart postgresql

# Redis
sudo systemctl status redis-server
sudo systemctl restart redis-server
```

---

## 验证安装

```bash
# 测试 PostgreSQL
psql -h localhost -U postgres -d freehire -c "SELECT 1;"

# 测试 Redis
redis-cli ping

# 测试文件存储目录
ls -la /data/freehire/uploads/
```

---

## 常见问题

### 1. PostgreSQL 连接被拒绝
```bash
# 检查 pg_hba.conf 配置
sudo cat /etc/postgresql/15/main/pg_hba.conf

# 添加远程访问权限
echo "host all all 0.0.0.0/0 md5" | sudo tee -a /etc/postgresql/15/main/pg_hba.conf
sudo systemctl restart postgresql
```

### 2. Redis 无法远程连接
```bash
# 编辑配置
sudo nano /etc/redis/redis.conf

# 修改 bind 配置
bind 0.0.0.0

sudo systemctl restart redis-server
```

### 3. 文件上传目录权限问题
```bash
# 检查目录权限
ls -la /data/freehire/uploads/

# 修复权限
sudo chown -R $USER:$USER /data/freehire/uploads/
sudo chmod -R 755 /data/freehire/uploads/
```
