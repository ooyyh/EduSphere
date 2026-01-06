-- EduSphere 测试数据
USE edusphere;

-- 插入测试用户
INSERT INTO user (username, password, email, avatar, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjT3x4WzI9vJzK8vJzK8vJ', 'admin@edusphere.com', 'https://via.placeholder.com/100', 'admin', 1),
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjT3x4WzI9vJzK8vJzK8vJ', 'teacher1@edusphere.com', 'https://via.placeholder.com/100', 'teacher', 1),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjT3x4WzI9vJzK8vJzK8vJ', 'teacher2@edusphere.com', 'https://via.placeholder.com/100', 'teacher', 1),
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjT3x4WzI9vJzK8vJzK8vJ', 'student1@edusphere.com', 'https://via.placeholder.com/100', 'student', 1);

-- 插入测试分类
INSERT INTO category (name, slug, description, icon, color, sort_order, status) VALUES
('编程开发', 'programming', '编程语言、框架、工具等开发相关课程', 'code', '#1890ff', 1, 1),
('设计创意', 'design', 'UI/UX设计、平面设计、创意设计等课程', 'design', '#52c41a', 2, 1),
('商业管理', 'business', '商业分析、项目管理、创业等课程', 'business', '#fa8c16', 3, 1),
('数据科学', 'data-science', '数据分析、机器学习、人工智能等课程', 'data', '#722ed1', 4, 1);

-- 插入测试课程
INSERT INTO course (title, subtitle, description, cover_image, instructor_id, category_id, price, original_price, is_free, level, duration, student_count, rating, rating_count, is_hot, is_new, status) VALUES
('Spring Boot 微服务架构', '从零开始构建微服务系统', '涵盖服务注册、配置管理、负载均衡等微服务架构核心内容。学习Spring Cloud全家桶，掌握微服务开发最佳实践。', 'https://picsum.photos/400/200?random=1', 2, 1, 399.00, 599.00, 0, 'advanced', '45小时', 1250, 4.8, 89, 1, 1, 1),
('UI/UX 设计实战课程', '现代界面设计完整指南', '学习设计思维、原型制作、用户研究等专业技能。从基础理论到实际项目，全面提升设计能力。', 'https://picsum.photos/400/200?random=2', 2, 2, 199.00, 299.00, 0, 'beginner', '28小时', 890, 4.6, 67, 0, 1, 1),
('Python 数据分析入门', '数据科学基础课程', '学习pandas、numpy、matplotlib等核心库的使用方法。掌握数据清洗、分析和可视化的完整流程。', 'https://picsum.photos/400/200?random=3', 2, 4, 0.00, NULL, 1, 'beginner', '24小时', 2100, 4.7, 156, 1, 0, 1),
('Vue.js 前端开发实战', '现代前端框架深度解析', '深入学习Vue.js 3.x版本，掌握组合式API、响应式系统、组件化开发等核心概念。', 'https://picsum.photos/400/200?random=4', 2, 1, 299.00, 399.00, 0, 'intermediate', '32小时', 1680, 4.9, 124, 1, 0, 1),
('React 全栈开发', '从零到一构建完整应用', '学习React、Node.js、MongoDB等技术栈，开发完整的全栈Web应用。', 'https://picsum.photos/400/200?random=5', 2, 1, 499.00, 699.00, 0, 'advanced', '56小时', 980, 4.5, 78, 0, 1, 1),
('Figma 设计工具精通', '专业UI设计工具使用指南', '从基础操作到高级技巧，全面掌握Figma设计工具的使用方法。', 'https://picsum.photos/400/200?random=6', 2, 2, 199.00, NULL, 0, 'beginner', '20小时', 1450, 4.8, 92, 1, 0, 1);
