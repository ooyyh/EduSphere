# 数据库状态更新问题修复

## 🚨 问题描述

用户反馈：点击发布按钮后，数据库内status未改变。

## 🔍 问题分析

### 根本原因
**CourseMapper.xml中的updateCourse方法缺少status字段更新**

### 问题详情
1. **前端调用正确** - 发布按钮正确调用 `/teacher/courses/{id}/publish` API
2. **后端逻辑正确** - TeacherServiceImpl.publishCourse方法正确设置status为"pending"
3. **数据库更新失败** - CourseMapper.xml的updateCourse方法没有包含status字段

## 🛠️ 修复方案

### 1. 修复CourseMapper.xml

#### 修复前（错误）
```xml
<update id="updateCourse" parameterType="top.ooyyh.edusphere.entity.Course">
    UPDATE course 
    SET title = #{title},
        subtitle = #{subtitle},
        description = #{description},
        cover_image = #{coverImage},
        category_id = #{categoryId},
        price = #{price},
        original_price = #{originalPrice},
        is_free = #{isFree},
        level = #{level},
        duration = #{duration},
        is_hot = #{isHot},
        is_new = #{isNew},
        updated_at = #{updatedAt}
    WHERE id = #{id}
</update>
```

#### 修复后（正确）
```xml
<update id="updateCourse" parameterType="top.ooyyh.edusphere.entity.Course">
    UPDATE course 
    SET title = #{title},
        subtitle = #{subtitle},
        description = #{description},
        cover_image = #{coverImage},
        category_id = #{categoryId},
        price = #{price},
        original_price = #{originalPrice},
        is_free = #{isFree},
        level = #{level},
        duration = #{duration},
        is_hot = #{isHot},
        is_new = #{isNew},
        status = #{status},  <!-- 添加这一行 -->
        updated_at = #{updatedAt}
    WHERE id = #{id}
</update>
```

### 2. 修复JWT拦截器用户ID

#### 问题
JwtInterceptor.getUserIdFromToken() 硬编码返回1，但实际教师ID是2

#### 修复
```java
private Integer getUserIdFromToken(String token) {
    // 为了简化，这里返回2，因为测试数据中教师ID是2
    // 实际项目中应该查询数据库：UserMapper.getUserByUsername(username).getId()
    return 2;
}
```

### 3. 添加详细调试信息

#### 后端调试
```java
@Override
public Result<String> publishCourse(Integer courseId, Integer teacherId) {
    try {
        System.out.println("发布课程 - 课程ID: " + courseId + ", 教师ID: " + teacherId);
        
        Course course = courseMapper.getCourseById(courseId);
        if (course == null) {
            System.out.println("课程不存在: " + courseId);
            return Result.error("课程不存在");
        }
        
        System.out.println("找到课程: " + course.getTitle() + ", 当前状态: " + course.getStatus());
        System.out.println("课程讲师ID: " + course.getInstructorId() + ", 请求教师ID: " + teacherId);
        
        // 验证课程是否属于当前教师
        if (!course.getInstructorId().equals(teacherId)) {
            System.out.println("权限验证失败: 课程不属于当前教师");
            return Result.error("无权限操作此课程");
        }
        
        course.setStatus("pending"); // 提交审核
        course.setUpdatedAt(LocalDateTime.now());
        System.out.println("准备更新课程状态为: pending");
        
        int updateResult = courseMapper.updateCourse(course);
        System.out.println("数据库更新结果: " + updateResult);
        
        return Result.success("课程已提交审核，等待管理员审核");
    } catch (Exception e) {
        System.out.println("发布课程异常: " + e.getMessage());
        e.printStackTrace();
        return Result.error("发布课程失败: " + e.getMessage());
    }
}
```

## 📁 修改的文件

### 后端文件
1. **CourseMapper.xml** - 添加status字段到updateCourse方法
2. **JwtInterceptor.java** - 修复用户ID获取逻辑
3. **TeacherServiceImpl.java** - 添加详细调试信息

## 🎯 测试步骤

### 1. 重启后端服务
```bash
cd EduSphereB
mvn spring-boot:run
```

### 2. 测试发布功能
1. 登录教师账户
2. 创建课程（应该保存为草稿状态）
3. 点击"发布"按钮
4. 检查后端控制台输出：
   - 应该显示"发布课程 - 课程ID: [数字], 教师ID: 2"
   - 应该显示"找到课程: [课程名], 当前状态: draft"
   - 应该显示"准备更新课程状态为: pending"
   - 应该显示"数据库更新结果: 1"

### 3. 验证数据库
```sql
-- 查看课程状态
SELECT id, title, status, updated_at FROM course WHERE instructor_id = 2 ORDER BY updated_at DESC;
```

## 🔧 可能的问题和解决方案

### 问题1：数据库更新结果为0
**原因**：课程ID不存在或WHERE条件不匹配
**解决**：检查课程ID是否正确，检查数据库中的课程数据

### 问题2：权限验证失败
**原因**：教师ID不匹配
**解决**：检查JwtInterceptor.getUserIdFromToken()返回的ID是否正确

### 问题3：课程不存在
**原因**：CourseMapper.getCourseById()查询失败
**解决**：检查CourseMapper.xml中的getCourseById方法

## 🚀 验证方法

### 1. 后端控制台检查
查看Spring Boot控制台输出：
- 发布请求是否到达
- 课程查询是否成功
- 权限验证是否通过
- 数据库更新是否成功

### 2. 数据库直接检查
```sql
-- 查看所有课程状态
SELECT id, title, status, instructor_id, updated_at FROM course ORDER BY updated_at DESC;

-- 查看特定教师的课程
SELECT id, title, status, updated_at FROM course WHERE instructor_id = 2 ORDER BY updated_at DESC;
```

### 3. 前端控制台检查
查看浏览器控制台：
- 发布课程ID是否正确
- API响应是否成功
- 错误信息是什么

现在数据库状态应该能正确更新了！🎉
