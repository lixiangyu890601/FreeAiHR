import { Context, Next } from 'koa';

const errorHandler = async (ctx: Context, next: Next): Promise<void> => {
  try {
    await next();
  } catch (err) {
    const error = err as Error & { status?: number; code?: string };
    
    // 记录错误
    console.error('Error:', error);
    
    // 设置状态码
    ctx.status = error.status || 500;
    
    // 设置响应体
    ctx.body = {
      error: {
        message: process.env.NODE_ENV === 'development' 
          ? error.message 
          : 'An internal server error occurred',
        ...(process.env.NODE_ENV === 'development' && { 
          stack: error.stack,
          code: error.code
        })
      }
    };
    
    // 触发应用级错误事件
    ctx.app.emit('error', error, ctx);
  }
};

export default errorHandler;