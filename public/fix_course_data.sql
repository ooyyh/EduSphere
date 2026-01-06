-- 修复课程数据问题
USE edusphere;

-- 1. 删除所有现有课程数据
DELETE FROM course;

-- 2. 重置AUTO_INCREMENT
ALTER TABLE course AUTO_INCREMENT = 1;

-- 3. 重新插入测试数据
INSERT INTO course (title, subtitle, description, cover_image, instructor_id, category_id, price, original_price, is_free, level, duration, student_count, rating, rating_count, is_hot, is_new, status) VALUES
('Spring Boot 微服务架构', '从零开始构建微服务系统', '涵盖服务注册、配置管理、负载均衡等微服务架构核心内容。学习Spring Cloud全家桶，掌握微服务开发最佳实践。', 'https://picsum.photos/400/200?random=1', 2, 1, 399.00, 599.00, 0, 'advanced', '45小时', 1250, 4.8, 89, 1, 1, 1),
('UI/UX 设计实战课程', '现代界面设计完整指南', '学习设计思维、原型制作、用户研究等专业技能。从基础理论到实际项目，全面提升设计能力。', 'https://picsum.photos/400/200?random=2', 2, 2, 199.00, 299.00, 0, 'beginner', '28小时', 890, 4.6, 67, 0, 1, 1),
('Python 数据分析入门', '数据科学基础课程', '学习pandas、numpy、matplotlib等核心库的使用方法。掌握数据清洗、分析和可视化的完整流程。', 'https://picsum.photos/400/200?random=3', 2, 4, 0.00, NULL, 1, 'beginner', '24小时', 2100, 4.7, 156, 1, 0, 1),
('Vue.js 前端开发实战', '现代前端框架深度解析', '深入学习Vue.js 3.x版本，掌握组合式API、响应式系统、组件化开发等核心概念。', 'https://picsum.photos/400/200?random=4', 2, 1, 299.00, 399.00, 0, 'intermediate', '32小时', 1680, 4.9, 124, 1, 0, 1),
('React 全栈开发', '从零到一构建完整应用', '学习React、Node.js、MongoDB等技术栈，开发完整的全栈Web应用。', 'https://picsum.photos/400/200?random=5', 2, 1, 499.00, 699.00, 0, 'advanced', '56小时', 980, 4.5, 78, 0, 1, 1),
('Figma 设计工具精通', '专业UI设计工具使用指南', '从基础操作到高级技巧，全面掌握Figma设计工具的使用方法。', 'https://picsum.photos/400/200?random=6', 2, 2, 199.00, NULL, 0, 'beginner', '20小时', 1450, 4.8, 92, 1, 0, 1);

-- 4. 验证数据
SELECT 
    id,
    title,
    status,
    created_at
FROM course 
ORDER BY id;

-- 5. 验证总数
SELECT COUNT(*) as total_courses FROM course WHERE status = 1;
