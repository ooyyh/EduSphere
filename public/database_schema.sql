-- EduSphere 数据库表结构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS edusphere DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edusphere;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    avatar TEXT COMMENT '头像URL',
    role ENUM('admin', 'teacher', 'student') DEFAULT 'student' COMMENT '角色：admin-管理员，teacher-教师，student-学生',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- 课程分类表
CREATE TABLE IF NOT EXISTS category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    slug VARCHAR(50) NOT NULL UNIQUE COMMENT '分类标识',
    description TEXT COMMENT '分类描述',
    icon VARCHAR(100) COMMENT '分类图标',
    color VARCHAR(20) COMMENT '分类颜色',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '课程分类表';

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '课程标题',
    subtitle VARCHAR(300) COMMENT '课程副标题',
    description TEXT COMMENT '课程描述',
    cover_image TEXT COMMENT '课程封面',
    instructor_id INT NOT NULL COMMENT '讲师ID',
    category_id INT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '课程价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    is_free TINYINT DEFAULT 0 COMMENT '是否免费：1-免费，0-付费',
    level ENUM('beginner', 'intermediate', 'advanced') DEFAULT 'beginner' COMMENT '难度等级',
    duration VARCHAR(50) COMMENT '课程时长',
    student_count INT DEFAULT 0 COMMENT '学员数量',
    rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '评分',
    rating_count INT DEFAULT 0 COMMENT '评分人数',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门：1-是，0-否',
    is_new TINYINT DEFAULT 0 COMMENT '是否新课程：1-是，0-否',
    status ENUM('draft', 'pending', 'published', 'rejected') DEFAULT 'draft' COMMENT '状态：draft-草稿，pending-审核中，published-已发布，rejected-已拒绝',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES user(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
) COMMENT '课程表';

-- 课程章节表
CREATE TABLE IF NOT EXISTS course_section (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '章节标题',
    description TEXT COMMENT '章节描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) COMMENT '课程章节表';

-- 课程课时表
CREATE TABLE IF NOT EXISTS course_lesson (
    id INT PRIMARY KEY AUTO_INCREMENT,
    section_id INT NOT NULL COMMENT '章节ID',
    title VARCHAR(200) NOT NULL COMMENT '课时标题',
    description TEXT COMMENT '课时描述',
    type ENUM('video', 'document', 'quiz') DEFAULT 'video' COMMENT '课时类型',
    duration VARCHAR(20) COMMENT '课时时长',
    video_url VARCHAR(500) COMMENT '视频URL',
    document_url VARCHAR(500) COMMENT '文档URL',
    is_free TINYINT DEFAULT 0 COMMENT '是否免费：1-免费，0-付费',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (section_id) REFERENCES course_section(id) ON DELETE CASCADE
) COMMENT '课程课时表';

-- 用户课程关系表（用户购买的课程）
CREATE TABLE IF NOT EXISTS user_course (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    course_id INT NOT NULL COMMENT '课程ID',
    purchase_price DECIMAL(10,2) COMMENT '购买价格',
    purchase_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    UNIQUE KEY uk_user_course (user_id, course_id)
) COMMENT '用户课程关系表';

-- 课程评价表
CREATE TABLE IF NOT EXISTS course_review (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT '课程ID',
    user_id INT NOT NULL COMMENT '用户ID',
    rating TINYINT NOT NULL COMMENT '评分：1-5星',
    content TEXT COMMENT '评价内容',
    status TINYINT DEFAULT 1 COMMENT '状态：1-显示，0-隐藏',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    UNIQUE KEY uk_course_user_review (course_id, user_id)
) COMMENT '课程评价表';

-- 用户余额表
CREATE TABLE IF NOT EXISTS user_balance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '用户余额',
    frozen_balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '冻结余额',
    total_recharge DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计充值',
    total_consumption DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计消费',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    UNIQUE KEY uk_user_balance (user_id)
) COMMENT '用户余额表';

-- 充值记录表
CREATE TABLE IF NOT EXISTS recharge_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '充值金额',
    balance_before DECIMAL(10,2) NOT NULL COMMENT '充值前余额',
    balance_after DECIMAL(10,2) NOT NULL COMMENT '充值后余额',
    recharge_type ENUM('manual', 'system') DEFAULT 'manual' COMMENT '充值类型：manual-手动充值，system-系统充值',
    remark VARCHAR(200) COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT '充值记录表';

-- 购买记录表
CREATE TABLE IF NOT EXISTS purchase_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '购买用户ID',
    course_id INT NOT NULL COMMENT '课程ID',
    instructor_id INT NOT NULL COMMENT '讲师ID',
    purchase_price DECIMAL(10,2) NOT NULL COMMENT '购买价格',
    balance_before DECIMAL(10,2) NOT NULL COMMENT '购买前余额',
    balance_after DECIMAL(10,2) NOT NULL COMMENT '购买后余额',
    instructor_income DECIMAL(10,2) NOT NULL COMMENT '讲师收入',
    platform_fee DECIMAL(10,2) NOT NULL COMMENT '平台手续费',
    status ENUM('success', 'failed', 'refunded') DEFAULT 'success' COMMENT '购买状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (instructor_id) REFERENCES user(id),
    UNIQUE KEY uk_user_course_purchase (user_id, course_id)
) COMMENT '购买记录表';

-- 讲师收入记录表
CREATE TABLE IF NOT EXISTS instructor_income (
    id INT PRIMARY KEY AUTO_INCREMENT,
    instructor_id INT NOT NULL COMMENT '讲师ID',
    course_id INT NOT NULL COMMENT '课程ID',
    purchase_record_id INT NOT NULL COMMENT '购买记录ID',
    income_amount DECIMAL(10,2) NOT NULL COMMENT '收入金额',
    total_income DECIMAL(10,2) NOT NULL COMMENT '累计收入',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (purchase_record_id) REFERENCES purchase_record(id)
) COMMENT '讲师收入记录表';

-- 学习进度表
CREATE TABLE IF NOT EXISTS learning_progress (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    course_id INT NOT NULL COMMENT '课程ID',
    lesson_id INT NOT NULL COMMENT '课时ID',
    progress INT DEFAULT 0 COMMENT '学习进度百分比(0-100)',
    completed BOOLEAN DEFAULT FALSE COMMENT '是否完成',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (lesson_id) REFERENCES course_lesson(id),
    UNIQUE KEY uk_user_lesson (user_id, lesson_id)
) COMMENT '学习进度表';

-- 插入初始数据
-- 插入分类数据
INSERT INTO category (name, slug, description, icon, color, sort_order) VALUES
('编程开发', 'programming', '学习各种编程语言和开发技术', 'Monitor', '#409EFF', 1),
('设计创意', 'design', 'UI/UX设计、平面设计等创意课程', 'Brush', '#67C23A', 2),
('商业管理', 'business', '商业分析、项目管理等管理课程', 'TrendCharts', '#E6A23C', 3),
('语言学习', 'language', '英语、日语等语言学习课程', 'ChatDotRound', '#F56C6C', 4),
('数据科学', 'data-science', '数据分析、机器学习等数据科学课程', 'DataAnalysis', '#9C27B0', 5),
('人工智能', 'ai', '人工智能、深度学习等AI课程', 'Cpu', '#FF9800', 6);

-- 插入测试用户数据
INSERT INTO user (username, password, email, role) VALUES
('admin', 'admin123', 'admin@edusphere.com', 'admin'),
('teacher1', 'teacher123', 'teacher1@edusphere.com', 'teacher'),
('teacher2', 'teacher123', 'teacher2@edusphere.com', 'teacher'),
('student1', 'student123', 'student1@edusphere.com', 'student'),
('student2', 'student123', 'student2@edusphere.com', 'student');

-- 为用户创建初始余额记录
INSERT INTO user_balance (user_id, balance, total_recharge) VALUES
(1, 1000.00, 1000.00),  -- admin
(2, 500.00, 500.00),    -- teacher1
(3, 500.00, 500.00),    -- teacher2
(4, 200.00, 200.00),    -- student1
(5, 200.00, 200.00);    -- student2

-- 插入测试课程数据
INSERT INTO course (title, subtitle, description, instructor_id, category_id, price, original_price, level, duration, is_hot, is_new) VALUES
('Vue.js 3.0 完整开发教程', '从零开始学习Vue.js 3.0，掌握现代前端开发技能', '本课程将带你从零开始学习Vue.js 3.0框架，深入理解组合式API、响应式系统、组件化开发等核心概念。', 2, 1, 299.00, 399.00, 'intermediate', '32小时', 1, 0),
('Spring Boot 微服务架构', '深入学习Spring Boot框架，构建企业级微服务应用', '涵盖服务注册、配置管理、负载均衡等微服务架构核心内容。', 2, 1, 399.00, NULL, 'advanced', '45小时', 1, 1),
('UI/UX 设计实战课程', '学习用户界面和用户体验设计，掌握设计思维', '学习设计思维、原型制作、用户研究等专业技能。', 2, 2, 199.00, 299.00, 'beginner', '28小时', 0, 0),
('Python 数据分析入门', '使用Python进行数据分析，学习核心库的使用方法', '学习pandas、numpy、matplotlib等核心库的使用方法。', 2, 5, 0.00, NULL, 'beginner', '24小时', 0, 1);

-- 插入课程章节数据
INSERT INTO course_section (course_id, title, description, sort_order) VALUES
(1, 'Vue.js 3.0 入门基础', 'Vue.js 3.0基础知识和环境搭建', 1),
(1, '组合式API深入学习', '深入学习Vue.js 3.0的组合式API', 2),
(2, 'Spring Boot 基础', 'Spring Boot框架基础知识和配置', 1),
(2, '微服务架构设计', '微服务架构的设计原则和最佳实践', 2);

-- 插入课程课时数据
INSERT INTO course_lesson (section_id, title, type, duration, is_free, sort_order) VALUES
(1, 'Vue.js 简介和环境搭建', 'video', '15分钟', 1, 1),
(1, '第一个Vue应用', 'video', '20分钟', 1, 2),
(1, '模板语法详解', 'video', '25分钟', 0, 3),
(2, 'setup函数详解', 'video', '30分钟', 0, 1),
(2, 'ref和reactive的使用', 'video', '35分钟', 0, 2),
(3, 'Spring Boot 项目创建', 'video', '20分钟', 1, 1),
(3, '配置文件详解', 'video', '25分钟', 0, 2),
(4, '服务注册与发现', 'video', '40分钟', 0, 1),
(4, '配置中心管理', 'video', '35分钟', 0, 2);
