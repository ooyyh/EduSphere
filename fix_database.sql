-- ======================================
-- 数据库字段修复脚本
-- ======================================

USE edusphere;

-- 修改 avatar 字段长度，支持更长的URL或base64数据
ALTER TABLE `user` MODIFY COLUMN `avatar` TEXT COMMENT '头像URL或base64数据';

-- 修改课程封面字段长度
ALTER TABLE `course` MODIFY COLUMN `cover_image` TEXT COMMENT '封面图片URL';

-- 修改课程课时的视频和文档URL字段
ALTER TABLE `course_lesson` MODIFY COLUMN `video_url` TEXT COMMENT '视频URL';
ALTER TABLE `course_lesson` MODIFY COLUMN `document_url` TEXT COMMENT '文档URL';

-- 查看修改结果
SHOW FULL COLUMNS FROM `user` WHERE Field = 'avatar';
SHOW FULL COLUMNS FROM `course` WHERE Field = 'cover_image';
SHOW FULL COLUMNS FROM `course_lesson` WHERE Field IN ('video_url', 'document_url');

SELECT '数据库字段修复完成！' AS message;
