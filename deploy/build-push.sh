#!/bin/bash

# FreeHire 镜像构建和推送脚本
# 使用方式: ./build-push.sh [镜像仓库地址]

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

info() { echo -e "${BLUE}[INFO]${NC} $1"; }
success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# 镜像仓库地址（可通过参数或环境变量指定）
# 示例：
# - 阿里云: registry.cn-hangzhou.aliyuncs.com/your-namespace
# - Docker Hub: your-dockerhub-username
# - 私有仓库: your-registry.com:5000
REGISTRY="${DOCKER_REGISTRY:-freehire}"

# 版本号（默认使用 git commit 短哈希或时间戳）
VERSION="${IMAGE_VERSION:-$(cd "$PROJECT_DIR" && git rev-parse --short HEAD 2>/dev/null || date +%Y%m%d%H%M)}"

# 镜像名称（固定使用 freehire 前缀，确保与 docker-compose 配置一致）
BACKEND_IMAGE="${REGISTRY}/freehire-server:${VERSION}"
FRONTEND_IMAGE="${REGISTRY}/freehire-web:${VERSION}"
BACKEND_IMAGE_LATEST="${REGISTRY}/freehire-server:latest"
FRONTEND_IMAGE_LATEST="${REGISTRY}/freehire-web:latest"

info "===== FreeHire 镜像构建脚本 ====="
info "镜像仓库: $REGISTRY"
info "版本号: $VERSION"
echo ""

# 检查 Docker
check_docker() {
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装"
    fi
    success "Docker 已安装"
}

# 构建后端镜像
build_backend() {
    info "构建后端镜像: $BACKEND_IMAGE"
    
    docker build \
        -t "$BACKEND_IMAGE" \
        -t "$BACKEND_IMAGE_LATEST" \
        -f "$PROJECT_DIR/freehire-server/Dockerfile" \
        "$PROJECT_DIR/freehire-server"
    
    success "后端镜像构建完成"
}

# 构建前端镜像
build_frontend() {
    info "构建前端镜像: $FRONTEND_IMAGE"
    
    docker build \
        -t "$FRONTEND_IMAGE" \
        -t "$FRONTEND_IMAGE_LATEST" \
        -f "$PROJECT_DIR/freehire-web/Dockerfile" \
        "$PROJECT_DIR/freehire-web"
    
    success "前端镜像构建完成"
}

# 推送镜像
push_images() {
    info "推送镜像到仓库..."
    
    docker push "$BACKEND_IMAGE"
    docker push "$BACKEND_IMAGE_LATEST"
    docker push "$FRONTEND_IMAGE"
    docker push "$FRONTEND_IMAGE_LATEST"
    
    success "镜像推送完成"
}

# 导出镜像为 tar 文件（离线部署用）
export_images() {
    local output_dir="$SCRIPT_DIR/dist"
    mkdir -p "$output_dir/images"
    
    info "===== 导出离线部署包 ====="
    
    # 1. 拉取基础镜像
    info "拉取基础镜像..."
    docker pull postgres:15-alpine
    docker pull redis:7-alpine
    
    # 2. 导出所有镜像
    info "导出镜像文件..."
    docker save -o "$output_dir/images/freehire-server.tar" "$BACKEND_IMAGE_LATEST"
    docker save -o "$output_dir/images/freehire-web.tar" "$FRONTEND_IMAGE_LATEST"
    docker save -o "$output_dir/images/postgres.tar" postgres:15-alpine
    docker save -o "$output_dir/images/redis.tar" redis:7-alpine
    
    # 3. 复制部署文件
    info "复制部署配置..."
    cp "$SCRIPT_DIR/docker-compose.images.yml" "$output_dir/"
    cp "$SCRIPT_DIR/docker-compose.app-only.yml" "$output_dir/"
    cp "$SCRIPT_DIR/server-deploy.sh" "$output_dir/"
    cp "$SCRIPT_DIR/env.example" "$output_dir/"
    cp "$PROJECT_DIR/freehire-server/src/main/resources/db/init.sql" "$output_dir/"
    
    # 4. 创建部署说明
    cat > "$output_dir/README.txt" << 'EOF'
=====================================
    FreeHire 离线部署包
=====================================

【部署步骤】

1. 解压部署包到服务器
   tar -xzf freehire-offline-xxx.tar.gz
   cd freehire-offline

2. 修改配置文件
   cp env.example .env
   vi .env
   
   必须修改:
   - DB_PASSWORD: 数据库密码
   - REDIS_PASSWORD: Redis密码

3. 执行部署
   chmod +x server-deploy.sh
   ./server-deploy.sh deploy

4. 访问系统
   http://服务器IP
   默认账号: admin
   默认密码: admin123

【运维命令】

查看状态: ./server-deploy.sh status
查看日志: ./server-deploy.sh logs
重启服务: ./server-deploy.sh restart
停止服务: ./server-deploy.sh stop

【系统要求】

- 操作系统: CentOS 7+ / Ubuntu 18.04+
- CPU: 2核+
- 内存: 4GB+
- 磁盘: 20GB+
- Docker: 20.10+（脚本会自动安装）

=====================================
EOF
    
    # 5. 打包
    info "创建压缩包..."
    cd "$SCRIPT_DIR"
    tar -czvf "freehire-offline-${VERSION}.tar.gz" -C "$output_dir" .
    
    # 6. 清理临时文件
    rm -rf "$output_dir"
    
    # 7. 显示结果
    local package_size=$(du -h "$SCRIPT_DIR/freehire-offline-${VERSION}.tar.gz" | cut -f1)
    
    echo ""
    success "===== 离线部署包创建完成 ====="
    echo ""
    info "文件位置: $SCRIPT_DIR/freehire-offline-${VERSION}.tar.gz"
    info "文件大小: $package_size"
    echo ""
    info "部署方式:"
    echo "  1. 将 freehire-offline-${VERSION}.tar.gz 上传到服务器"
    echo "  2. tar -xzf freehire-offline-${VERSION}.tar.gz"
    echo "  3. cd freehire-offline && ./server-deploy.sh deploy"
}

# 显示帮助
show_help() {
    echo "用法: $0 [命令] [镜像仓库地址]"
    echo ""
    echo "命令:"
    echo "  build      仅构建镜像（默认）"
    echo "  push       构建并推送到镜像仓库"
    echo "  export     构建并导出为 tar 文件（离线部署）"
    echo "  help       显示帮助"
    echo ""
    echo "环境变量:"
    echo "  DOCKER_REGISTRY   镜像仓库地址"
    echo "  IMAGE_VERSION     镜像版本号"
    echo ""
    echo "示例:"
    echo "  $0 build                                    # 仅构建"
    echo "  $0 push registry.cn-hangzhou.aliyuncs.com/myns  # 推送到阿里云"
    echo "  $0 export                                   # 导出离线包"
}

# 显示部署信息
show_deploy_info() {
    echo ""
    info "===== 部署信息 ====="
    echo ""
    echo "后端镜像: $BACKEND_IMAGE_LATEST"
    echo "前端镜像: $FRONTEND_IMAGE_LATEST"
    echo ""
    echo "在服务器上使用以下命令部署："
    echo ""
    echo "  # 拉取镜像"
    echo "  docker pull $BACKEND_IMAGE_LATEST"
    echo "  docker pull $FRONTEND_IMAGE_LATEST"
    echo ""
    echo "  # 启动服务"
    echo "  docker compose -f docker-compose.images.yml up -d"
}

# 主函数
main() {
    check_docker
    
    case "${1:-build}" in
        build)
            build_backend
            build_frontend
            show_deploy_info
            ;;
        push)
            build_backend
            build_frontend
            push_images
            show_deploy_info
            ;;
        export)
            build_backend
            build_frontend
            export_images
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            # 如果第一个参数不是命令，当作仓库地址处理
            REGISTRY="$1"
            BACKEND_IMAGE="${REGISTRY}/freehire-server:${VERSION}"
            FRONTEND_IMAGE="${REGISTRY}/freehire-web:${VERSION}"
            BACKEND_IMAGE_LATEST="${REGISTRY}/freehire-server:latest"
            FRONTEND_IMAGE_LATEST="${REGISTRY}/freehire-web:latest"
            
            build_backend
            build_frontend
            show_deploy_info
            ;;
    esac
}

main "$@"

