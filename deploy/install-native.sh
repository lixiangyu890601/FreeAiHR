#!/bin/bash

# FreeHire 中间件原生安装脚本（不使用Docker）
# 适用于 Ubuntu 20.04/22.04/24.04

set -e

echo "=========================================="
echo "    FreeHire 中间件原生安装脚本"
echo "=========================================="

# 颜色定义
GREEN='\033[0;32m'
NC='\033[0m'

info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

# 检查 root
if [ "$EUID" -ne 0 ]; then
    echo "请使用 sudo 运行此脚本"
    exit 1
fi

# 更新系统
info "更新系统..."
apt-get update -y

# ========================================
# 1. 安装 PostgreSQL
# ========================================
info "安装 PostgreSQL 15..."

# 添加 PostgreSQL 官方仓库
sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -
apt-get update -y
apt-get install -y postgresql-15

# 启动 PostgreSQL
systemctl start postgresql
systemctl enable postgresql

# 配置数据库
info "配置 PostgreSQL 数据库..."
sudo -u postgres psql <<EOF
ALTER USER postgres PASSWORD 'postgres';
CREATE DATABASE freehire;
\q
EOF

# 允许远程连接（可选，生产环境谨慎）
echo "host    all             all             0.0.0.0/0               md5" >> /etc/postgresql/15/main/pg_hba.conf
sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/" /etc/postgresql/15/main/postgresql.conf
systemctl restart postgresql

info "PostgreSQL 安装完成"

# ========================================
# 2. 安装 Redis
# ========================================
info "安装 Redis..."

apt-get install -y redis-server

# 配置 Redis
sed -i "s/bind 127.0.0.1 ::1/bind 0.0.0.0/" /etc/redis/redis.conf
sed -i "s/# requirepass foobared/# requirepass /" /etc/redis/redis.conf

# 启动 Redis
systemctl start redis-server
systemctl enable redis-server

info "Redis 安装完成"

# ========================================
# 3. 创建文件存储目录
# ========================================
info "创建文件存储目录..."

STORAGE_BASE="/data/freehire/uploads"

# 创建目录结构
mkdir -p "$STORAGE_BASE/resume"
mkdir -p "$STORAGE_BASE/avatar"
mkdir -p "$STORAGE_BASE/attachment"
mkdir -p "$STORAGE_BASE/common"

# 设置权限
chmod -R 755 /data/freehire

info "文件存储目录创建完成: $STORAGE_BASE"

# ========================================
# 4. 初始化数据库
# ========================================
info "初始化数据库表..."

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SQL_FILE="$SCRIPT_DIR/../freehire-server/src/main/resources/db/init.sql"

if [ -f "$SQL_FILE" ]; then
    sudo -u postgres psql -d freehire -f "$SQL_FILE"
    info "数据库初始化完成"
else
    info "请手动执行 init.sql 初始化数据库"
fi

# ========================================
# 5. 打印信息
# ========================================
echo ""
echo "=========================================="
echo "    安装完成！"
echo "=========================================="
echo ""
echo "服务状态："
systemctl status postgresql --no-pager | head -5
echo ""
systemctl status redis-server --no-pager | head -5
echo ""
echo "连接信息："
echo ""
echo "PostgreSQL:"
echo "  - Host: localhost:5432"
echo "  - Database: freehire"
echo "  - User: postgres / Password: postgres"
echo ""
echo "Redis:"
echo "  - Host: localhost:6379"
echo ""
echo "文件存储（本地）:"
echo "  - 路径: /data/freehire/uploads"
echo "  - 子目录: resume/, avatar/, attachment/, common/"
echo ""
