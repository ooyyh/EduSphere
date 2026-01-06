# 课程列表页面数据渲染问题修复

## 问题描述

从用户提供的截图可以看到，课程列表页面存在以下问题：

1. **课程数量显示错误**：显示"共找到0门课程"但实际显示了3个课程卡片
2. **图片加载失败**：所有课程卡片都显示"FAILED"
3. **数据渲染错误**：显示了原始JSON数据而不是格式化的课程信息
4. **数据不完整**：JSON中很多字段都是null

## 根本原因分析

### 1. 后端查询问题
- `CourseMapper.xml`中的`getCourseList`查询缺少了`instructor_avatar`和`instructor_email`字段
- 导致instructor对象不完整，前端无法正确显示讲师信息

### 2. 前端数据映射问题
- 前端期望的字段名与后端返回的不匹配
- 例如：前端使用`course.image`，但后端返回的是`course.coverImage`
- 前端使用`course.instructor`，但后端返回的是`course.instructor.username`

### 3. 课程数量统计问题
- 后端没有返回总数信息
- 前端无法正确显示课程数量

## 修复方案

### 1. 后端修复

#### 1.1 修复CourseMapper.xml查询
```xml
<!-- 修复前 -->
<select id="getCourseList" resultMap="CourseResultMap">
    SELECT c.*, u.username as instructor_name, cat.name as category_name
    FROM course c
    LEFT JOIN user u ON c.instructor_id = u.id
    LEFT JOIN category cat ON c.category_id = cat.id

<!-- 修复后 -->
<select id="getCourseList" resultMap="CourseResultMap">
    SELECT c.*, 
           u.id as instructor_id,
           u.username as instructor_name, 
           u.avatar as instructor_avatar,
           u.email as instructor_email,
           cat.name as category_name
    FROM course c
    LEFT JOIN user u ON c.instructor_id = u.id
    LEFT JOIN category cat ON c.category_id = cat.id
```

#### 1.2 创建响应类
```java
@Data
public class CourseSearchResponse {
    private List<Course> courses;
    private Integer total;
    private Integer pageNum;
    private Integer pageSize;
}
```

#### 1.3 修改服务层
```java
@Override
public Result<CourseSearchResponse> searchCourses(CourseSearchRequest request) {
    try {
        List<Course> courses = courseMapper.getCourseList(request);
        Integer total = courseMapper.getCourseCount(request);
        CourseSearchResponse response = new CourseSearchResponse(
            courses, total, request.getPageNum(), request.getPageSize()
        );
        return Result.success(response);
    } catch (Exception e) {
        return Result.error("搜索课程失败: " + e.getMessage());
    }
}
```

### 2. 前端修复

#### 2.1 修复数据字段映射
```vue
<!-- 修复前 -->
<el-image :src="course.image" />

<!-- 修复后 -->
<el-image :src="course.coverImage" />
```

#### 2.2 修复讲师信息显示
```vue
<!-- 修复前 -->
<p class="course-instructor">{{ course.instructor }}</p>

<!-- 修复后 -->
<p class="course-instructor">{{ course.instructor?.username || '未知讲师' }}</p>
```

#### 2.3 修复数据获取逻辑
```javascript
// 修复前
if (response.code === 0) {
  courses.value = response.data || []
}

// 修复后
if (response.code === 0) {
  courses.value = response.data.courses || []
  totalCourses.value = response.data.total || 0
}
```

#### 2.4 添加空值处理
```vue
<!-- 价格显示 -->
<span class="current-price">¥{{ course.price || 0 }}</span>

<!-- 学员数显示 -->
<span class="student-count">{{ course.studentCount || 0 }} 学员</span>

<!-- 评分显示 -->
<el-rate :model-value="course.rating || 0" disabled />
```

## 修复文件列表

### 后端文件
1. `EduSphereB/src/main/resources/mapper/CourseMapper.xml` - 修复查询字段
2. `EduSphereB/src/main/java/top/ooyyh/edusphere/response/CourseSearchResponse.java` - 新增响应类
3. `EduSphereB/src/main/java/top/ooyyh/edusphere/service/CourseService.java` - 修改接口
4. `EduSphereB/src/main/java/top/ooyyh/edusphere/service/impl/CourseServiceImpl.java` - 修改实现
5. `EduSphereB/src/main/java/top/ooyyh/edusphere/controller/CourseController.java` - 修改控制器

### 前端文件
1. `EduSphereF/src/views/pages/courses/CourseList.vue` - 修复数据映射和显示逻辑

### 数据库文件
1. `public/test_data.sql` - 新增测试数据

## 测试建议

1. **数据库测试**：运行`test_data.sql`插入测试数据
2. **后端测试**：启动后端服务，测试`/course/search`接口
3. **前端测试**：启动前端服务，检查课程列表页面显示
4. **功能测试**：测试搜索、筛选、排序等功能

## 预期结果

修复后，课程列表页面应该能够：
1. 正确显示课程数量
2. 正确显示课程封面图片
3. 正确显示格式化的课程信息（标题、讲师、价格等）
4. 正确处理空值和默认值
5. 支持搜索、筛选、排序等功能

## 注意事项

1. 确保数据库中有测试数据
2. 确保后端服务正常运行
3. 确保前端代理配置正确
4. 检查图片URL是否可访问
