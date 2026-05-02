-- FreeHire 智能招聘系统 - 数据库初始化脚本
-- PostgreSQL

-- =============================================
-- 系统管理模块
-- =============================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    avatar VARCHAR(255),
    email VARCHAR(100),
    phone VARCHAR(20),
    gender SMALLINT DEFAULT 0,
    dept_id BIGINT,
    status SMALLINT DEFAULT 1,
    last_login_time TIMESTAMP,
    last_login_ip VARCHAR(50),
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.real_name IS '姓名';
COMMENT ON COLUMN sys_user.gender IS '性别(0-未知 1-男 2-女)';
COMMENT ON COLUMN sys_user.status IS '状态(0-禁用 1-正常)';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGSERIAL PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    status SMALLINT DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE sys_role IS '角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE(user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    dept_name VARCHAR(50) NOT NULL,
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    sort INT DEFAULT 0,
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE sys_dept IS '部门表';

-- 菜单/权限表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(50) NOT NULL,
    menu_type CHAR(1) DEFAULT 'M',
    path VARCHAR(200),
    component VARCHAR(200),
    permission VARCHAR(100),
    icon VARCHAR(100),
    sort INT DEFAULT 0,
    visible SMALLINT DEFAULT 1,
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE sys_menu IS '菜单权限表';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型(M-目录 C-菜单 F-按钮)';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    UNIQUE(role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色菜单关联表';

-- =============================================
-- 职位模块
-- =============================================

-- 职位表
CREATE TABLE IF NOT EXISTS job_position (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    dept_id BIGINT,
    job_type VARCHAR(20) DEFAULT 'full_time',
    city VARCHAR(50),
    address VARCHAR(200),
    salary_min INT,
    salary_max INT,
    salary_month INT DEFAULT 12,
    education VARCHAR(20),
    experience VARCHAR(50),
    headcount INT DEFAULT 1,
    description TEXT,
    requirements TEXT,
    highlights TEXT,
    status SMALLINT DEFAULT 1,
    urgent SMALLINT DEFAULT 0,
    publish_date DATE,
    deadline DATE,
    hr_user_id BIGINT,
    view_count INT DEFAULT 0,
    apply_count INT DEFAULT 0,
    tags VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE job_position IS '职位表';
COMMENT ON COLUMN job_position.job_type IS '职位类型(full_time-全职 part_time-兼职 intern-实习)';
COMMENT ON COLUMN job_position.status IS '状态(0-关闭 1-招聘中 2-暂停)';

-- =============================================
-- 候选人模块
-- =============================================

-- 候选人表
CREATE TABLE IF NOT EXISTS candidate (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    gender SMALLINT DEFAULT 0,
    birth_date DATE,
    age INT,
    city VARCHAR(50),
    education VARCHAR(20),
    school VARCHAR(100),
    major VARCHAR(100),
    work_years INT,
    current_company VARCHAR(100),
    current_position VARCHAR(100),
    expect_position VARCHAR(100),
    expect_city VARCHAR(50),
    expect_salary INT,
    skills VARCHAR(500),
    avatar VARCHAR(255),
    latest_resume_id BIGINT,
    source VARCHAR(50),
    source_detail VARCHAR(200),
    referrer_id BIGINT,
    tags VARCHAR(500),
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE candidate IS '候选人表';

-- 简历表
CREATE TABLE IF NOT EXISTS resume (
    id BIGSERIAL PRIMARY KEY,
    candidate_id BIGINT,
    file_name VARCHAR(200),
    file_path VARCHAR(500),
    file_type VARCHAR(20),
    file_size BIGINT,
    parsed SMALLINT DEFAULT 0,
    parse_status VARCHAR(20) DEFAULT 'pending',
    parsed_content TEXT,
    raw_text TEXT,
    name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    gender VARCHAR(10),
    birth_date DATE,
    age INT,
    city VARCHAR(50),
    education VARCHAR(20),
    school VARCHAR(100),
    major VARCHAR(100),
    work_years INT,
    current_company VARCHAR(100),
    current_position VARCHAR(100),
    expect_position VARCHAR(100),
    expect_city VARCHAR(50),
    expect_salary INT,
    skills VARCHAR(500),
    source VARCHAR(50),
    source_detail VARCHAR(200),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE resume IS '简历表';
COMMENT ON COLUMN resume.parse_status IS '解析状态(pending-待解析 processing-解析中 success-成功 failed-失败)';

-- 求职申请表
CREATE TABLE IF NOT EXISTS application (
    id BIGSERIAL PRIMARY KEY,
    candidate_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    resume_id BIGINT,
    stage VARCHAR(30) DEFAULT 'new',
    match_score INT,
    match_analysis TEXT,
    hr_user_id BIGINT,
    reject_reason VARCHAR(500),
    source VARCHAR(50),
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stage_update_time TIMESTAMP,
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE application IS '求职申请表';
COMMENT ON COLUMN application.stage IS '当前阶段(new/filtered/interview_pending/interviewing/interview_passed/offer_pending/offered/onboarded/rejected/withdrawn)';

-- =============================================
-- 面试模块
-- =============================================

-- 面试表
CREATE TABLE IF NOT EXISTS interview (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    round INT DEFAULT 1,
    interview_type VARCHAR(20) DEFAULT 'onsite',
    interview_time TIMESTAMP,
    duration INT DEFAULT 60,
    location VARCHAR(200),
    meeting_link VARCHAR(500),
    interviewer_ids VARCHAR(200),
    status VARCHAR(20) DEFAULT 'scheduled',
    feedback TEXT,
    score INT,
    result VARCHAR(20),
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE interview IS '面试表';
COMMENT ON COLUMN interview.interview_type IS '面试类型(onsite-现场 phone-电话 video-视频)';
COMMENT ON COLUMN interview.status IS '状态(scheduled-已安排 ongoing-进行中 completed-已完成 cancelled-已取消)';
COMMENT ON COLUMN interview.result IS '结果(pass-通过 fail-不通过 pending-待定)';

-- =============================================
-- 系统配置模块
-- =============================================

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    config_type VARCHAR(50),
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_config IS '系统配置表';

-- AI配置表（支持BYOK）
CREATE TABLE IF NOT EXISTS ai_config (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,
    api_key VARCHAR(200),
    base_url VARCHAR(200),
    model VARCHAR(100),
    is_default SMALLINT DEFAULT 0,
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ai_config IS 'AI服务配置表';

-- =============================================
-- 商业化/套餐模块
-- =============================================

-- 套餐配置表
CREATE TABLE IF NOT EXISTS subscription_plan (
    id BIGSERIAL PRIMARY KEY,
    plan_code VARCHAR(50) NOT NULL UNIQUE,
    plan_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) DEFAULT 0,
    billing_cycle VARCHAR(20) DEFAULT 'monthly',
    -- 功能开关
    feature_ai_parse SMALLINT DEFAULT 0,
    feature_ai_match SMALLINT DEFAULT 0,
    feature_ai_generate_jd SMALLINT DEFAULT 0,
    feature_talent_pool SMALLINT DEFAULT 0,
    feature_data_report SMALLINT DEFAULT 0,
    feature_career_site SMALLINT DEFAULT 0,
    feature_api_access SMALLINT DEFAULT 0,
    -- 用量限制 (-1表示无限)
    limit_job_count INT DEFAULT 3,
    limit_resume_count INT DEFAULT 50,
    limit_user_count INT DEFAULT 1,
    limit_ai_parse_monthly INT DEFAULT 0,
    limit_ai_match_monthly INT DEFAULT 0,
    -- 其他
    sort INT DEFAULT 0,
    is_default SMALLINT DEFAULT 0,
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE subscription_plan IS '套餐配置表';
COMMENT ON COLUMN subscription_plan.plan_code IS '套餐编码(free/basic/pro/enterprise)';
COMMENT ON COLUMN subscription_plan.billing_cycle IS '计费周期(monthly/yearly/permanent)';
COMMENT ON COLUMN subscription_plan.feature_ai_parse IS 'AI简历解析功能(0-关闭 1-开启)';
COMMENT ON COLUMN subscription_plan.feature_ai_match IS 'AI智能匹配功能(0-关闭 1-开启)';
COMMENT ON COLUMN subscription_plan.limit_job_count IS '职位数量限制(-1无限)';
COMMENT ON COLUMN subscription_plan.limit_ai_parse_monthly IS '每月AI解析次数限制(-1无限)';

-- 系统订阅状态表（单租户，只有一条记录）
CREATE TABLE IF NOT EXISTS subscription_status (
    id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    license_key VARCHAR(200),
    activated_at TIMESTAMP,
    expires_at TIMESTAMP,
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE subscription_status IS '系统订阅状态表';
COMMENT ON COLUMN subscription_status.status IS '状态(0-过期 1-正常 2-试用)';

-- 用量统计表（按月统计）
CREATE TABLE IF NOT EXISTS usage_stats (
    id BIGSERIAL PRIMARY KEY,
    stat_month VARCHAR(7) NOT NULL UNIQUE,
    job_count INT DEFAULT 0,
    resume_count INT DEFAULT 0,
    candidate_count INT DEFAULT 0,
    user_count INT DEFAULT 0,
    ai_parse_count INT DEFAULT 0,
    ai_match_count INT DEFAULT 0,
    ai_generate_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE usage_stats IS '用量统计表';
COMMENT ON COLUMN usage_stats.stat_month IS '统计月份(格式: 2024-01)';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化超级管理员（密码: admin123，使用 Hutool BCrypt 生成）
INSERT INTO sys_user (username, password, real_name, email, status) 
VALUES ('admin', '$2a$10$GzBgwE0YzuVKbvoCa5qf4ODH9a/X1yRMTALXWD8b0bojlOuNdUuyK', '超级管理员', 'admin@freehire.com', 1)
ON CONFLICT (username) DO NOTHING;

-- 初始化角色
INSERT INTO sys_role (role_code, role_name, sort, remark) VALUES 
('super_admin', '超级管理员', 1, '拥有所有权限'),
('hr_manager', 'HR主管', 2, '管理招聘全流程'),
('hr_staff', 'HR专员', 3, '处理简历和候选人'),
('interviewer', '面试官', 4, '仅面试评价权限'),
('employee', '普通员工', 5, '仅内推权限')
ON CONFLICT (role_code) DO NOTHING;

-- 给admin分配超级管理员角色
INSERT INTO sys_user_role (user_id, role_id) 
SELECT u.id, r.id FROM sys_user u, sys_role r 
WHERE u.username = 'admin' AND r.role_code = 'super_admin'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 初始化部门
INSERT INTO sys_dept (id, parent_id, dept_name, sort) VALUES 
(1, 0, '总公司', 1),
(2, 1, '技术部', 1),
(3, 1, '产品部', 2),
(4, 1, '人力资源部', 3),
(5, 1, '市场部', 4)
ON CONFLICT DO NOTHING;

-- 初始化菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort) VALUES 
-- 工作台
(1, 0, '工作台', 'C', '/dashboard', 'dashboard/index', '', 'DashboardOutlined', 1),
-- 招聘管理
(10, 0, '招聘管理', 'M', '/recruit', '', '', 'TeamOutlined', 2),
(11, 10, '职位管理', 'C', '/recruit/job', 'recruit/job/index', 'recruit:job:list', 'SolutionOutlined', 1),
(12, 10, '简历管理', 'C', '/recruit/resume', 'recruit/resume/index', 'recruit:resume:list', 'FileTextOutlined', 2),
(13, 10, '候选人管理', 'C', '/recruit/candidate', 'recruit/candidate/index', 'recruit:candidate:list', 'UserOutlined', 3),
(14, 10, '面试管理', 'C', '/recruit/interview', 'recruit/interview/index', 'recruit:interview:list', 'CalendarOutlined', 4),
-- 人才库
(20, 0, '人才库', 'M', '/talent', '', '', 'DatabaseOutlined', 3),
(21, 20, '人才搜索', 'C', '/talent/search', 'talent/search/index', 'talent:search', 'SearchOutlined', 1),
(22, 20, '人才列表', 'C', '/talent/list', 'talent/list/index', 'talent:list', 'UnorderedListOutlined', 2),
-- 系统管理
(30, 0, '系统管理', 'M', '/system', '', '', 'SettingOutlined', 4),
(31, 30, '用户管理', 'C', '/system/user', 'system/user/index', 'system:user:list', 'UserOutlined', 1),
(32, 30, '角色管理', 'C', '/system/role', 'system/role/index', 'system:role:list', 'SafetyOutlined', 2),
(33, 30, '部门管理', 'C', '/system/dept', 'system/dept/index', 'system:dept:list', 'ApartmentOutlined', 3),
(34, 30, 'AI配置', 'C', '/system/ai', 'system/ai/index', 'system:ai:config', 'RobotOutlined', 4),
(35, 30, '公司设置', 'C', '/system/company', 'system/company/index', 'system:company:config', 'BankOutlined', 5)
ON CONFLICT DO NOTHING;

-- 给超级管理员分配所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM sys_role r, sys_menu m 
WHERE r.role_code = 'super_admin'
ON CONFLICT (role_id, menu_id) DO NOTHING;

-- 初始化套餐
INSERT INTO subscription_plan (plan_code, plan_name, description, price, billing_cycle,
    feature_ai_parse, feature_ai_match, feature_ai_generate_jd, feature_talent_pool, feature_data_report, feature_career_site, feature_api_access,
    limit_job_count, limit_resume_count, limit_user_count, limit_ai_parse_monthly, limit_ai_match_monthly,
    sort, is_default) VALUES 
-- 免费版（开发阶段开放AI功能）
('free', '免费版', '适合小微企业或个人试用', 0, 'permanent',
    1, 1, 1, 1, 1, 0, 0,
    10, 100, 3, 50, 50,
    1, 1),
-- 基础版
('basic', '基础版', '适合中小企业日常招聘', 299, 'monthly',
    1, 0, 1, 1, 1, 0, 0,
    20, 500, 3, 100, 0,
    2, 0),
-- 专业版
('pro', '专业版', '适合中大型企业高效招聘', 799, 'monthly',
    1, 1, 1, 1, 1, 1, 0,
    100, 5000, 10, 1000, 500,
    3, 0),
-- 企业版
('enterprise', '企业版', '适合大型企业，支持私有化部署', 0, 'permanent',
    1, 1, 1, 1, 1, 1, 1,
    -1, -1, -1, -1, -1,
    4, 0)
ON CONFLICT (plan_code) DO NOTHING;

-- 初始化订阅状态（默认免费版）
INSERT INTO subscription_status (id, plan_id, status) 
SELECT 1, p.id, 1 FROM subscription_plan p WHERE p.plan_code = 'free'
ON CONFLICT DO NOTHING;

-- 初始化当月用量统计
INSERT INTO usage_stats (stat_month) 
VALUES (TO_CHAR(CURRENT_DATE, 'YYYY-MM'))
ON CONFLICT (stat_month) DO NOTHING;

-- 通知表
CREATE TABLE IF NOT EXISTS notification (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20),
    title VARCHAR(200) NOT NULL,
    content TEXT,
    biz_id BIGINT,
    biz_type VARCHAR(50),
    link VARCHAR(500),
    is_read SMALLINT DEFAULT 0,
    read_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE notification IS '通知表';
COMMENT ON COLUMN notification.type IS '通知类型(system/resume/interview/offer/application)';
COMMENT ON COLUMN notification.biz_id IS '关联业务ID';
COMMENT ON COLUMN notification.biz_type IS '关联业务类型';
COMMENT ON COLUMN notification.is_read IS '是否已读(0-未读 1-已读)';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_notification_user_id ON notification(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_is_read ON notification(is_read);
CREATE INDEX IF NOT EXISTS idx_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_user_dept_id ON sys_user(dept_id);
CREATE INDEX IF NOT EXISTS idx_candidate_phone ON candidate(phone);
CREATE INDEX IF NOT EXISTS idx_candidate_email ON candidate(email);
CREATE INDEX IF NOT EXISTS idx_resume_candidate_id ON resume(candidate_id);
CREATE INDEX IF NOT EXISTS idx_application_candidate_id ON application(candidate_id);
CREATE INDEX IF NOT EXISTS idx_application_job_id ON application(job_id);
CREATE INDEX IF NOT EXISTS idx_application_stage ON application(stage);
CREATE INDEX IF NOT EXISTS idx_job_status ON job_position(status);
CREATE INDEX IF NOT EXISTS idx_interview_application_id ON interview(application_id);

