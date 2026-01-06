-- 更新课程状态字段，从TINYINT改为ENUM
-- 支持四个状态：草稿、审核中、已发布、已拒绝

-- 1. 修改course表的status字段类型
ALTER TABLE course MODIFY COLUMN status ENUM('draft', 'pending', 'published', 'rejected') DEFAULT 'draft' COMMENT '状态：draft-草稿，pending-审核中，published-已发布，rejected-已拒绝';

-- 2. 更新现有数据的状态值
-- 将原来的 0-草稿 改为 'draft'
UPDATE course SET status = 'draft' WHERE status = '0';

-- 将原来的 1-发布 改为 'published'
UPDATE course SET status = 'published' WHERE status = '1';

-- 3. 验证修改结果
SELECT id, title, status, created_at FROM course ORDER BY id;

-- 4. 显示表结构确认
DESCRIBE course;
