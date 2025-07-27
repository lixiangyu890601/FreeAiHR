import { Context } from 'koa';
// 定义用户属性接口
export interface UserAttributes {
  id: number;
  username: string;
  password: string;
  email?: string;
  role?: string;  // 添加这一行
  createdAt?: Date;
  updatedAt?: Date;
}

// 扩展Koa的Context类型
export interface AppContext extends Context {
  user?: UserAttributes;
}

// 自定义JWT Payload类型
export interface CustomJwtPayload {
  userId: number;
  iat?: number;
  exp?: number;
}