#!/bin/bash

# FreeHire 一键部署脚本
set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的信息
info() { echo -e "${BLUE}[INFO]${NC} $1"; }
success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

info "===== FreeHire 部署脚本 ====="
info "项目目录: $PROJECT_DIR"
info "部署目录: $SCRIPT_DIR"

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装，请先安装 Docker"
    fi
    if ! command -v docker compose &> /dev/null; then
        error "Docker Compose 未安装，请先安装 Docker Compose"
    fi
    success "Docker 已安装"
}

# 检查环境变量文件
check_env() {
    if [ ! -f "$SCRIPT_DIR/.env" ]; then
        if [ -f "$SCRIPT_DIR/.env.example" ]; then
            warn ".env 文件不存在，从 .env.example 复制..."
            cp "$SCRIPT_DIR/.env.example" "$SCRIPT_DIR/.env"
            warn "请修改 .env 文件中的配置后重新运行部署脚本"
            exit 1
        else
            error ".env 和 .env.example 文件都不存在"
        fi
    fi
    success ".env 文件已存在"
}

# 复制数据库初始化脚本
copy_init_sql() {
    local init_sql="$PROJECT_DIR/freehire-server/src/main/resources/db/init.sql"
    if [ -f "$init_sql" ]; then
        cp "$init_sql" "$SCRIPT_DIR/init.sql"
        success "数据库初始化脚本已复制"
    else
        error "找不到数据库初始化脚本: $init_sql"
    fi
}

# 构建镜像
build_images() {
    info "开始构建 Docker 镜像..."
    cd "$SCRIPT_DIR"
    
    docker compose -f docker-compose.prod.yml build --no-cache
    
    success "Docker 镜像构建完成"
}

# 启动服务
start_services() {
    info "启动服务..."
    cd "$SCRIPT_DIR"
    
    docker compose -f docker-compose.prod.yml up -d
    
    success "服务启动完成"
}

# 停止服务
stop_services() {
    info "停止服务..."
    cd "$SCRIPT_DIR"
    
    docker compose -f docker-compose.prod.yml down
    
    success "服务已停止"
}

# 查看日志
view_logs() {
    cd "$SCRIPT_DIR"
    docker compose -f docker-compose.prod.yml logs -f "$@"
}

# 查看状态
view_status() {
    cd "$SCRIPT_DIR"
    docker compose -f docker-compose.prod.yml ps
}

# 完整部署
full_deploy() {
    check_docker
    check_env
    copy_init_sql
    build_images
    start_services
    
    echo ""
    success "===== 部署完成 ====="
    echo ""
    info "访问地址: http://$(hostname -I | awk '{print $1}')"
    info "默认账号: admin"
    info "默认密码: admin123"
    echo ""
    info "查看日志: $0 logs"
    info "查看状态: $0 status"
    info "停止服务: $0 stop"
}

# 显示帮助
show_help() {
    echo "用法: $0 [命令]"
    echo ""
    echo "命令:"
    echo "  deploy    完整部署（默认）"
    echo "  build     仅构建镜像"
    echo "  start     启动服务"
    echo "  stop      停止服务"
    echo "  restart   重启服务"
    echo "  logs      查看日志（可指定服务名）"
    echo "  status    查看服务状态"
    echo "  help      显示帮助"
    echo ""
    echo "示例:"
    echo "  $0 deploy          # 完整部署"
    echo "  $0 logs backend    # 查看后端日志"
    echo "  $0 logs -f         # 实时查看所有日志"
}

# 主函数
main() {
    case "${1:-deploy}" in
        deploy)
            full_deploy
            ;;
        build)
            check_docker
            build_images
            ;;
        start)
            check_docker
            start_services
            ;;
        stop)
            check_docker
            stop_services
            ;;
        restart)
            check_docker
            stop_services
            start_services
            ;;
        logs)
            shift
            view_logs "$@"
            ;;
        status)
            view_status
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            error "未知命令: $1，使用 '$0 help' 查看帮助"
            ;;
    esac
}

main "$@"


