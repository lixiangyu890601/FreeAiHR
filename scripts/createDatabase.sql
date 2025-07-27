-- 创建数据库
CREATE DATABASE IF NOT EXISTS resume_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE resume_db;

-- 创建用户（可选，如果需要专门的数据库用户）
-- CREATE USER 'resume_user'@'localhost' IDENTIFIED BY 'your_password';
-- GRANT ALL PRIVILEGES ON resume_db.* TO 'resume_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 注意：表结构将由Sequelize自动创建
-- 运行应用程序时，Sequelize会根据模型定义自动创建表