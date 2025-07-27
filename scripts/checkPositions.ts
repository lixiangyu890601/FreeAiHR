import { sequelize } from '../src/config/database';
import Position from '../src/models/Position';

async function checkPositions() {
  try {
    console.log('连接数据库...');
    await sequelize.authenticate();
    console.log('数据库连接成功');

    // 查询总数
    const totalCount = await Position.count();
    console.log(`\n岗位总数: ${totalCount}条`);

    // 查询最新的5条数据
    const latestPositions = await Position.findAll({
      limit: 5,
      order: [['createdAt', 'DESC']],
      attributes: ['id', 'positionName', 'department', 'status', 'workType', 'experienceLevel']
    });

    console.log('\n最新的5条岗位数据:');
    latestPositions.forEach(position => {
      console.log(`${position.id}. ${position.positionName} - ${position.department} - ${position.status} - ${position.workType} - ${position.experienceLevel}`);
    });

    // 按状态统计
    const statusStats = await Position.findAll({
      attributes: [
        'status',
        [sequelize.fn('COUNT', sequelize.col('id')), 'count']
      ],
      group: ['status']
    });

    console.log('\n岗位状态统计:');
    statusStats.forEach((stat: any) => {
      console.log(`${stat.status}: ${stat.dataValues.count}条`);
    });

    process.exit(0);
  } catch (error) {
    console.error('错误:', error);
    process.exit(1);
  }
}

checkPositions();