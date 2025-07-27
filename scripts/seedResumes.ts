import { sequelize } from '../src/config/database';
import Resume from '../src/models/Resume';
import User from '../src/models/User';
import '../src/models/User';
import '../src/models/Resume';

// 测试简历数据
const resumeData = [
  {
    resumeName: '前端开发工程师简历',
    candidateName: '张三',
    phone: '+8613800138001',
    email: 'zhangsan@example.com',
    aiScore: 85.5,
    status: 'approved' as const,
    remarks: '技术能力强，项目经验丰富'
  },
  {
    resumeName: 'Java后端开发简历',
    candidateName: '李四',
    phone: '+8613800138002',
    email: 'lisi@example.com',
    aiScore: 78.2,
    status: 'reviewed' as const,
    remarks: '有一定经验，需要进一步面试'
  },
  {
    resumeName: 'UI/UX设计师作品集',
    candidateName: '王五',
    phone: '+8613800138003',
    email: 'wangwu@example.com',
    aiScore: 92.1,
    status: 'approved' as const,
    remarks: '设计能力出色，作品质量高'
  },
  {
    resumeName: '产品经理简历',
    candidateName: '赵六',
    phone: '+8613800138004',
    email: 'zhaoliu@example.com',
    aiScore: 73.8,
    status: 'pending' as const
  },
  {
    resumeName: '数据分析师简历',
    candidateName: '孙七',
    phone: '+8613800138005',
    email: 'sunqi@example.com',
    aiScore: 88.9,
    status: 'approved' as const,
    remarks: '数据处理能力强，逻辑清晰'
  },
  {
    resumeName: 'DevOps工程师简历',
    candidateName: '周八',
    phone: '+8613800138006',
    email: 'zhouba@example.com',
    aiScore: 81.3,
    status: 'reviewed' as const
  },
  {
    resumeName: '移动端开发简历',
    candidateName: '吴九',
    phone: '+8613800138007',
    email: 'wujiu@example.com',
    aiScore: 76.5,
    status: 'rejected' as const,
    remarks: '技术栈不匹配当前需求'
  },
  {
    resumeName: '测试工程师简历',
    candidateName: '郑十',
    phone: '+8613800138008',
    email: 'zhengshi@example.com',
    aiScore: 79.7,
    status: 'pending' as const
  },
  {
    resumeName: '全栈开发工程师简历',
    candidateName: '陈一一',
    phone: '+8613800138009',
    email: 'chenyiyi@example.com',
    aiScore: 90.2,
    status: 'approved' as const,
    remarks: '全栈能力强，适合小团队'
  },
  {
    resumeName: '算法工程师简历',
    candidateName: '林一二',
    phone: '+8613800138010',
    email: 'linyier@example.com',
    aiScore: 94.6,
    status: 'approved' as const,
    remarks: '算法基础扎实，有深度学习经验'
  },
  {
    resumeName: '运营专员简历',
    candidateName: '黄一三',
    phone: '+8613800138011',
    email: 'huangyisan@example.com',
    aiScore: 68.4,
    status: 'pending' as const
  },
  {
    resumeName: '市场营销简历',
    candidateName: '刘一四',
    phone: '+8613800138012',
    email: 'liuyisi@example.com',
    aiScore: 72.1,
    status: 'reviewed' as const
  },
  {
    resumeName: '人力资源专员简历',
    candidateName: '杨一五',
    phone: '+8613800138013',
    email: 'yangyiwu@example.com',
    aiScore: 75.8,
    status: 'pending' as const
  },
  {
    resumeName: '财务分析师简历',
    candidateName: '何一六',
    phone: '+8613800138014',
    email: 'heyiliu@example.com',
    aiScore: 83.2,
    status: 'approved' as const,
    remarks: '财务分析能力强，有CPA证书'
  },
  {
    resumeName: '架构师简历',
    candidateName: '罗一七',
    phone: '+8613800138015',
    email: 'luoyiqi@example.com',
    aiScore: 96.3,
    status: 'approved' as const,
    remarks: '架构设计经验丰富，技术视野广'
  },
  {
    resumeName: '网络安全工程师简历',
    candidateName: '宋一八',
    phone: '+8613800138016',
    email: 'songyiba@example.com',
    aiScore: 87.4,
    status: 'reviewed' as const
  },
  {
    resumeName: '机器学习工程师简历',
    candidateName: '韩一九',
    phone: '+8613800138017',
    email: 'hanyijiu@example.com',
    aiScore: 91.7,
    status: 'approved' as const,
    remarks: 'ML项目经验丰富，论文发表多篇'
  },
  {
    resumeName: '区块链开发简历',
    candidateName: '冯二十',
    phone: '+8613800138018',
    email: 'fengershi@example.com',
    aiScore: 84.9,
    status: 'pending' as const
  },
  {
    resumeName: '游戏开发工程师简历',
    candidateName: '邓二一',
    phone: '+8613800138019',
    email: 'dengeryi@example.com',
    aiScore: 77.6,
    status: 'rejected' as const,
    remarks: '游戏引擎经验不足'
  },
  {
    resumeName: '云计算工程师简历',
    candidateName: '曹二二',
    phone: '+8613800138020',
    email: 'caoerier@example.com',
    aiScore: 89.1,
    status: 'approved' as const,
    remarks: 'AWS和阿里云经验丰富'
  }
];

async function seedResumes() {
  try {
    // 连接数据库
    await sequelize.authenticate();
    console.log('数据库连接成功');
    
    // 同步模型
    await sequelize.sync();
    console.log('数据库模型同步完成');
    
    // 查找或创建一个用户作为简历的上传者
    let user = await User.findOne({ 
      where: { email: 'admin@example.com' },
      attributes: ['id', 'username', 'email', 'password', 'role', 'isActive', 'lastLogin', 'createdAt', 'updatedAt']
    });
    
    if (!user) {
      // 如果没有管理员用户，创建一个
      user = await User.create({
        username: 'admin',
        email: 'admin@example.com',
        password: 'admin123',
        role: 'admin',
        isActive: true
      });
      console.log('创建管理员用户成功');
    }
    
    // 清空现有简历数据（可选）
    await Resume.destroy({ where: {} });
    console.log('清空现有简历数据');
    
    // 创建简历数据
    let createdCount = 0;
    for (let i = 0; i < resumeData.length; i++) {
      const data = resumeData[i];
      const uploadTime = new Date();
      uploadTime.setDate(uploadTime.getDate() - Math.floor(Math.random() * 30)); // 随机设置过去30天内的上传时间
      
      await Resume.create({
        userId: user.id,
        resumeName: data.resumeName,
        candidateName: data.candidateName,
        phone: data.phone,
        email: data.email,
        aiScore: data.aiScore,
        status: data.status,
        uploadTime: uploadTime,
        remarks: data.remarks,
        fileName: `${data.candidateName}_resume.pdf`,
        fileSize: Math.floor(Math.random() * 2000000) + 500000, // 随机文件大小 0.5-2.5MB
        filePath: `/uploads/resumes/${data.candidateName}_resume.pdf`
      });
      
      createdCount++;
      console.log(`创建简历 ${i + 1}: ${data.candidateName} - ${data.resumeName}`);
    }
    
    console.log(`\n成功创建 ${createdCount} 条简历数据！`);
    
    // 显示统计信息
    const stats = {
      total: await Resume.count(),
      pending: await Resume.count({ where: { status: 'pending' } }),
      reviewed: await Resume.count({ where: { status: 'reviewed' } }),
      approved: await Resume.count({ where: { status: 'approved' } }),
      rejected: await Resume.count({ where: { status: 'rejected' } })
    };
    
    console.log('\n简历统计信息:');
    console.log(`总数: ${stats.total}`);
    console.log(`待审核: ${stats.pending}`);
    console.log(`已审核: ${stats.reviewed}`);
    console.log(`已通过: ${stats.approved}`);
    console.log(`已拒绝: ${stats.rejected}`);
    
  } catch (error) {
    console.error('创建简历数据失败:', error);
  } finally {
    // 关闭数据库连接
    await sequelize.close();
    console.log('\n数据库连接已关闭');
  }
}

// 执行种子数据创建
seedResumes();