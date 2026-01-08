#!/bin/bash

# FreeHire 智能招聘系统 - 一键安装脚本

set -e

echo ""
echo "=================================================="
echo "  FreeHire 智能招聘系统 - 安装脚本"
echo "=================================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查Docker是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}错误: Docker未安装，请先安装Docker${NC}"
        echo "安装指南: https://docs.docker.com/get-docker/"
        exit 1
    fi
    echo -e "${GREEN}✓ Docker已安装${NC}"
}

# 检查Docker Compose是否安装
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        echo -e "${RED}错误: Docker Compose未安装${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Docker Compose已安装${NC}"
}

# 构建后端镜像
build_backend() {
    echo ""
    echo -e "${YELLOW}正在构建后端镜像...${NC}"
    cd ../freehire-server
    docker build -t freehire-server:latest .
    cd ../deploy
    echo -e "${GREEN}✓ 后端镜像构建完成${NC}"
}

# 构建前端
build_frontend() {
    echo ""
    echo -e "${YELLOW}正在构建前端...${NC}"
    cd ../freehire-web
    
    # 检查node_modules
    if [ ! -d "node_modules" ]; then
        echo "安装前端依赖..."
        npm install
    fi
    
    npm run build
    cd ../deploy
    echo -e "${GREEN}✓ 前端构建完成${NC}"
}

# 启动服务
start_services() {
    echo ""
    echo -e "${YELLOW}正在启动服务...${NC}"
    docker-compose up -d
    echo -e "${GREEN}✓ 服务启动完成${NC}"
}

# 等待服务就绪
wait_for_services() {
    echo ""
    echo -e "${YELLOW}等待服务就绪...${NC}"
    sleep 10
    
    # 检查后端服务
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/actuator/health > /dev/null 2>&1; then
            echo -e "${GREEN}✓ 后端服务已就绪${NC}"
            break
        fi
        echo "等待后端服务启动... ($i/30)"
        sleep 2
    done
}

# 显示访问信息
show_info() {
    echo ""
    echo "=================================================="
    echo -e "${GREEN}  FreeHire 安装完成！${NC}"
    echo "=================================================="
    echo ""
    echo "  访问地址: http://localhost"
    echo "  API文档:  http://localhost/api/doc.html"
    echo ""
    echo "  默认账号: admin"
    echo "  默认密码: admin123"
    echo ""
    echo "  MinIO控制台: http://localhost:9001"
    echo "  MinIO账号:   minioadmin / minioadmin"
    echo ""
    echo "=================================================="
    echo ""
}

# 主流程
main() {
    check_docker
    check_docker_compose
    
    echo ""
    read -p "是否开始安装？(y/n) " -n 1 -r
    echo ""
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        build_backend
        build_frontend
        start_services
        wait_for_services
        show_info
    else
        echo "安装已取消"
        exit 0
    fi
}

main

