-- EduSphere 数据库Schema修复脚本
-- 此脚本用于修复database_schema.sql中缺失的表定义

USE edusphere;

-- 购物车表
CREATE TABLE IF NOT EXISTS cart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    course_id INT NOT NULL COMMENT '课程ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_course_cart (user_id, course_id) COMMENT '同一用户不能重复添加同一课程'
) COMMENT '购物车表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id INT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    status ENUM('pending', 'paid', 'cancelled', 'refunded') DEFAULT 'pending' COMMENT '订单状态：pending-待支付，paid-已支付，cancelled-已取消，refunded-已退款',
    payment_method VARCHAR(50) COMMENT '支付方式',
    payment_time TIMESTAMP NULL COMMENT '支付时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT '订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS order_item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL COMMENT '订单ID',
    course_id INT NOT NULL COMMENT '课程ID',
    course_title VARCHAR(200) NOT NULL COMMENT '课程标题（冗余字段，防止课程删除后订单无法显示）',
    course_price DECIMAL(10,2) NOT NULL COMMENT '购买时的课程价格',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id)
) COMMENT '订单项表';

-- 删除重复的learning_progress表定义（如果存在）
-- 注意：由于database_schema.sql中learning_progress定义了两次，我们保留第二个版本（更简洁）

-- 课程收藏表（新功能）
CREATE TABLE IF NOT EXISTS course_favorite (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    course_id INT NOT NULL COMMENT '课程ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_course_favorite (user_id, course_id) COMMENT '同一用户不能重复收藏同一课程'
) COMMENT '课程收藏表';

-- 学习笔记表（新功能）
CREATE TABLE IF NOT EXISTS learning_note (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    course_id INT NOT NULL COMMENT '课程ID',
    lesson_id INT COMMENT '课时ID',
    content TEXT NOT NULL COMMENT '笔记内容',
    video_time INT COMMENT '视频时间点（秒）',
    is_public TINYINT DEFAULT 0 COMMENT '是否公开：1-公开，0-私有',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    FOREIGN KEY (lesson_id) REFERENCES course_lesson(id) ON DELETE CASCADE
) COMMENT '学习笔记表';

-- 课程讨论表（新功能）
CREATE TABLE IF NOT EXISTS course_discussion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT '课程ID',
    lesson_id INT COMMENT '课时ID（如果是针对某个课时的讨论）',
    user_id INT NOT NULL COMMENT '用户ID',
    parent_id INT COMMENT '父评论ID（用于回复）',
    content TEXT NOT NULL COMMENT '讨论内容',
    likes INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态：1-显示，0-隐藏',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    FOREIGN KEY (lesson_id) REFERENCES course_lesson(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (parent_id) REFERENCES course_discussion(id) ON DELETE CASCADE
) COMMENT '课程讨论表';

-- 优惠券表（新功能）
CREATE TABLE IF NOT EXISTS coupon (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '优惠券代码',
    name VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    description TEXT COMMENT '优惠券描述',
    discount_type ENUM('percentage', 'fixed') NOT NULL COMMENT '折扣类型：percentage-百分比，fixed-固定金额',
    discount_value DECIMAL(10,2) NOT NULL COMMENT '折扣值',
    min_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低消费金额',
    max_discount DECIMAL(10,2) COMMENT '最大折扣金额（仅用于百分比类型）',
    total_quantity INT NOT NULL COMMENT '总数量',
    used_quantity INT DEFAULT 0 COMMENT '已使用数量',
    valid_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '有效期开始时间',
    valid_to TIMESTAMP NOT NULL COMMENT '有效期结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '优惠券表';

-- 用户优惠券表（新功能）
CREATE TABLE IF NOT EXISTS user_coupon (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    coupon_id INT NOT NULL COMMENT '优惠券ID',
    status ENUM('unused', 'used', 'expired') DEFAULT 'unused' COMMENT '状态：unused-未使用，used-已使用，expired-已过期',
    used_at TIMESTAMP NULL COMMENT '使用时间',
    order_id INT COMMENT '关联订单ID',
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (coupon_id) REFERENCES coupon(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id)
) COMMENT '用户优惠券表';

-- 学习证书表（新功能）
CREATE TABLE IF NOT EXISTS learning_certificate (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    course_id INT NOT NULL COMMENT '课程ID',
    certificate_no VARCHAR(50) NOT NULL UNIQUE COMMENT '证书编号',
    issue_date DATE NOT NULL COMMENT '颁发日期',
    certificate_url TEXT COMMENT '证书图片URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-已撤销',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    UNIQUE KEY uk_user_course_cert (user_id, course_id) COMMENT '同一用户同一课程只能有一个证书'
) COMMENT '学习证书表';

-- Quiz题库表（新功能）
CREATE TABLE IF NOT EXISTS quiz_question (
    id INT PRIMARY KEY AUTO_INCREMENT,
    lesson_id INT NOT NULL COMMENT '课时ID',
    question_type ENUM('single_choice', 'multiple_choice', 'true_false', 'short_answer') NOT NULL COMMENT '题目类型',
    question_text TEXT NOT NULL COMMENT '题目内容',
    options TEXT COMMENT '选项（JSON格式）',
    correct_answer TEXT NOT NULL COMMENT '正确答案',
    explanation TEXT COMMENT '答案解析',
    score INT DEFAULT 10 COMMENT '分值',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (lesson_id) REFERENCES course_lesson(id) ON DELETE CASCADE
) COMMENT 'Quiz题库表';

-- Quiz提交记录表（新功能）
CREATE TABLE IF NOT EXISTS quiz_submission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    lesson_id INT NOT NULL COMMENT '课时ID',
    answers TEXT COMMENT '用户答案（JSON格式）',
    score DECIMAL(5,2) COMMENT '得分',
    total_score DECIMAL(5,2) COMMENT '总分',
    pass_status TINYINT COMMENT '是否通过：1-通过，0-未通过',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (lesson_id) REFERENCES course_lesson(id) ON DELETE CASCADE
) COMMENT 'Quiz提交记录表';

-- 课程推荐记录表（新功能）
CREATE TABLE IF NOT EXISTS course_recommendation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    course_id INT NOT NULL COMMENT '推荐的课程ID',
    recommendation_reason VARCHAR(200) COMMENT '推荐理由',
    score DECIMAL(5,2) COMMENT '推荐分数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) COMMENT '课程推荐记录表';

-- 用户浏览记录表（新功能，用于推荐系统）
CREATE TABLE IF NOT EXISTS user_browse_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '用户ID',
    course_id INT NOT NULL COMMENT '浏览的课程ID',
    browse_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    duration INT DEFAULT 0 COMMENT '浏览时长（秒）',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    INDEX idx_user_time (user_id, browse_time)
) COMMENT '用户浏览记录表';

-- 课程标签表（新功能，用于推荐系统）
CREATE TABLE IF NOT EXISTS course_tag (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    description TEXT COMMENT '标签描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '课程标签表';

-- 课程-标签关联表（新功能）
CREATE TABLE IF NOT EXISTS course_tag_relation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT '课程ID',
    tag_id INT NOT NULL COMMENT '标签ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES course_tag(id) ON DELETE CASCADE,
    UNIQUE KEY uk_course_tag (course_id, tag_id)
) COMMENT '课程-标签关联表';

-- 添加索引以优化查询性能
CREATE INDEX idx_cart_user ON cart(user_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_favorite_user ON course_favorite(user_id);
CREATE INDEX idx_note_user_course ON learning_note(user_id, course_id);
CREATE INDEX idx_discussion_course ON course_discussion(course_id);
CREATE INDEX idx_discussion_lesson ON course_discussion(lesson_id);
CREATE INDEX idx_user_coupon_user ON user_coupon(user_id);
CREATE INDEX idx_user_coupon_status ON user_coupon(status);
CREATE INDEX idx_certificate_user ON learning_certificate(user_id);
CREATE INDEX idx_quiz_question_lesson ON quiz_question(lesson_id);
CREATE INDEX idx_quiz_submission_user ON quiz_submission(user_id);
