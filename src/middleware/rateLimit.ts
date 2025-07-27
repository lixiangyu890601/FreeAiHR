import rateLimit from 'koa-ratelimit';
import { Context } from 'koa';

// 内存存储
const db = new Map();

// 登录限流：每分钟5次尝试
export const loginRateLimit = rateLimit({
  driver: 'memory',
  db: db,
  duration: 60 * 1000, // 1分钟
  max: 5, // 最大请求数
  id: (ctx: Context) => ctx.ip,
  headers: {
    remaining: 'Rate-Limit-Remaining',
    reset: 'Rate-Limit-Reset',
    total: 'Rate-Limit-Total'
  },
  disableHeader: false
  // message: '尝试次数过多，请稍后再试。' // 移除这行
});

// API通用限流：每分钟100次请求
export const apiRateLimit = rateLimit({
  driver: 'memory',
  db: db,
  duration: 60 * 1000, // 1分钟
  max: 100, // 最大请求数
  id: (ctx: Context) => ctx.ip,
  headers: {
    remaining: 'Rate-Limit-Remaining',
    reset: 'Rate-Limit-Reset',
    total: 'Rate-Limit-Total'
  },
  disableHeader: false
  // message: '请求过于频繁，请稍后再试。' // 移除这行
});