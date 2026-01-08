#!/bin/bash

# FreeHire 中间件部署脚本
# 适用于 Ubuntu 20.04/22.04/24.04
# 使用阿里云镜像源

set -e

echo "=========================================="
echo "    FreeHire 中间件部署脚本"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
    exit 1
}

# 检查是否为 root 用户
check_root() {
    if [ "$EUID" -ne 0 ]; then
        error "请使用 sudo 或 root 用户运行此脚本"
    fi
}

# 更新系统
update_system() {
    info "更新系统软件包..."
    apt-get update -y
}

# 安装 Docker（使用阿里云镜像）
install_docker() {
    if command -v docker &> /dev/null; then
        info "Docker 已安装，版本: $(docker --version)"
    else
        info "安装 Docker（使用阿里云镜像）..."
        
        # 安装依赖
        apt-get install -y \
            apt-transport-https \
            ca-certificates \
            curl \
            gnupg \
            lsb-release

        # 使用阿里云 Docker 镜像源
        curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

        # 设置阿里云仓库
        echo \
          "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://mirrors.aliyun.com/docker-ce/linux/ubuntu \
          $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

        # 安装 Docker Engine
        apt-get update -y
        apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

        # 配置 Docker 镜像加速器
        mkdir -p /etc/docker
        cat > /etc/docker/daemon.json <<EOF
{
    "registry-mirrors": [
        "https://docker.1ms.run",
        "https://docker.xuanyuan.me"
    ]
}
EOF

        # 启动 Docker
        systemctl daemon-reload
        systemctl start docker
        systemctl enable docker

        info "Docker 安装完成"
    fi
}

# 安装 Docker Compose
install_docker_compose() {
    if command -v docker-compose &> /dev/null || docker compose version &> /dev/null; then
        info "Docker Compose 已安装"
    else
        info "安装 Docker Compose..."
        
        # 使用 apt 安装（已在上面安装 docker-compose-plugin）
        # 或者手动下载
        apt-get install -y docker-compose-plugin 2>/dev/null || true
        
        # 创建软链接
        if [ -f /usr/libexec/docker/cli-plugins/docker-compose ]; then
            ln -sf /usr/libexec/docker/cli-plugins/docker-compose /usr/local/bin/docker-compose 2>/dev/null || true
        fi

        info "Docker Compose 安装完成"
    fi
}

# 复制初始化SQL
copy_init_sql() {
    SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
    SQL_SOURCE="../freehire-server/src/main/resources/db/init.sql"
    
    if [ -f "$SCRIPT_DIR/$SQL_SOURCE" ]; then
        cp "$SCRIPT_DIR/$SQL_SOURCE" "$SCRIPT_DIR/init.sql"
        info "已复制数据库初始化脚本"
    elif [ -f "$SCRIPT_DIR/init.sql" ]; then
        info "init.sql 已存在"
    else
        warn "未找到 init.sql，数据库将为空"
    fi
}

# 创建本地文件存储目录
create_storage_dirs() {
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
}

# 启动服务
start_services() {
    info "启动中间件服务..."
    
    SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
    cd "$SCRIPT_DIR"
    
    # 停止旧容器（如果存在）
    docker compose down 2>/dev/null || true
    
    # 启动服务
    docker compose up -d
    
    info "等待服务启动..."
    sleep 15
    
    # 检查服务状态
    docker compose ps
}

# 打印连接信息
print_info() {
    echo ""
    echo "=========================================="
    echo "    部署完成！"
    echo "=========================================="
    echo ""
    echo "服务连接信息："
    echo ""
    echo "PostgreSQL:"
    echo "  - Host: localhost"
    echo "  - Port: 5432"
    echo "  - Database: freehire"
    echo "  - Username: postgres"
    echo "  - Password: postgres"
    echo ""
    echo "Redis:"
    echo "  - Host: localhost"
    echo "  - Port: 6379"
    echo "  - Password: (无)"
    echo ""
    echo "文件存储（本地）:"
    echo "  - 路径: /data/freehire/uploads"
    echo "  - 子目录: resume/, avatar/, attachment/, common/"
    echo ""
    echo "管理命令："
    echo "  - 查看状态: docker compose ps"
    echo "  - 查看日志: docker compose logs -f"
    echo "  - 停止服务: docker compose down"
    echo "  - 启动服务: docker compose up -d"
    echo ""
    
    # 显示服务状态
    echo "当前服务状态："
    docker compose ps
}

# 主函数
main() {
    check_root
    update_system
    install_docker
    install_docker_compose
    copy_init_sql
    create_storage_dirs
    start_services
    print_info
}

# 运行
main
