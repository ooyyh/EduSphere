-- 课程购买功能数据库迁移脚本
-- 用户名: root, 密码: root

USE edusphere;

-- 创建用户余额表
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

-- 创建充值记录表
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

-- 创建购买记录表
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

-- 创建讲师收入记录表
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

-- 为现有用户创建余额记录
INSERT IGNORE INTO user_balance (user_id, balance, total_recharge) VALUES
(1, 1000.00, 1000.00),  -- admin
(2, 500.00, 500.00),    -- teacher1
(3, 500.00, 500.00),    -- teacher2
(4, 200.00, 200.00),    -- student1
(5, 200.00, 200.00);    -- student2

-- 创建充值记录
INSERT INTO recharge_record (user_id, amount, balance_before, balance_after, recharge_type, remark) VALUES
(1, 1000.00, 0.00, 1000.00, 'system', '系统初始充值'),
(2, 500.00, 0.00, 500.00, 'system', '系统初始充值'),
(3, 500.00, 0.00, 500.00, 'system', '系统初始充值'),
(4, 200.00, 0.00, 200.00, 'system', '系统初始充值'),
(5, 200.00, 0.00, 200.00, 'system', '系统初始充值');

-- 删除旧的购物车和订单表（如果存在）
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart;

-- 更新user_course表，添加购买价格字段
ALTER TABLE user_course ADD COLUMN IF NOT EXISTS purchase_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '购买价格';
ALTER TABLE user_course ADD COLUMN IF NOT EXISTS purchase_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间';

-- 添加索引优化查询性能
CREATE INDEX idx_user_balance_user_id ON user_balance(user_id);
CREATE INDEX idx_recharge_record_user_id ON recharge_record(user_id);
CREATE INDEX idx_recharge_record_created_at ON recharge_record(created_at);
CREATE INDEX idx_purchase_record_user_id ON purchase_record(user_id);
CREATE INDEX idx_purchase_record_course_id ON purchase_record(course_id);
CREATE INDEX idx_purchase_record_instructor_id ON purchase_record(instructor_id);
CREATE INDEX idx_instructor_income_instructor_id ON instructor_income(instructor_id);

-- 显示创建结果
SELECT 'Database migration completed successfully!' as message;
SELECT COUNT(*) as user_balance_count FROM user_balance;
SELECT COUNT(*) as recharge_record_count FROM recharge_record;
