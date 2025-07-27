import Koa from 'koa';
import Router from 'koa-router';
import bodyParser from 'koa-bodyparser';
import cors from '@koa/cors';
import { connectDB, sequelize } from './config/database';
import './models/User';
import './models/Position';

// 导入路由
import authRoutes from './routes/auth';
import userRoutes from './routes/users';
import resumeRoutes from './routes/resumes';
import positionRoutes from './routes/positions';
import { apiRateLimit } from './middleware/rateLimit';

const app = new Koa();
const router = new Router();

// 中间件
app.use(cors());
app.use(bodyParser());
app.use(apiRateLimit);

// 调试中间件
app.use(async (ctx, next) => {
  console.log(`${ctx.method} ${ctx.url}`);
  await next();
});

// 基础路由
router.get('/', (ctx) => {
  ctx.body = { message: 'Resume Server API v1.0.0' };
});

router.get('/health', async (ctx) => {
  try {
    await sequelize.authenticate();
    ctx.body = { 
      status: 'OK', 
      timestamp: new Date().toISOString(),
      database: 'connected',
      version: '1.0.0'
    };
  } catch (error) {
    ctx.status = 500;
    ctx.body = { 
      status: 'ERROR', 
      timestamp: new Date().toISOString(),
      database: 'disconnected',
      error: 'Database connection failed'
    };
  }
});

router.get('/test', (ctx) => {
  ctx.body = { message: 'Test route works!' };
});

// 注册所有路由
app.use(router.routes());
app.use(router.allowedMethods());

// 注册认证路由
app.use(authRoutes.routes());
app.use(authRoutes.allowedMethods());

// 注册用户路由
app.use(userRoutes.routes());
app.use(userRoutes.allowedMethods());

// 注册简历路由
app.use(resumeRoutes.routes());
app.use(resumeRoutes.allowedMethods());

// 注册岗位路由
app.use(positionRoutes.routes());
app.use(positionRoutes.allowedMethods());

// 404 错误处理中间件（必须放在所有路由之后）
app.use(async (ctx) => {
  ctx.status = 404;
  ctx.body = { 
    error: 'Not Found',
    message: 'The requested resource was not found',
    availableEndpoints: {
      'GET /': 'API信息',
      'GET /health': '健康检查',
      'POST /auth/register': '用户注册',
      'POST /auth/login': '用户登录',
      'GET /auth/profile': '获取用户信息',
      'POST /auth/init-admin': '初始化管理员用户',
      'GET /users': '获取所有用户（需管理员权限）',
      'GET /users/:id': '获取单个用户',
      'POST /users': '创建用户（需管理员权限）',
      'POST /resumes/list': '获取简历列表',
      'POST /resumes/detail': '获取简历详情',
      'POST /resumes/create': '创建简历',
      'PUT /resumes/:id': '更新简历',
      'POST /resumes/delete': '删除简历',
      'PATCH /resumes/batch-status': '批量更新简历状态（需管理员权限）',
      'GET /resumes/stats/overview': '获取简历统计信息（需管理员权限）',
      'POST /positions/list': '获取岗位列表',
      'POST /positions/detail': '获取岗位详情',
      'POST /positions/create': '创建岗位',
      'PUT /positions/:id': '更新岗位',
      'POST /positions/delete': '删除岗位',
      'PATCH /positions/batch-status': '批量更新岗位状态（需管理员权限）',
      'GET /positions/stats/overview': '获取岗位统计信息（需管理员权限）'
    }
  };
});

// 数据库连接
connectDB().catch(console.error);

export default app;