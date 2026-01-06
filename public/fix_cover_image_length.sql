-- 修复 cover_image 字段长度问题
-- 将 VARCHAR(255) 改为 TEXT 以支持长图片URL

-- 修改 course 表的 cover_image 字段
ALTER TABLE course MODIFY COLUMN cover_image TEXT COMMENT '课程封面';

-- 同时修改 user 表的 avatar 字段（可能也有同样问题）
ALTER TABLE user MODIFY COLUMN avatar TEXT COMMENT '用户头像';

-- 验证修改结果
DESCRIBE course;
DESCRIBE user;
