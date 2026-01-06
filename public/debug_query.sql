-- 调试查询：检查课程数据
USE edusphere;

-- 查看所有课程
SELECT id, title, status, created_at FROM course ORDER BY created_at DESC;

-- 查看课程总数
SELECT COUNT(*) as total_courses FROM course WHERE status = 1;

-- 查看分页查询（第1页，每页12条）
SELECT id, title, status, created_at 
FROM course 
WHERE status = 1 
ORDER BY created_at DESC 
LIMIT 0, 12;

-- 查看是否有重复ID
SELECT id, COUNT(*) as count 
FROM course 
GROUP BY id 
HAVING COUNT(*) > 1;
