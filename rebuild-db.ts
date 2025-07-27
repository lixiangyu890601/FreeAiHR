import { sequelize } from './src/config/database';
import User from './src/models/User';
import Resume from './src/models/Resume';
import Position from './src/models/Position';

async function rebuildDatabase() {
  try {
    console.log('连接数据库...');
    await sequelize.authenticate();
    console.log('数据库连接成功');
    
    console.log('强制重建数据库表结构...');
    await sequelize.sync({ force: true });
    console.log('数据库表结构重建完成');
    
    console.log('检查users表结构...');
    const [usersResults] = await sequelize.query('DESCRIBE users');
    console.log('users表字段:', usersResults);
    
    console.log('检查resumes表结构...');
    const [resumesResults] = await sequelize.query('DESCRIBE resumes');
    console.log('resumes表字段:', resumesResults);
    
    console.log('检查positions表结构...');
    const [positionsResults] = await sequelize.query('DESCRIBE positions');
    console.log('positions表字段:', positionsResults);
    
    process.exit(0);
  } catch (error) {
    console.error('操作失败:', error);
    process.exit(1);
  }
}

rebuildDatabase();