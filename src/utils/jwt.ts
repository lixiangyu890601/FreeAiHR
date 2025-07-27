import jwt from 'jsonwebtoken';
import { CustomJwtPayload } from '../types';

export const generateToken = (payload: CustomJwtPayload): string => {
  const secret = process.env.JWT_SECRET || 'fallback-secret';
  const expiry = process.env.JWT_EXPIRE || '7d';
  return jwt.sign(payload, secret, {
    expiresIn: expiry as string
  } as jwt.SignOptions);
};

export const verifyToken = (token: string): CustomJwtPayload => {
  const secret = process.env.JWT_SECRET || 'fallback-secret';
  return jwt.verify(token, secret) as CustomJwtPayload;
};