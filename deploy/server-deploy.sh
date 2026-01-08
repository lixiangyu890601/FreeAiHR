#!/bin/bash

# FreeHire 服务器端部署脚本（使用预构建镜像）
# 将此脚本和相关文件复制到服务器执行

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

info() { echo -e "${BLUE}[INFO]${NC} $1"; }
success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 检查 Docker
check_docker() {
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装！请先安装 Docker。

离线安装方法:
1. 从有网络的机器下载 Docker 离线包:
   https://download.docker.com/linux/static/stable/x86_64/

2. 上传到服务器并安装:
   tar -xzf docker-*.tgz
   cp docker/* /usr/bin/
   
3. 创建 systemd 服务文件并启动

或联系运维人员协助安装 Docker。"
    fi
    
    if ! docker info &> /dev/null; then
        error "Docker 未启动，请执行: systemctl start docker"
    fi
    
    success "Docker 已就绪"
}

# 检查必要文件
check_files() {
    local missing=0
    
    if [ ! -f "$SCRIPT_DIR/docker-compose.images.yml" ]; then
        error "缺少 docker-compose.images.yml"
        missing=1
    fi
    
    if [ ! -f "$SCRIPT_DIR/init.sql" ]; then
        warn "缺少 init.sql，请确保数据库已初始化或复制初始化脚本"
    fi
    
    if [ ! -f "$SCRIPT_DIR/.env" ]; then
        if [ -f "$SCRIPT_DIR/env.example" ]; then
            cp "$SCRIPT_DIR/env.example" "$SCRIPT_DIR/.env"
            warn "已从 env.example 创建 .env，请修改配置后重新运行"
            exit 1
        else
            error "缺少 .env 配置文件"
        fi
    fi
    
    success "配置文件检查通过"
}

# 导入离线镜像
load_images() {
    local images_dir="$SCRIPT_DIR/images"
    
    if [ -d "$images_dir" ] && [ "$(ls -A $images_dir/*.tar 2>/dev/null)" ]; then
        info "导入离线镜像..."
        
        for tar_file in "$images_dir"/*.tar; do
            local filename=$(basename "$tar_file")
            
            # deploy-app 模式下跳过数据库镜像
            if [ "$DEPLOY_MODE" = "app-only" ]; then
                if [[ "$filename" == "postgres.tar" ]] || [[ "$filename" == "redis.tar" ]]; then
                    info "  跳过: $filename (使用外部服务)"
                    continue
                fi
            fi
            
            info "  导入: $filename"
            if ! docker load -i "$tar_file"; then
                warn "  导入失败: $filename，继续..."
            fi
        done
        
        success "镜像导入完成"
    else
        warn "未找到离线镜像，将尝试从网络拉取"
    fi
}

# 获取 compose 文件
get_compose_file() {
    if [ "$DEPLOY_MODE" = "app-only" ]; then
        echo "docker-compose.app-only.yml"
    else
        echo "docker-compose.images.yml"
    fi
}

# 启动服务
start_services() {
    cd "$SCRIPT_DIR"
    local compose_file=$(get_compose_file)
    
    info "启动服务 (配置: $compose_file)..."
    docker compose -f "$compose_file" up -d
    
    success "服务启动完成"
}

# 停止服务
stop_services() {
    cd "$SCRIPT_DIR"
    local compose_file=$(get_compose_file)
    docker compose -f "$compose_file" down
    success "服务已停止"
}

# 查看状态
view_status() {
    cd "$SCRIPT_DIR"
    # 尝试两个配置文件
    if [ -f "docker-compose.app-only.yml" ]; then
        docker compose -f docker-compose.app-only.yml ps 2>/dev/null || true
    fi
    if [ -f "docker-compose.images.yml" ]; then
        docker compose -f docker-compose.images.yml ps 2>/dev/null || true
    fi
}

# 查看日志
view_logs() {
    cd "$SCRIPT_DIR"
    local compose_file=$(get_compose_file)
    docker compose -f "$compose_file" logs -f "$@"
}

# 完整部署
full_deploy() {
    check_docker
    check_files
    load_images
    start_services
    
    echo ""
    success "===== 部署完成 ====="
    echo ""
    local ip=$(hostname -I 2>/dev/null | awk '{print $1}' || echo "localhost")
    info "访问地址: http://$ip"
    info "默认账号: admin"
    info "默认密码: admin123"
    echo ""
    info "查看状态: $0 status"
    info "查看日志: $0 logs"
}

# 显示帮助
show_help() {
    echo "用法: $0 [命令]"
    echo ""
    echo "命令:"
    echo "  deploy            完整部署（包含 PostgreSQL 和 Redis）"
    echo "  deploy-app        仅部署应用（使用外部数据库和 Redis）"
    echo "  start             启动服务"
    echo "  stop              停止服务"
    echo "  restart           重启服务"
    echo "  status            查看状态"
    echo "  logs [服务名]     查看日志"
    echo "  load              导入离线镜像"
    echo "  help              显示帮助"
    echo ""
    echo "示例:"
    echo "  $0 deploy         # 完整部署（自带数据库）"
    echo "  $0 deploy-app     # 仅部署应用（连接外部数据库）"
}

# 主函数
main() {
    # 默认部署模式
    DEPLOY_MODE="${DEPLOY_MODE:-full}"
    
    case "${1:-deploy}" in
        deploy)
            DEPLOY_MODE="full"
            full_deploy
            ;;
        deploy-app)
            DEPLOY_MODE="app-only"
            full_deploy
            ;;
        start)
            start_services
            ;;
        stop)
            stop_services
            ;;
        restart)
            stop_services
            start_services
            ;;
        status)
            view_status
            ;;
        logs)
            shift
            view_logs "$@"
            ;;
        load)
            load_images
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

