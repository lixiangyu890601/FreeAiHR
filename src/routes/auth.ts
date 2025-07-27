import Router from 'koa-router';
import { Op } from 'sequelize';
import { loginRateLimit } from '../middleware/rateLimit';
import { auth } from '../middleware/auth';
import { AppContext } from '../types';
import User from '../models/User';
import { generateToken } from '../utils/jwt';

const router = new Router<any, AppContext>({
  prefix: '/auth'
});

// 用户注册
router.post('/register', async (ctx) => {
  try {
    const { username, email, phone, password } = ctx.request.body as any;
    
    // 检查用户是否已存在
    const whereConditions: any[] = [{ username }, { email }];
    if (phone) {
      whereConditions.push({ phone });
    }
    
    const existingUser = await User.findOne({ 
      where: { 
        [Op.or]: whereConditions
      } 
    });
    
    if (existingUser) {
      ctx.status = 400;
      ctx.body = { error: 'Username, email or phone already exists' };
      return;
    }
    
    // 创建新用户
    const user = await User.create({
      username,
      email,
      phone,
      password,
      role: 'user',
      isActive: true
    });
    
    // 返回用户信息（不包含密码）
    const { password: _, ...userWithoutPassword } = user.get({ plain: true });
    ctx.status = 201;
    ctx.body = { user: userWithoutPassword };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 用户登录
router.post('/login', loginRateLimit, async (ctx) => {
  try {
    const requestBody = ctx.request.body as any;
    const { identifier, password } = requestBody;
    
    // 支持邮箱和手机号登录，保持向后兼容
    const loginIdentifier = identifier || requestBody.email;
    
    if (!loginIdentifier) {
      ctx.status = 400;
      ctx.body = { error: 'Email or phone number is required' };
      return;
    }
    
    // 查找用户（支持邮箱或手机号）
    const user = await User.findByEmailOrPhone(loginIdentifier);
    
    if (!user || !user.isActive) {
      ctx.status = 401;
      ctx.body = { error: 'Invalid credentials' };
      return;
    }
    
    // 验证密码
    const isPasswordValid = await user.comparePassword(password);
    
    if (!isPasswordValid) {
      ctx.status = 401;
      ctx.body = { error: 'Invalid credentials' };
      return;
    }
    
    // 更新最后登录时间
    user.lastLogin = new Date();
    await user.save();
    
    // 生成JWT
    const token = generateToken({ userId: user.id });
    
    // 返回用户信息和token
    const { password: _, ...userWithoutPassword } = user.get({ plain: true });
    ctx.body = { 
      user: userWithoutPassword,
      token 
    };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 获取当前用户信息
router.get('/profile', auth, async (ctx) => {
  ctx.body = { user: ctx.user };
});

// 用户退出登录
router.post('/logout', auth, async (ctx) => {
  try {
    // 由于使用JWT无状态认证，服务端不需要维护token状态
    // 退出登录主要由客户端删除存储的token来实现
    ctx.body = { 
      message: 'Logout successful',
      timestamp: new Date().toISOString()
    };
  } catch (error) {
    console.error('用户退出登录失败:', error);
    ctx.status = 500;
    ctx.body = { error: (error as Error).message };
  }
});

// 初始化管理员用户（不受权限管控）
router.post('/init-admin', async (ctx) => {
  console.log('初始化管理员接口被调用');
  try {
    // 检查是否已存在管理员用户
    console.log('检查是否已存在管理员用户...');
    const existingAdmin = await User.findOne({ 
      where: { role: 'admin' } 
    });
    console.log('管理员用户查询结果:', existingAdmin ? '已存在' : '不存在');
    
    if (existingAdmin) {
      ctx.status = 400;
      ctx.body = { error: 'Admin user already exists' };
      return;
    }
    
    // 创建默认管理员用户
    console.log('开始创建管理员用户...');
    const adminUser = await User.create({
      username: 'admin',
      email: 'admin@example.com',
      password: 'admin123456',
      role: 'admin',
      isActive: true
    });
    console.log('管理员用户创建成功:', adminUser.id);
    
    // 返回管理员信息（不包含密码）
    const { password: _, ...adminWithoutPassword } = adminUser.get({ plain: true });
    ctx.status = 201;
    ctx.body = { 
      message: 'Admin user initialized successfully',
      user: adminWithoutPassword 
    };
  } catch (error) {
    console.error('初始化管理员用户失败:', error);
    ctx.status = 500;
    ctx.body = { error: (error as Error).message };
  }
});

export default router;