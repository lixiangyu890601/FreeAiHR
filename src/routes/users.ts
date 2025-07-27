import Router from 'koa-router';
import { Op } from 'sequelize';  // 添加这一行
import { auth, adminAuth } from '../middleware/auth';
import { apiRateLimit } from '../middleware/rateLimit';
import User from '../models/User';
import { AppContext } from '../types';

const router = new Router<any, AppContext>();

// 应用认证中间件到所有路由
router.use(auth, apiRateLimit);

// 获取所有用户（仅管理员）
router.get('/', adminAuth, async (ctx) => {
  try {
    const users = await User.findAll({
      where: { isActive: true },
      attributes: { exclude: ['password'] }
    });
    ctx.body = { users };
  } catch (error) {
    ctx.status = 500;
    ctx.body = { error: (error as Error).message };
  }
});

// 获取单个用户
router.get('/:id', async (ctx) => {
  try {
    const userId = parseInt(ctx.params.id, 10);
    
    // 检查权限：普通用户只能查看自己的信息
    if (ctx.user?.role !== 'admin' && ctx.user?.id !== userId) {
      ctx.status = 403;
      ctx.body = { error: 'Access denied' };
      return;
    }
    
    const user = await User.findByIdSafe(userId);
    
    if (!user) {
      ctx.status = 404;
      ctx.body = { error: 'User not found' };
      return;
    }
    
    ctx.body = { user };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 创建用户（仅管理员）
router.post('/', adminAuth, async (ctx) => {
  try {
    const userData = ctx.request.body as any;
    
    // 检查用户是否已存在
    const existingUser = await User.findOne({ 
      where: { 
        [Op.or]: [  // 修改这一行，去掉 User.sequelize!.Sequelize.Op.or
          { username: userData.username }, 
          { email: userData.email }
        ] 
      } 
    });
    
    if (existingUser) {
      ctx.status = 400;
      ctx.body = { error: 'Username or email already exists' };
      return;
    }
    
    // 创建新用户
    const user = await User.create(userData);
    
    // 返回用户信息（不包含密码）
    const { password: _, ...userWithoutPassword } = user.get({ plain: true });
    ctx.status = 201;
    ctx.body = { user: userWithoutPassword };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

export default router;