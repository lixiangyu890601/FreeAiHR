import Router from 'koa-router';
import { Op } from 'sequelize';
import Position from '../models/Position';
import User from '../models/User';
import { auth, adminAuth } from '../middleware/auth';
import { AppContext } from '../types';

const router = new Router<any, AppContext>({
  prefix: '/positions'
});

// 应用认证中间件到所有路由
router.use(auth);

// 获取岗位列表（支持分页和搜索）
router.post('/list', async (ctx) => {
  try {
    const {
      page = 1,
      limit = 10,
      search = '',
      status = '',
      department = '',
      workType = '',
      experienceLevel = '',
      sortBy = 'createdAt',
      sortOrder = 'DESC',
      filters = {},
      positionName = '',
      workLocation = '',
      ...otherParams
    } = ctx.request.body as any;

    const offset = (Number(page) - 1) * Number(limit);
    const whereConditions: any = {};

    // 如果不是管理员，只能查看自己的岗位
    if (ctx.user?.role !== 'admin') {
      whereConditions.userId = ctx.user?.id;
    }

    // 搜索条件 - 去除空格后检查是否为空
    const trimmedSearch = search ? search.trim() : '';
    if (trimmedSearch) {
      whereConditions[Op.or] = [
        { positionName: { [Op.like]: `%${trimmedSearch}%` } },
        { department: { [Op.like]: `%${trimmedSearch}%` } },
        { description: { [Op.like]: `%${trimmedSearch}%` } },
        { workLocation: { [Op.like]: `%${trimmedSearch}%` } }
      ];
    }

    // 状态筛选 - 去除空格后检查是否为空
    const trimmedStatus = status ? status.trim() : '';
    if (trimmedStatus) {
      whereConditions.status = trimmedStatus;
    }

    // 直接字段筛选 - 去除空格后检查是否为空
    const trimmedPositionName = positionName ? positionName.trim() : '';
    if (trimmedPositionName) {
      whereConditions.positionName = { [Op.like]: `%${trimmedPositionName}%` };
    }

    const trimmedDepartment = department ? department.trim() : '';
    if (trimmedDepartment) {
      whereConditions.department = { [Op.like]: `%${trimmedDepartment}%` };
    }

    const trimmedWorkLocation = workLocation ? workLocation.trim() : '';
    if (trimmedWorkLocation) {
      whereConditions.workLocation = { [Op.like]: `%${trimmedWorkLocation}%` };
    }

    const trimmedWorkType = workType ? workType.trim() : '';
    if (trimmedWorkType) {
      whereConditions.workType = trimmedWorkType;
    }

    const trimmedExperienceLevel = experienceLevel ? experienceLevel.trim() : '';
    if (trimmedExperienceLevel) {
      whereConditions.experienceLevel = trimmedExperienceLevel;
    }

    // 额外的筛选条件 - 去除空格后检查是否为空
    if (filters && typeof filters === 'object') {
      Object.keys(filters).forEach(key => {
        const value = filters[key];
        if (value !== undefined && value !== null) {
          const trimmedValue = typeof value === 'string' ? value.trim() : value;
          if (trimmedValue !== '') {
            whereConditions[key] = trimmedValue;
          }
        }
      });
    }

    // 处理其他参数 - 去除空格后检查是否为空
    Object.keys(otherParams).forEach(key => {
      // 排除分页和排序相关参数
      if (!['page', 'limit', 'pageSize', 'sortBy', 'sortOrder'].includes(key)) {
        const value = otherParams[key];
        if (value !== undefined && value !== null) {
          const trimmedValue = typeof value === 'string' ? value.trim() : value;
          if (trimmedValue !== '') {
            whereConditions[key] = trimmedValue;
          }
        }
      }
    });

    const { count, rows } = await Position.findAndCountAll({
      where: whereConditions,
      include: [
        {
          model: User,
          as: 'user',
          attributes: ['id', 'username', 'email']
        },
        {
          model: User,
          as: 'publisher',
          attributes: ['id', 'username', 'email'],
          required: false
        }
      ],
      order: [[sortBy as string, sortOrder as string]],
      limit: Number(limit),
      offset: offset
    });

    ctx.body = {
      positions: rows,
      pagination: {
        total: count,
        page: Number(page),
        pageSize: Number(limit),
        totalPages: Math.ceil(count / Number(limit))
      }
    };
  } catch (error) {
    ctx.status = 500;
    ctx.body = { error: (error as Error).message };
  }
});

// 获取单个岗位详情
router.post('/detail', async (ctx) => {
  try {
    const { id } = ctx.request.body as any;
    const positionId = parseInt(id, 10);
    
    if (!positionId || isNaN(positionId)) {
      ctx.status = 400;
      ctx.body = { error: 'Invalid position ID' };
      return;
    }
    
    const position = await Position.findByPk(positionId, {
      include: [
        {
          model: User,
          as: 'user',
          attributes: ['id', 'username', 'email']
        },
        {
          model: User,
          as: 'publisher',
          attributes: ['id', 'username', 'email'],
          required: false
        }
      ]
    });

    if (!position) {
      ctx.status = 404;
      ctx.body = { error: 'Position not found' };
      return;
    }

    // 权限检查：普通用户只能查看自己的岗位
    if (ctx.user?.role !== 'admin' && position.userId !== ctx.user?.id) {
      ctx.status = 403;
      ctx.body = { error: 'Access denied' };
      return;
    }

    ctx.body = { position };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 创建岗位
router.post('/create', async (ctx) => {
  try {
    const positionData = ctx.request.body as any;
    
    // 设置创建用户ID
    positionData.userId = ctx.user?.id;
    
    // 创建岗位
    const position = await Position.create(positionData);
    
    // 返回创建的岗位信息
    const createdPosition = await Position.findByPk(position.id, {
      include: [
        {
          model: User,
          as: 'user',
          attributes: ['id', 'username', 'email']
        }
      ]
    });
    
    ctx.status = 201;
    ctx.body = { position: createdPosition };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 更新岗位
router.post('/update', async (ctx) => {
  try {
    const { id, ...updateData } = ctx.request.body as any;
    const positionId = parseInt(id, 10);
    
    const position = await Position.findByPk(positionId);
    
    if (!position) {
      ctx.status = 404;
      ctx.body = { error: 'Position not found' };
      return;
    }
    
    // 权限检查：普通用户只能更新自己的岗位，管理员可以更新所有岗位
    if (ctx.user?.role !== 'admin' && position.userId !== ctx.user?.id) {
      ctx.status = 403;
      ctx.body = { error: 'Access denied' };
      return;
    }
    
    // 如果是管理员更新状态为发布，记录发布信息
    if (ctx.user?.role === 'admin' && updateData.status === 'published' && position.status !== 'published') {
      updateData.publishTime = new Date();
      updateData.publisherId = ctx.user.id;
    }
    
    // 如果状态改为关闭，记录关闭时间
    if (updateData.status === 'closed' && position.status !== 'closed') {
      updateData.closeTime = new Date();
    }
    
    // 更新岗位
    await position.update(updateData);
    
    // 返回更新后的岗位信息
    const updatedPosition = await Position.findByPk(positionId, {
      include: [
        {
          model: User,
          as: 'user',
          attributes: ['id', 'username', 'email']
        },
        {
          model: User,
          as: 'publisher',
          attributes: ['id', 'username', 'email'],
          required: false
        }
      ]
    });
    
    ctx.body = { position: updatedPosition };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 删除岗位
router.post('/delete', async (ctx) => {
  try {
    const { id } = ctx.request.body as any;
    const positionId = parseInt(id, 10);
    
    if (!positionId || isNaN(positionId)) {
      ctx.status = 400;
      ctx.body = { error: 'Invalid position ID' };
      return;
    }
    
    const position = await Position.findByPk(positionId);
    
    if (!position) {
      ctx.status = 404;
      ctx.body = { error: 'Position not found' };
      return;
    }
    
    // 权限检查：普通用户只能删除自己的岗位，管理员可以删除所有岗位
    if (ctx.user?.role !== 'admin' && position.userId !== ctx.user?.id) {
      ctx.status = 403;
      ctx.body = { error: 'Access denied' };
      return;
    }
    
    await position.destroy();
    
    ctx.body = { message: 'Position deleted successfully' };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 批量更新岗位状态（仅管理员）
router.patch('/batch-status', adminAuth, async (ctx) => {
  try {
    const { positionIds, status, remarks } = ctx.request.body as any;
    
    if (!positionIds || !Array.isArray(positionIds) || positionIds.length === 0) {
      ctx.status = 400;
      ctx.body = { error: 'Position IDs are required' };
      return;
    }
    
    if (!status) {
      ctx.status = 400;
      ctx.body = { error: 'Status is required' };
      return;
    }
    
    const updateData: any = {
      status
    };
    
    // 如果状态是发布，记录发布信息
    if (status === 'published') {
      updateData.publishTime = new Date();
      updateData.publisherId = ctx.user?.id;
    }
    
    // 如果状态是关闭，记录关闭时间
    if (status === 'closed') {
      updateData.closeTime = new Date();
    }
    
    if (remarks) {
      updateData.remarks = remarks;
    }
    
    const [affectedCount] = await Position.update(updateData, {
      where: {
        id: {
          [Op.in]: positionIds
        }
      }
    });
    
    ctx.body = {
      message: `Successfully updated ${affectedCount} positions`,
      affectedCount
    };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 获取岗位统计信息（仅管理员）
router.get('/stats/overview', adminAuth, async (ctx) => {
  try {
    const totalPositions = await Position.count();
    const draftPositions = await Position.count({ where: { status: 'draft' } });
    const publishedPositions = await Position.count({ where: { status: 'published' } });
    const pausedPositions = await Position.count({ where: { status: 'paused' } });
    const closedPositions = await Position.count({ where: { status: 'closed' } });
    
    // 获取今日创建的岗位数量
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const todayPositions = await Position.count({
      where: {
        createdAt: {
          [Op.gte]: today
        }
      }
    });
    
    // 按部门统计岗位数量
    const departmentStats = await Position.findAll({
      attributes: [
        'department',
        [Position.sequelize!.fn('COUNT', Position.sequelize!.col('id')), 'count']
      ],
      group: ['department'],
      order: [[Position.sequelize!.fn('COUNT', Position.sequelize!.col('id')), 'DESC']],
      limit: 10,
      raw: true
    }) as any;
    
    // 按工作类型统计岗位数量
    const workTypeStats = await Position.findAll({
      attributes: [
        'workType',
        [Position.sequelize!.fn('COUNT', Position.sequelize!.col('id')), 'count']
      ],
      group: ['workType'],
      raw: true
    }) as any;
    
    ctx.body = {
      stats: {
        total: totalPositions,
        draft: draftPositions,
        published: publishedPositions,
        paused: pausedPositions,
        closed: closedPositions,
        todayCreated: todayPositions,
        departmentStats,
        workTypeStats
      }
    };
  } catch (error) {
    ctx.status = 500;
    ctx.body = { error: (error as Error).message };
  }
});

export default router;