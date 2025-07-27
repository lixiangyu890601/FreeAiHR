import Router from 'koa-router';
import { Op } from 'sequelize';
import { auth, adminAuth } from '../middleware/auth';
import { apiRateLimit } from '../middleware/rateLimit';
import Resume from '../models/Resume';
import User from '../models/User';
import { AppContext } from '../types';

const router = new Router<any, AppContext>({
  prefix: '/resumes'
});

// 应用认证中间件到所有路由
router.use(auth, apiRateLimit);

// 获取简历列表（支持分页和搜索）
router.post('/list', async (ctx) => {
  try {
    const {
      page = 1,
      limit = 10,
      search = '',
      status = '',
      sortBy = 'uploadTime',
      sortOrder = 'DESC',
      filters = {},
      resumeName = '',
      candidateName = '',
      phone = '',
      email = '',
      ...otherParams
    } = ctx.request.body as any;

    const offset = (Number(page) - 1) * Number(limit);
    const whereConditions: any = {};

    // 如果不是管理员，只能查看自己的简历
    if (ctx.user?.role !== 'admin') {
      whereConditions.userId = ctx.user?.id;
    }

    // 搜索条件 - 去除空格后检查是否为空
    const trimmedSearch = search ? search.trim() : '';
    if (trimmedSearch) {
      whereConditions[Op.or] = [
        { resumeName: { [Op.like]: `%${trimmedSearch}%` } },
        { candidateName: { [Op.like]: `%${trimmedSearch}%` } },
        { email: { [Op.like]: `%${trimmedSearch}%` } },
        { phone: { [Op.like]: `%${trimmedSearch}%` } }
      ];
    }

    // 状态筛选 - 去除空格后检查是否为空
    const trimmedStatus = status ? status.trim() : '';
    if (trimmedStatus) {
      whereConditions.status = trimmedStatus;
    }

    // 直接字段筛选 - 去除空格后检查是否为空
    const trimmedResumeName = resumeName ? resumeName.trim() : '';
    if (trimmedResumeName) {
      whereConditions.resumeName = { [Op.like]: `%${trimmedResumeName}%` };
    }

    const trimmedCandidateName = candidateName ? candidateName.trim() : '';
    if (trimmedCandidateName) {
      whereConditions.candidateName = { [Op.like]: `%${trimmedCandidateName}%` };
    }

    const trimmedPhone = phone ? phone.trim() : '';
    if (trimmedPhone) {
      whereConditions.phone = { [Op.like]: `%${trimmedPhone}%` };
    }

    const trimmedEmail = email ? email.trim() : '';
    if (trimmedEmail) {
      whereConditions.email = { [Op.like]: `%${trimmedEmail}%` };
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
    })

    const { count, rows } = await Resume.findAndCountAll({
      where: whereConditions,
      include: [
        {
          model: User,
          as: 'user',
          attributes: ['id', 'username', 'email']
        },
        {
          model: User,
          as: 'reviewer',
          attributes: ['id', 'username', 'email'],
          required: false
        }
      ],
      order: [[sortBy as string, sortOrder as string]],
      limit: Number(limit),
      offset: offset
    });

    ctx.body = {
      resumes: rows,
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

// 获取单个简历详情
router.post('/detail', async (ctx) => {
  try {
    const { id } = ctx.request.body as any;
    const resumeId = parseInt(id, 10);
    
    if (!resumeId || isNaN(resumeId)) {
      ctx.status = 400;
      ctx.body = { error: 'Invalid resume ID' };
      return;
    }
    
    const resume = await Resume.findByPk(resumeId, {
      include: [
        {
          model: User,
          as: 'user',
          attributes: ['id', 'username', 'email']
        },
        {
          model: User,
          as: 'reviewer',
          attributes: ['id', 'username', 'email'],
          required: false
        }
      ]
    });

    if (!resume) {
      ctx.status = 404;
      ctx.body = { error: 'Resume not found' };
      return;
    }

    // 权限检查：普通用户只能查看自己的简历
    if (ctx.user?.role !== 'admin' && resume.userId !== ctx.user?.id) {
      ctx.status = 403;
      ctx.body = { error: 'Access denied' };
      return;
    }

    ctx.body = { resume };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 创建简历
router.post('/create', async (ctx) => {
  try {
    const resumeData = ctx.request.body as any;
    
    // 设置上传用户ID
    resumeData.userId = ctx.user?.id;
    resumeData.uploadTime = new Date();
    
    // 创建简历
    const resume = await Resume.create(resumeData);
    
    // 返回创建的简历信息
    const createdResume = await Resume.findByPk(resume.id, {
      include: [
        {
          model: User,
          as: 'user',
          attributes: ['id', 'username', 'email']
        }
      ]
    });
    
    ctx.status = 201;
    ctx.body = { resume: createdResume };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 更新简历
router.put('/:id', async (ctx) => {
  try {
    const resumeId = parseInt(ctx.params.id, 10);
    const updateData = ctx.request.body as any;
    
    const resume = await Resume.findByPk(resumeId);
    
    if (!resume) {
      ctx.status = 404;
      ctx.body = { error: 'Resume not found' };
      return;
    }
    
    // 权限检查：普通用户只能更新自己的简历，管理员可以更新所有简历
    if (ctx.user?.role !== 'admin' && resume.userId !== ctx.user?.id) {
      ctx.status = 403;
      ctx.body = { error: 'Access denied' };
      return;
    }
    
    // 如果是管理员更新状态，记录审核信息
    if (ctx.user?.role === 'admin' && updateData.status && updateData.status !== resume.status) {
      updateData.reviewTime = new Date();
      updateData.reviewerId = ctx.user.id;
    }
    
    // 更新简历
    await resume.update(updateData);
    
    // 返回更新后的简历信息
    const updatedResume = await Resume.findByPk(resumeId, {
      include: [
        {
          model: User,
          as: 'user',
          attributes: ['id', 'username', 'email']
        },
        {
          model: User,
          as: 'reviewer',
          attributes: ['id', 'username', 'email'],
          required: false
        }
      ]
    });
    
    ctx.body = { resume: updatedResume };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 删除简历
router.post('/delete', async (ctx) => {
  try {
    const { id } = ctx.request.body as any;
    const resumeId = parseInt(id, 10);
    
    if (!resumeId || isNaN(resumeId)) {
      ctx.status = 400;
      ctx.body = { error: 'Invalid resume ID' };
      return;
    }
    
    const resume = await Resume.findByPk(resumeId);
    
    if (!resume) {
      ctx.status = 404;
      ctx.body = { error: 'Resume not found' };
      return;
    }
    
    // 权限检查：普通用户只能删除自己的简历，管理员可以删除所有简历
    if (ctx.user?.role !== 'admin' && resume.userId !== ctx.user?.id) {
      ctx.status = 403;
      ctx.body = { error: 'Access denied' };
      return;
    }
    
    await resume.destroy();
    
    ctx.body = { message: 'Resume deleted successfully' };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 批量更新简历状态（仅管理员）
router.patch('/batch-status', adminAuth, async (ctx) => {
  try {
    const { resumeIds, status, remarks } = ctx.request.body as any;
    
    if (!resumeIds || !Array.isArray(resumeIds) || resumeIds.length === 0) {
      ctx.status = 400;
      ctx.body = { error: 'Resume IDs are required' };
      return;
    }
    
    if (!status) {
      ctx.status = 400;
      ctx.body = { error: 'Status is required' };
      return;
    }
    
    const updateData: any = {
      status,
      reviewTime: new Date(),
      reviewerId: ctx.user?.id
    };
    
    if (remarks) {
      updateData.remarks = remarks;
    }
    
    const [affectedCount] = await Resume.update(updateData, {
      where: {
        id: {
          [Op.in]: resumeIds
        }
      }
    });
    
    ctx.body = {
      message: `Successfully updated ${affectedCount} resumes`,
      affectedCount
    };
  } catch (error) {
    ctx.status = 400;
    ctx.body = { error: (error as Error).message };
  }
});

// 获取简历统计信息（仅管理员）
router.get('/stats/overview', adminAuth, async (ctx) => {
  try {
    const totalResumes = await Resume.count();
    const pendingResumes = await Resume.count({ where: { status: 'pending' } });
    const reviewedResumes = await Resume.count({ where: { status: 'reviewed' } });
    const approvedResumes = await Resume.count({ where: { status: 'approved' } });
    const rejectedResumes = await Resume.count({ where: { status: 'rejected' } });
    
    // 获取今日上传的简历数量
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const todayResumes = await Resume.count({
      where: {
        uploadTime: {
          [Op.gte]: today
        }
      }
    });
    
    // 获取平均AI评分
    const avgScore = await Resume.findOne({
      attributes: [
        [Resume.sequelize!.fn('AVG', Resume.sequelize!.col('aiScore')), 'avgScore']
      ],
      where: {
        aiScore: {
          [Op.not]: null as any
        }
      },
      raw: true
    }) as any;
    
    ctx.body = {
      stats: {
        total: totalResumes,
        pending: pendingResumes,
        reviewed: reviewedResumes,
        approved: approvedResumes,
        rejected: rejectedResumes,
        todayUploads: todayResumes,
        averageScore: avgScore?.avgScore ? parseFloat(avgScore.avgScore).toFixed(1) : null
      }
    };
  } catch (error) {
    ctx.status = 500;
    ctx.body = { error: (error as Error).message };
  }
});

export default router;