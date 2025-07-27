const Koa = require('koa');
const Router = require('koa-router');

const app = new Koa();
const router = new Router();

router.get('/', (ctx) => {
  ctx.body = 'Hello World!';
});

router.get('/test', (ctx) => {
  ctx.body = { message: 'Simple test works!' };
});

app.use(router.routes());
app.use(router.allowedMethods());

app.listen(3001, () => {
  console.log('Test server running on port 3001');
});