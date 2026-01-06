-- 详细调试查询
USE edusphere;

-- 1. 查看所有课程记录
SELECT 
    id, 
    title, 
    status, 
    created_at,
    instructor_id,
    category_id
FROM course 
ORDER BY id;

-- 2. 查看课程总数（按状态）
SELECT 
    status,
    COUNT(*) as count
FROM course 
GROUP BY status;

-- 3. 查看有效的课程总数
SELECT COUNT(*) as valid_courses 
FROM course 
WHERE status = 1;

-- 4. 查看分页查询的详细结果
SELECT 
    c.id,
    c.title,
    c.status,
    c.created_at,
    u.username as instructor_name,
    cat.name as category_name
FROM course c
LEFT JOIN user u ON c.instructor_id = u.id
LEFT JOIN category cat ON c.category_id = cat.id
WHERE c.status = 1
ORDER BY c.created_at DESC
LIMIT 0, 12;

-- 5. 查看是否有数据重复
SELECT 
    id,
    title,
    COUNT(*) as duplicate_count
FROM course
GROUP BY id, title
HAVING COUNT(*) > 1;

-- 6. 查看用户表数据
SELECT id, username, role FROM user;

-- 7. 查看分类表数据
SELECT id, name, slug FROM category;
