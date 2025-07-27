import { sequelize } from '../src/config/database';
import Position from '../src/models/Position';
import User from '../src/models/User';

async function seedPositions() {
  try {
    console.log('连接数据库...');
    await sequelize.authenticate();
    console.log('数据库连接成功');

    // 获取第一个用户作为创建者
    const user = await User.findOne();
    if (!user) {
      console.error('未找到用户，请先创建用户');
      process.exit(1);
    }

    console.log('清空现有岗位数据...');
    await Position.destroy({ where: {} });

    console.log('创建25条岗位数据...');
    const positions: any[] = [
      {
        userId: user.id,
        positionName: 'Java高级工程师',
        department: '技术部',
        description: '负责Java后端开发，参与系统架构设计，优化系统性能。',
        requirements: '3年以上Java开发经验，熟悉Spring Boot、MyBatis等框架，具备良好的编程习惯。',
        salaryMin: 25000,
        salaryMax: 35000,
        workLocation: '北京',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'senior' as 'senior',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-15 10:30:00'),
        publisherId: user.id
      },
      {
        userId: user.id,
        positionName: '前端工程师',
        department: '技术部',
        description: '负责前端页面开发，与设计师和后端工程师协作完成产品功能。',
        requirements: '2年以上前端开发经验，熟悉Vue.js、React等框架，了解TypeScript。',
        salaryMin: 15000,
        salaryMax: 25000,
        workLocation: '上海',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'mid' as 'mid',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-14 16:45:00')
      },
      {
        userId: user.id,
        positionName: '产品经理',
        department: '产品部',
        description: '负责产品规划和需求分析，协调各部门资源推进产品开发。',
        requirements: '3年以上产品经验，具备良好的沟通能力和项目管理能力，熟悉敏捷开发流程。',
        salaryMin: 20000,
        salaryMax: 30000,
        workLocation: '深圳',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'senior' as 'senior',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-13 09:15:00')
      },
      {
        userId: user.id,
        positionName: 'UI设计师',
        department: '设计部',
        description: '负责产品界面设计，制作设计规范，与开发团队协作实现设计效果。',
        requirements: '2年以上UI设计经验，熟练使用Figma、Sketch等设计工具，具备良好的审美能力。',
        salaryMin: 12000,
        salaryMax: 18000,
        workLocation: '杭州',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'mid' as 'mid',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-12 14:20:00')
      },
      {
        userId: user.id,
        positionName: '运维工程师',
        department: '运维部',
        description: '负责服务器运维，监控系统稳定性，处理线上问题。',
        requirements: '2年以上运维经验，熟悉Linux系统，了解Docker、Kubernetes等容器技术。',
        salaryMin: 15000,
        salaryMax: 22000,
        workLocation: '成都',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'mid' as 'mid',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-11 16:30:00')
      },
      {
        userId: user.id,
        positionName: '数据分析师',
        department: '数据部',
        description: '负责业务数据分析，制作数据报表，为业务决策提供数据支持。',
        requirements: '2年以上数据分析经验，熟悉SQL、Python，了解统计学知识。',
        salaryMin: 18000,
        salaryMax: 25000,
        workLocation: '广州',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'mid' as 'mid',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-10 09:45:00')
      },
      {
        userId: user.id,
        positionName: '测试工程师',
        department: '测试部',
        description: '负责软件测试，编写测试用例，保证产品质量。',
        requirements: '2年以上测试经验，熟悉自动化测试工具，具备良好的逻辑思维能力。',
        salaryMin: 12000,
        salaryMax: 20000,
        workLocation: '武汉',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'mid' as 'mid',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-09 11:20:00')
      },
      {
        userId: user.id,
        positionName: '架构师',
        department: '技术部',
        description: '负责系统架构设计，技术选型，指导团队技术发展方向。',
        requirements: '5年以上开发经验，具备大型系统架构设计能力，熟悉微服务架构。',
        salaryMin: 35000,
        salaryMax: 50000,
        workLocation: '北京',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'lead' as 'lead',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-08 15:40:00')
      },
      {
        userId: user.id,
        positionName: '算法工程师',
        department: '算法部',
        description: '负责机器学习算法研发，优化推荐系统，提升用户体验。',
        requirements: '3年以上算法经验，熟悉深度学习框架，具备扎实的数学基础。',
        salaryMin: 30000,
        salaryMax: 45000,
        workLocation: '北京',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'senior' as 'senior',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-07 13:25:00')
      },
      {
        userId: user.id,
        positionName: '移动端开发工程师',
        department: '技术部',
        description: '负责iOS/Android应用开发，优化应用性能和用户体验。',
        requirements: '2年以上移动端开发经验，熟悉Swift/Kotlin，了解跨平台开发技术。',
        salaryMin: 18000,
        salaryMax: 28000,
        workLocation: '深圳',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'mid' as 'mid',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-06 10:15:00')
      },
      {
        userId: user.id,
        positionName: '销售经理',
        department: '销售部',
        description: '负责客户开发和维护，完成销售目标，管理销售团队。',
        requirements: '3年以上销售经验，具备良好的沟通能力和团队管理能力。',
        salaryMin: 15000,
        salaryMax: 25000,
        workLocation: '上海',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'senior' as 'senior',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-05 14:30:00')
      },
      {
        userId: user.id,
        positionName: '市场专员',
        department: '市场部',
        description: '负责市场推广活动策划和执行，品牌宣传和用户增长。',
        requirements: '1年以上市场经验，具备创意思维和执行能力，熟悉新媒体运营。',
        salaryMin: 8000,
        salaryMax: 12000,
        workLocation: '杭州',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'junior' as 'junior',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-04 16:45:00')
      },
      {
        userId: user.id,
        positionName: '人事专员',
        department: '人事部',
        description: '负责招聘工作，员工关系维护，人事制度执行。',
        requirements: '1年以上人事经验，熟悉劳动法规，具备良好的沟通协调能力。',
        salaryMin: 6000,
        salaryMax: 10000,
        workLocation: '成都',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'junior' as 'junior',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-03 09:20:00')
      },
      {
        userId: user.id,
        positionName: '财务分析师',
        department: '财务部',
        description: '负责财务数据分析，预算制定，成本控制分析。',
        requirements: '2年以上财务经验，熟悉财务软件，具备CPA证书优先。',
        salaryMin: 12000,
        salaryMax: 18000,
        workLocation: '广州',
        workType: 'full-time',
        experienceLevel: 'mid',
        status: 'published',
        publishTime: new Date('2024-03-02 11:35:00')
      },
      {
        userId: user.id,
        positionName: '客服专员',
        department: '客服部',
        description: '负责客户咨询处理，问题解答，客户满意度提升。',
        requirements: '具备良好的服务意识和沟通能力，耐心细致，应届生可考虑。',
        salaryMin: 5000,
        salaryMax: 8000,
        workLocation: '武汉',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'entry' as 'entry',
        status: 'published' as 'published',
        publishTime: new Date('2024-03-01 15:10:00')
      },
      {
        userId: user.id,
        positionName: '实习生-Java开发',
        department: '技术部',
        description: '参与Java项目开发，学习企业级开发流程和技术栈。',
        requirements: '计算机相关专业在校生，熟悉Java基础，有学习热情。',
        salaryMin: 3000,
        salaryMax: 5000,
        workLocation: '北京',
        workType: 'internship' as 'internship',
        experienceLevel: 'entry' as 'entry',
        status: 'published' as 'published',
        publishTime: new Date('2024-02-28 10:25:00')
      },
      {
        userId: user.id,
        positionName: '兼职UI设计师',
        department: '设计部',
        description: '负责部分UI设计工作，配合全职设计师完成项目。',
        requirements: '具备UI设计能力，能够独立完成设计任务，时间灵活。',
        salaryMin: 200,
        salaryMax: 300,
        workLocation: '远程',
        workType: 'part-time' as 'part-time',
        experienceLevel: 'mid' as 'mid',
        status: 'published' as 'published',
        publishTime: new Date('2024-02-27 14:40:00')
      },
      {
        userId: user.id,
        positionName: '项目经理',
        department: '项目部',
        description: '负责项目管理，协调资源，确保项目按时交付。',
        requirements: '3年以上项目管理经验，具备PMP证书优先，沟通协调能力强。',
        salaryMin: 20000,
        salaryMax: 30000,
        workLocation: '深圳',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'senior' as 'senior',
        status: 'draft' as 'draft',
      },
      {
        userId: user.id,
        positionName: '网络安全工程师',
        department: '安全部',
        description: '负责网络安全防护，安全事件处理，安全制度建设。',
        requirements: '2年以上安全经验，熟悉网络安全技术，具备安全认证优先。',
        salaryMin: 18000,
        salaryMax: 28000,
        workLocation: '北京',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'mid' as 'mid',
        status: 'draft' as 'draft'
      },
      {
        userId: user.id,
        positionName: '业务分析师',
        department: '业务部',
        description: '负责业务需求分析，流程优化，系统需求整理。',
        requirements: '2年以上业务分析经验，具备良好的逻辑思维和文档能力。',
        salaryMin: 15000,
        salaryMax: 22000,
        workLocation: '上海',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'mid' as 'mid',
        status: 'paused' as 'paused'
      },
      {
        userId: user.id,
        positionName: '技术支持工程师',
        department: '技术部',
        description: '负责技术支持服务，客户问题解决，产品培训。',
        requirements: '1年以上技术支持经验，具备良好的服务意识和技术能力。',
        salaryMin: 10000,
        salaryMax: 15000,
        workLocation: '杭州',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'junior' as 'junior',
        status: 'paused' as 'paused'
      },
      {
        userId: user.id,
        positionName: '数据库管理员',
        department: '技术部',
        description: '负责数据库运维，性能优化，数据备份恢复。',
        requirements: '2年以上DBA经验，熟悉MySQL、Oracle等数据库，具备调优能力。',
        salaryMin: 16000,
        salaryMax: 24000,
        workLocation: '成都',
        workType: 'full-time' as 'full-time',
        experienceLevel: 'mid' as 'mid',
        status: 'closed' as 'closed',
        closeTime: new Date('2024-02-25 17:00:00')
      },
      {
        userId: user.id,
        positionName: '内容运营',
        department: '运营部',
        description: '负责内容策划和制作，用户运营，活动策划执行。',
        requirements: '1年以上运营经验，具备内容创作能力，熟悉社交媒体运营。',
        salaryMin: 8000,
        salaryMax: 14000,
        workLocation: '广州',
        workType: 'full-time',
        experienceLevel: 'junior',
        status: 'closed',
        closeTime: new Date('2024-02-24 16:30:00')
      },
      {
        userId: user.id,
        positionName: '合同开发工程师',
        department: '技术部',
        description: '短期项目开发，参与特定功能模块开发。',
        requirements: '3年以上开发经验，能够快速上手项目，独立完成开发任务。',
        salaryMin: 25000,
        salaryMax: 35000,
        workLocation: '北京',
        workType: 'contract' as 'contract',
        experienceLevel: 'senior' as 'senior',
        status: 'published' as 'published',
        publishTime: new Date('2024-02-23 12:15:00')
      },
      {
        userId: user.id,
        positionName: '质量保证工程师',
        department: '质量部',
        description: '负责质量体系建设，流程优化，质量问题跟踪。',
        requirements: '2年以上QA经验，熟悉质量管理体系，具备流程梳理能力。',
        salaryMin: 14000,
        salaryMax: 20000,
        workLocation: '深圳',
        workType: 'full-time',
        experienceLevel: 'mid',
        status: 'published',
        publishTime: new Date('2024-02-22 09:50:00')
      }
    ];

    for (let i = 0; i < positions.length; i++) {
      await Position.create(positions[i] as any);
      console.log(`创建第${i + 1}条岗位数据: ${positions[i].positionName}`);
    }
    console.log('成功创建25条岗位数据');

    // 统计各状态岗位数量
    const stats = await Position.findAll({
      attributes: [
        'status',
        [Position.sequelize!.fn('COUNT', Position.sequelize!.col('id')), 'count']
      ],
      group: ['status'],
      raw: true
    }) as any;

    console.log('岗位状态统计:');
    stats.forEach((stat: any) => {
      console.log(`${stat.status}: ${stat.count}条`);
    });

    process.exit(0);
  } catch (error) {
    console.error('操作失败:', error);
    process.exit(1);
  }
}

seedPositions();