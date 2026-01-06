# 角色和数据对接修复说明

## 修复的问题

### 1. 角色字段不匹配 ✅

**问题描述**：
- 数据库role字段：`admin`, `teacher`, `student` (字符串)
- 前端传递：`1`, `2`, `3` (数字)
- 导致登录和注册失败

**修复方案**：
1. 修改前端登录和注册页面的默认角色值
2. 统一使用字符串格式的角色值
3. 创建角色常量配置文件

**修复内容**：
```javascript
// 修改前
role: '1' // 默认角色

// 修改后  
role: 'student' // 默认角色：admin, teacher, student
```

### 2. 前端使用模拟数据 ✅

**问题描述**：
- 前端页面使用硬编码的模拟数据
- 没有调用后端API获取真实数据
- 导致数据不一致

**修复方案**：
1. 移除所有硬编码的模拟数据
2. 添加API调用方法
3. 实现真实的数据获取逻辑

**修复的页面**：
- ✅ 课程列表页面 (`CourseList.vue`)
- ✅ 首页 (`PlayGround.vue`) 
- ✅ 课程详情页面 (`CourseDetail.vue`)

### 3. 数据获取方法实现

#### 课程列表页面
```javascript
// 获取课程数据
const loadCourses = async () => {
  try {
    loading.value = true
    const response = await request.post('/course/search', {
      keyword: searchQuery.value,
      category: selectedCategory.value,
      level: selectedLevel.value,
      priceRange: selectedPriceRange.value,
      sortBy: sortBy.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.code === 0) {
      courses.value = response.data || []
    }
  } catch (error) {
    console.error('获取课程数据失败:', error)
  } finally {
    loading.value = false
  }
}
```

#### 首页数据获取
```javascript
// 获取首页数据
const loadHomeData = async () => {
  try {
    const response = await request.get('/home/data')
    if (response.code === 0) {
      featuredCourses.value = response.data.hotCourses || []
      categories.value = response.data.categories || []
      if (response.data.stats) {
        stats.value = response.data.stats
      }
    }
  } catch (error) {
    console.error('获取首页数据失败:', error)
  }
}
```

#### 课程详情获取
```javascript
// 获取课程详情
const loadCourseDetail = async () => {
  try {
    loading.value = true
    const courseId = route.params.id
    const response = await request.get(`/course/${courseId}`)
    
    if (response.code === 0) {
      course.value = response.data
    }
  } catch (error) {
    console.error('获取课程详情失败:', error)
  } finally {
    loading.value = false
  }
}
```

### 4. 角色常量配置 ✅

**新增文件**：`src/utils/constants.js`

```javascript
// 角色常量定义
export const ROLES = {
  ADMIN: 'admin',
  TEACHER: 'teacher', 
  STUDENT: 'student'
}

// 角色显示名称映射
export const ROLE_NAMES = {
  [ROLES.ADMIN]: '管理员',
  [ROLES.TEACHER]: '教师',
  [ROLES.STUDENT]: '学生'
}
```

## 修复后的数据流

### 1. 用户认证流程
```
前端登录 → 发送 {username, password, role: 'student'} → 后端验证 → 返回JWT token
```

### 2. 课程数据流程
```
前端页面加载 → 调用后端API → 获取真实数据 → 渲染页面
```

### 3. 搜索筛选流程
```
用户操作筛选 → 调用后端搜索API → 返回筛选结果 → 更新页面
```

## 验证方法

### 1. 角色验证
- 测试不同角色的登录
- 验证角色权限控制
- 检查用户信息显示

### 2. 数据验证
- 检查课程列表是否显示真实数据
- 验证搜索和筛选功能
- 测试课程详情页面

### 3. API验证
- 检查网络请求是否正常
- 验证响应数据格式
- 测试错误处理机制

## 注意事项

1. **角色一致性**：确保前后端使用相同的角色值
2. **数据格式**：确保API返回的数据格式与前端期望一致
3. **错误处理**：添加适当的错误处理和用户提示
4. **加载状态**：在数据加载时显示loading状态

## 后续优化建议

1. **缓存机制**：添加数据缓存减少API调用
2. **分页优化**：实现真正的分页功能
3. **搜索优化**：添加搜索防抖和缓存
4. **错误处理**：完善错误处理和用户反馈
