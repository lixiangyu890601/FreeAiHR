# FreeAiHR - AI时代下的人力资源系统
<p align="left">
  <img src="https://img.shields.io/badge/Java-17-orange" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/React-18-blue" alt="React">
  <img src="https://img.shields.io/badge/Ant%20Design-5.x-blue" alt="Ant Design">
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/License-AGPL-yellow" alt="License">
</p>

## 📖 简介

AI面试，AI简历，AI人力资源！FreeAiHR 是一款企业级、人工智能时代下的智能招聘解决方案，支持私有化部署，AI驱动的全流程招聘管理系统！

### ✨ 核心特性

- 🤖 **AI智能解析** - 自动解析简历，提取结构化信息
- 🎯 **智能匹配** - AI精准匹配人才与职位，自动评分
- 📊 **全流程管理** - 从投递到入职，一站式招聘流程管理
- 🔐 **私有化部署** - 一键部署，数据完全私有
- 🔑 **BYOK模式** - 支持自带AI密钥，灵活配置

## 🏗️ 技术栈

### 后端
- Java 17
- Spring Boot 3.2.5
- Sa-Token (权限认证)
- MyBatis-Plus (ORM)
- PostgreSQL (数据库)
- Redis (缓存)

### 前端
- React 18
- TypeScript
- Ant Design 5
- Vite
- TailwindCSS
- Zustand (状态管理)

## 📁 项目结构

```
FreeHR/
├── freehire-server/          # 后端项目
│   ├── src/main/java/com/freehire/
│   │   ├── common/           # 公共模块
│   │   ├── modules/          # 业务模块
│   │   │   ├── auth/         # 认证
│   │   │   ├── system/       # 系统管理
│   │   │   ├── job/          # 职位管理
│   │   │   ├── resume/       # 简历管理
│   │   │   ├── candidate/    # 候选人
│   │   │   └── ai/           # AI服务
│   │   └── FreeHireApplication.java
│   └── pom.xml
│
├── freehire-web/             # 前端项目
│   ├── src/
│   │   ├── pages/            # 页面
│   │   ├── components/       # 组件
│   │   ├── services/         # API服务
│   │   ├── stores/           # 状态管理
│   │   └── router/           # 路由
│   └── package.json
│
├── deploy/                   # 部署配置
│   ├── docker-compose.yml
│   ├── nginx.conf
│   └── install.sh
│
└── README.md
```

## 🚀 快速开始

### 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- Node.js 18+ (本地开发)
- JDK 17+ (本地开发)
- Maven 3.8+ (本地开发)

### 一键部署 (推荐)

```bash
# 克隆项目
git clone https://github.com/your-repo/freehire.git
cd freehire

# 运行安装脚本
cd deploy
chmod +x install.sh
./install.sh
```

安装完成后访问：
- 系统地址: http://localhost
- 默认账号: admin / admin123

### 本地开发

#### 后端启动

```bash
cd freehire-server

# 安装依赖
mvn install

# 启动服务（需要先启动PostgreSQL和Redis）
mvn spring-boot:run
```

#### 前端启动

```bash
cd freehire-web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问 http://localhost:3000

## 📊 功能模块

| 模块 | 功能 | 状态 |
|------|------|------|
| 工作台 | 数据概览、待办事项、招聘漏斗 | ✅ |
| 职位管理 | 发布/编辑/关闭职位、AI生成JD | ✅ |
| 简历管理 | 上传/解析简历、AI智能解析 | ✅ |
| 候选人管理 | 候选人状态流转、沟通记录 | ✅ |
| 面试管理 | 面试安排、反馈录入 | ✅ |
| 人才库 | 人才搜索、标签管理 | ✅ |
| 系统管理 | 用户/角色/部门/AI配置 | ✅ |
| 招聘官网 | 对外招聘页面、在线投递 | 🚧 |

## 🤖 AI功能

系统支持多种AI提供商（BYOK模式）：

| 提供商 | 模型 | 说明 |
|--------|------|------|
| OpenAI | GPT-4/GPT-4o | 推荐，效果最好 |
| 通义千问 | qwen-max | 阿里云，国内首选 |
| DeepSeek | deepseek-chat | 性价比高 |
| 智谱清言 | GLM-4 | 国产大模型 |

AI功能包括：
- 📄 简历智能解析
- 🎯 JD-简历匹配评分
- ✍️ 职位描述生成
- 💬 面试问题建议

## 📦 部署配置

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| DB_HOST | 数据库地址 | localhost |
| DB_PORT | 数据库端口 | 5432 |
| DB_NAME | 数据库名 | freehire |
| DB_USERNAME | 数据库用户名 | postgres |
| DB_PASSWORD | 数据库密码 | postgres |
| REDIS_HOST | Redis地址 | localhost |
| REDIS_PORT | Redis端口 | 6379 |
| MINIO_ENDPOINT | MinIO地址 | http://localhost:9000 |

### 硬件要求

| 规模 | CPU | 内存 | 磁盘 |
|------|-----|------|------|
| 小型 (<100用户) | 2核 | 4GB | 50GB |
| 中型 (<500用户) | 4核 | 8GB | 100GB |
| 大型 (>500用户) | 8核 | 16GB | 200GB+ |

## 🤝 联系客服小编

完整版代码文件：freeaihr源码包.zip【Github文件上传个数有限制，最新源码文件见网盘里链接】
链接: https://pan.baidu.com/s/1ijM6z3lGgSSJlEI2zxW47A?pwd=mn9j 提取码: mn9j 


点击Star后，入群讨论：

<img width="1031" height="1478" alt="db06d17402f5c4e14c0bb1827030db6e" src="https://github.com/user-attachments/assets/4389cb6c-26c9-4855-be7e-a18d8b84988d" />

扫码添加客服小编微信
![0e1483a4df22e1f6c8ec5404ee86a7f0](https://github.com/user-attachments/assets/d0b5edf3-aefd-4631-a073-5c95f30bc945)

如有问题或建议，欢迎提交 Issue 或 Pull Request。

---

<p align="center">Made with ❤️ by FreeAiHR Team</p>

