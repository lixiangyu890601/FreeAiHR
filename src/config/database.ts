import { Sequelize, Options } from 'sequelize';
import dotenv from 'dotenv';

// 加载环境变量
dotenv.config();

// 数据库配置
const dbConfig: Options = {
  dialect: 'mysql',
  host: process.env.DB_HOST || 'localhost',
  port: parseInt(process.env.DB_PORT || '3306'),
  database: process.env.DB_NAME || 'resume_db',
  username: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  logging: process.env.NODE_ENV === 'development' ? console.log : false,
  pool: {
    max: 5,
    min: 0,
    acquire: 30000,
    idle: 10000
  },
  timezone: '+08:00', // 设置时区
  define: {
    charset: 'utf8mb4',
    collate: 'utf8mb4_unicode_ci',
    timestamps: true,
    underscored: false
  }
};

// 创建Sequelize实例
export const sequelize = new Sequelize(dbConfig);

// 连接数据库函数
export const connectDB = async (): Promise<void> => {
  try {
    await sequelize.authenticate();
    console.log('数据库连接成功');
    
    // 同步所有模型 - 移除 alter 选项
    await sequelize.sync({ force: false });
    console.log('数据库模型同步完成');
  } catch (error) {
    console.error('数据库连接失败:', error);
    process.exit(1);
  }
};