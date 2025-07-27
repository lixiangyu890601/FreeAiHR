import { Next } from 'koa';
// @ts-ignore
import { verifyToken } from '../utils/jwt';
import User from '../models/User';
import { AppContext } from '../types';
import { JwtPayload } from 'jsonwebtoken';

export const auth = async (ctx: AppContext, next: Next): Promise<void> => {
  try {
    const token = ctx.headers.authorization?.replace('Bearer ', '');
    
    if (!token) {
      ctx.status = 401;
      ctx.body = { error: 'Access denied. No token provided.' };
      return;
    }
    
    const decoded = verifyToken(token) as JwtPayload;
    const user = await User.findByIdSafe(decoded.userId);
    
    if (!user || !user.isActive) {
      ctx.status = 401;
      ctx.body = { error: 'Invalid token or user not found.' };
      return;
    }
    
    ctx.user = user;
    await next();
  } catch (error) {
    ctx.status = 401;
    ctx.body = { error: 'Invalid token.' };
  }
};

export const adminAuth = async (ctx: AppContext, next: Next): Promise<void> => {
  if ((ctx.user as any).role !== 'admin') {
    ctx.status = 403;
    ctx.body = { error: 'Access denied. Admin role required.' };
    return;
  }
  await next();
};