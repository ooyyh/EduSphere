-- ======================================
-- EduSphere 在线教育平台 - 数据库初始化脚本
-- ======================================

-- 创建数据库
DROP DATABASE IF EXISTS edusphere;
CREATE DATABASE edusphere DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edusphere;

-- ======================================
-- 1. 用户相关表
-- ======================================

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `avatar` TEXT DEFAULT NULL COMMENT '头像URL',
  `role` VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT '角色：admin-管理员，teacher-教师，student-学生',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户余额表
DROP TABLE IF EXISTS `user_balance`;
CREATE TABLE `user_balance` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '余额ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '当前余额',
  `frozen_balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '冻结余额',
  `total_recharge` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计充值',
  `total_consumption` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计消费',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `fk_user_balance_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户余额表';

-- 充值记录表
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '充值金额',
  `balance_before` DECIMAL(10,2) NOT NULL COMMENT '充值前余额',
  `balance_after` DECIMAL(10,2) NOT NULL COMMENT '充值后余额',
  `recharge_type` VARCHAR(20) NOT NULL DEFAULT 'manual' COMMENT '充值类型：manual-手动充值，system-系统充值',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_recharge_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值记录表';

-- ======================================
-- 2. 课程相关表
-- ======================================

-- 分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `slug` VARCHAR(50) NOT NULL COMMENT 'URL别名',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
  `color` VARCHAR(50) DEFAULT NULL COMMENT '颜色',
  `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug` (`slug`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程分类表';

-- 课程表
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` VARCHAR(200) NOT NULL COMMENT '课程标题',
  `subtitle` VARCHAR(500) DEFAULT NULL COMMENT '课程副标题',
  `description` TEXT COMMENT '课程描述',
  `cover_image` TEXT DEFAULT NULL COMMENT '封面图片URL',
  `instructor_id` INT(11) NOT NULL COMMENT '讲师ID',
  `category_id` INT(11) DEFAULT NULL COMMENT '分类ID',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '课程价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `is_free` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否免费：1-免费，0-付费',
  `level` VARCHAR(20) DEFAULT 'beginner' COMMENT '难度：beginner-初级，intermediate-中级，advanced-高级',
  `duration` VARCHAR(50) DEFAULT NULL COMMENT '课程时长',
  `student_count` INT(11) NOT NULL DEFAULT 0 COMMENT '学生数',
  `rating` DECIMAL(3,2) DEFAULT 0.00 COMMENT '评分',
  `rating_count` INT(11) NOT NULL DEFAULT 0 COMMENT '评分人数',
  `is_hot` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否热门：1-是，0-否',
  `is_new` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否最新：1-是，0-否',
  `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态：draft-草稿，pending-待审核，published-已发布，rejected-已拒绝',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_instructor_id` (`instructor_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_is_new` (`is_new`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_course_instructor` FOREIGN KEY (`instructor_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_course_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 课程章节表
DROP TABLE IF EXISTS `course_section`;
CREATE TABLE `course_section` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` INT(11) NOT NULL COMMENT '课程ID',
  `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
  `description` TEXT COMMENT '章节描述',
  `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_section_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程章节表';

-- 课程课时表
DROP TABLE IF EXISTS `course_lesson`;
CREATE TABLE `course_lesson` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '课时ID',
  `section_id` INT(11) NOT NULL COMMENT '章节ID',
  `title` VARCHAR(200) NOT NULL COMMENT '课时标题',
  `description` TEXT COMMENT '课时描述',
  `type` VARCHAR(20) NOT NULL DEFAULT 'video' COMMENT '类型：video-视频，document-文档，quiz-测验',
  `duration` VARCHAR(50) DEFAULT NULL COMMENT '时长',
  `video_url` TEXT DEFAULT NULL COMMENT '视频URL',
  `document_url` TEXT DEFAULT NULL COMMENT '文档URL',
  `is_free` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否免费试看：1-是，0-否',
  `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_section_id` (`section_id`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_lesson_section` FOREIGN KEY (`section_id`) REFERENCES `course_section` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程课时表';

-- 课程评价表
DROP TABLE IF EXISTS `course_review`;
CREATE TABLE `course_review` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `course_id` INT(11) NOT NULL COMMENT '课程ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `rating` INT(11) NOT NULL COMMENT '评分（1-5分）',
  `content` TEXT COMMENT '评价内容',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-显示，0-隐藏',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_review_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程评价表';

-- ======================================
-- 3. 交易相关表
-- ======================================

-- 购买记录表
DROP TABLE IF EXISTS `purchase_record`;
CREATE TABLE `purchase_record` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` INT(11) NOT NULL COMMENT '购买用户ID',
  `course_id` INT(11) NOT NULL COMMENT '课程ID',
  `instructor_id` INT(11) NOT NULL COMMENT '讲师ID',
  `purchase_price` DECIMAL(10,2) NOT NULL COMMENT '购买价格',
  `balance_before` DECIMAL(10,2) NOT NULL COMMENT '购买前余额',
  `balance_after` DECIMAL(10,2) NOT NULL COMMENT '购买后余额',
  `instructor_income` DECIMAL(10,2) NOT NULL COMMENT '讲师收入',
  `platform_fee` DECIMAL(10,2) NOT NULL COMMENT '平台手续费',
  `status` VARCHAR(20) NOT NULL DEFAULT 'success' COMMENT '状态：success-成功，failed-失败，refunded-已退款',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_instructor_id` (`instructor_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_purchase_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_purchase_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_purchase_instructor` FOREIGN KEY (`instructor_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购买记录表';

-- 讲师收入表
DROP TABLE IF EXISTS `instructor_income`;
CREATE TABLE `instructor_income` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '收入ID',
  `instructor_id` INT(11) NOT NULL COMMENT '讲师ID',
  `course_id` INT(11) NOT NULL COMMENT '课程ID',
  `purchase_record_id` INT(11) NOT NULL COMMENT '购买记录ID',
  `income_amount` DECIMAL(10,2) NOT NULL COMMENT '收入金额',
  `total_income` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计收入',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_instructor_id` (`instructor_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_purchase_record_id` (`purchase_record_id`),
  CONSTRAINT `fk_income_instructor` FOREIGN KEY (`instructor_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_income_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_income_purchase` FOREIGN KEY (`purchase_record_id`) REFERENCES `purchase_record` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讲师收入表';

-- 用户课程关联表
DROP TABLE IF EXISTS `user_course`;
CREATE TABLE `user_course` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `course_id` INT(11) NOT NULL COMMENT '课程ID',
  `purchase_price` DECIMAL(10,2) NOT NULL COMMENT '购买价格',
  `purchase_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`, `course_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`),
  CONSTRAINT `fk_user_course_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_course_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户课程关联表';

-- 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `course_id` INT(11) NOT NULL COMMENT '课程ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`, `course_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 订单表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总额',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待支付，paid-已支付，cancelled-已取消，refunded-已退款',
  `payment_method` VARCHAR(20) DEFAULT NULL COMMENT '支付方式：balance-余额支付',
  `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单项表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` INT(11) NOT NULL COMMENT '订单ID',
  `course_id` INT(11) NOT NULL COMMENT '课程ID',
  `course_title` VARCHAR(200) NOT NULL COMMENT '课程标题',
  `course_price` DECIMAL(10,2) NOT NULL COMMENT '课程价格',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_course_id` (`course_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_item_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- ======================================
-- 4. 学习相关表
-- ======================================

-- 学习进度表
DROP TABLE IF EXISTS `learning_progress`;
CREATE TABLE `learning_progress` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `course_id` INT(11) NOT NULL COMMENT '课程ID',
  `lesson_id` INT(11) NOT NULL COMMENT '课时ID',
  `progress` INT(11) NOT NULL DEFAULT 0 COMMENT '进度百分比（0-100）',
  `completed` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否完成：1-是，0-否',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_lesson` (`user_id`, `lesson_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_lesson_id` (`lesson_id`),
  CONSTRAINT `fk_progress_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_progress_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_progress_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `course_lesson` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习进度表';

-- ======================================
-- 5. 初始化数据
-- ======================================

-- 插入默认管理员账户（密码：admin123）
INSERT INTO `user` (`username`, `password`, `email`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@edusphere.com', 'admin', 1);

-- 插入测试教师账户（密码：teacher123）
INSERT INTO `user` (`username`, `password`, `email`, `role`, `status`) VALUES
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'teacher1@edusphere.com', 'teacher', 1),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'teacher2@edusphere.com', 'teacher', 1);

-- 插入测试学生账户（密码：student123）
INSERT INTO `user` (`username`, `password`, `email`, `role`, `status`) VALUES
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'student1@edusphere.com', 'student', 1),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'student2@edusphere.com', 'student', 1);

-- 为所有用户初始化余额
INSERT INTO `user_balance` (`user_id`, `balance`) VALUES
(1, 0.00),
(2, 0.00),
(3, 0.00),
(4, 1000.00),
(5, 1000.00);

-- 插入课程分类
INSERT INTO `category` (`name`, `slug`, `description`, `icon`, `color`, `sort_order`) VALUES
('编程开发', 'programming', '学习各种编程语言和开发技术', 'el-icon-cpu', '#409EFF', 1),
('前端开发', 'frontend', '前端技术栈：HTML、CSS、JavaScript、Vue、React等', 'el-icon-monitor', '#67C23A', 2),
('后端开发', 'backend', '后端技术栈：Java、Python、Node.js等', 'el-icon-box', '#E6A23C', 3),
('移动开发', 'mobile', 'iOS、Android等移动应用开发', 'el-icon-mobile', '#F56C6C', 4),
('数据库', 'database', 'MySQL、MongoDB、Redis等数据库技术', 'el-icon-coin', '#909399', 5),
('云计算', 'cloud', 'AWS、阿里云、Docker、Kubernetes等', 'el-icon-cloudy', '#00D8FF', 6),
('人工智能', 'ai', '机器学习、深度学习、自然语言处理等', 'el-icon-lightning', '#9C27B0', 7),
('设计创意', 'design', 'UI/UX设计、平面设计、视频剪辑等', 'el-icon-brush', '#FF9800', 8);

-- 插入示例课程
INSERT INTO `course` (`title`, `subtitle`, `description`, `cover_image`, `instructor_id`, `category_id`, `price`, `original_price`, `is_free`, `level`, `duration`, `student_count`, `rating`, `rating_count`, `is_hot`, `is_new`, `status`) VALUES
('Vue 3 从入门到精通', '全面掌握Vue 3核心技术', 'Vue 3是一个用于构建用户界面的渐进式JavaScript框架。本课程将带你从零开始，全面学习Vue 3的核心概念和实战技巧。', 'https://picsum.photos/seed/vue3/400/300', 2, 2, 199.00, 299.00, 0, 'beginner', '20小时', 1250, 4.8, 320, 1, 1, 'published'),
('Java Spring Boot 实战', '企业级Java Web开发', '使用Spring Boot快速构建企业级Java应用。学习Spring MVC、Spring Data JPA、Spring Security等核心技术。', 'https://picsum.photos/seed/springboot/400/300', 2, 3, 299.00, 399.00, 0, 'intermediate', '30小时', 980, 4.9, 256, 1, 0, 'published'),
('Python 数据分析入门', '用Python分析数据', '学习使用Python进行数据分析，掌握Pandas、NumPy、Matplotlib等数据分析必备工具。', 'https://picsum.photos/seed/python/400/300', 3, 1, 149.00, 199.00, 0, 'beginner', '15小时', 760, 4.7, 189, 0, 1, 'published'),
('React 实战开发', '现代前端框架React', 'React是用于构建用户界面的JavaScript库。学习React Hooks、Redux状态管理、React Router等核心技术。', 'https://picsum.photos/seed/react/400/300', 2, 2, 249.00, 349.00, 0, 'intermediate', '25小时', 650, 4.8, 178, 1, 0, 'published'),
('MySQL 数据库从入门到精通', '全面掌握MySQL', '学习MySQL数据库的安装、配置、SQL语法、索引优化、事务处理等核心知识。', 'https://picsum.photos/seed/mysql/400/300', 3, 5, 129.00, 179.00, 0, 'beginner', '18小时', 890, 4.6, 234, 0, 0, 'published'),
('Docker 容器化技术', '云原生时代必备技能', '学习Docker容器技术，掌握镜像构建、容器编排、Docker Compose等实用技能。', 'https://picsum.photos/seed/docker/400/300', 2, 6, 199.00, 249.00, 0, 'intermediate', '16小时', 520, 4.7, 145, 0, 1, 'published');

-- ======================================
-- 6. 创建视图（可选）
-- ======================================

-- 课程详情视图（包含讲师和分类信息）
CREATE OR REPLACE VIEW `v_course_detail` AS
SELECT
  c.id,
  c.title,
  c.subtitle,
  c.description,
  c.cover_image,
  c.price,
  c.original_price,
  c.is_free,
  c.level,
  c.duration,
  c.student_count,
  c.rating,
  c.rating_count,
  c.is_hot,
  c.is_new,
  c.status,
  c.created_at,
  c.updated_at,
  u.id AS instructor_id,
  u.username AS instructor_name,
  u.avatar AS instructor_avatar,
  cat.id AS category_id,
  cat.name AS category_name,
  cat.slug AS category_slug
FROM course c
LEFT JOIN user u ON c.instructor_id = u.id
LEFT JOIN category cat ON c.category_id = cat.id;

-- ======================================
-- 7. 完成
-- ======================================

-- 查看表结构
SELECT '数据库初始化完成！' AS message;
SELECT CONCAT('共创建了 ', COUNT(*), ' 个表') AS summary FROM information_schema.tables WHERE table_schema = 'edusphere';
