declare module 'koa-ratelimit' {
  import { Context, Middleware } from 'koa';
  
  interface RateLimitOptions {
    driver: string;
    db: any;
    duration: number;
    max: number;
    id: (ctx: Context) => string;
    headers?: {
      remaining?: string;
      reset?: string;
      total?: string;
    };
    disableHeader?: boolean;
    message?: string;
  }
  
  function rateLimit(options: RateLimitOptions): Middleware;
  export = rateLimit;
}