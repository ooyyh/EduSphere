# 课程发布按钮问题修复

## 🚨 问题描述

用户反馈：第一次保存为草稿之后，点击外面的"发布"按钮还是没有变成审核中状态。

## 🔍 问题分析

### 可能的原因
1. **课程创建状态错误** - 创建时直接设置为 `pending` 而不是 `draft`
2. **课程ID获取失败** - 发布按钮无法获取正确的课程ID
3. **API调用失败** - 发布API调用失败或返回错误
4. **状态更新失败** - 后端状态更新失败

## 🛠️ 修复方案

### 1. 修复课程创建状态

#### 修复前（错误）
```javascript
// 课程创建时直接提交审核
const response = await request.post('/teacher/courses', {
  ...form,
  status: 'pending' // 提交审核状态
})
```

#### 修复后（正确）
```javascript
// 课程创建时保存为草稿
const response = await request.post('/teacher/courses', {
  ...form,
  status: 'draft' // 默认草稿状态
})
```

### 2. 添加调试信息

#### 前端调试
```javascript
// 发布课程时添加调试信息
const publishCourse = async (courseId) => {
  try {
    console.log('发布课程ID:', courseId) // 调试信息
    const response = await request.post(`/teacher/courses/${courseId}/publish`)
    console.log('发布响应:', response) // 调试信息
    // ...
  } catch (error) {
    console.error('发布课程错误:', error) // 调试信息
  }
}

// 课程列表加载时添加调试信息
const loadCourses = async () => {
  // ...
  console.log('课程列表数据:', courses.value) // 调试信息
  console.log('课程ID列表:', courses.value.map(c => c.id)) // 调试信息
}
```

### 3. 完整的课程流程

#### 修复后的流程
```
1. 创建课程 → 草稿状态 (draft)
   ↓ 点击"发布"按钮
2. 调用 /teacher/courses/{id}/publish API
   ↓ 后端更新状态
3. 审核中状态 (pending) → 等待管理员审核
```

## 📁 修改的文件

### 前端文件
1. **CourseCreate.vue** - 修复课程创建状态
2. **TeacherDashboard.vue** - 添加调试信息

## 🎯 测试步骤

### 1. 创建课程测试
1. 点击"创建课程"
2. 填写课程信息
3. 点击"创建课程"
4. 检查控制台：应该显示"课程创建成功，已保存为草稿"
5. 检查课程状态：应该显示"草稿"

### 2. 发布课程测试
1. 在教师工作台找到草稿状态的课程
2. 点击"发布"按钮
3. 检查控制台：
   - 应该显示"发布课程ID: [数字]"
   - 应该显示"发布响应: {code: 0, message: '...'}"
4. 检查课程状态：应该变为"审核中"

### 3. 错误排查
如果发布按钮不工作，检查控制台：
- **课程ID是否正确**：`发布课程ID: [数字]`
- **API响应是否成功**：`发布响应: {code: 0, ...}`
- **是否有错误信息**：`发布课程错误: [错误详情]`

## 🔧 可能的问题和解决方案

### 问题1：课程ID为undefined
**原因**：课程列表API没有返回ID字段
**解决**：检查后端CourseMapper.xml是否正确映射ID字段

### 问题2：API调用失败
**原因**：JWT认证失败或网络问题
**解决**：检查JWT token是否有效，检查网络连接

### 问题3：状态更新失败
**原因**：后端数据库更新失败
**解决**：检查后端日志，确认数据库连接正常

## 🚀 验证方法

### 1. 浏览器控制台检查
打开浏览器开发者工具，查看控制台输出：
- 课程列表数据是否包含ID
- 发布按钮点击时是否输出课程ID
- API响应是否成功

### 2. 网络请求检查
在开发者工具的Network标签页中：
- 查看 `/teacher/courses` 请求是否成功
- 查看 `/teacher/courses/{id}/publish` 请求是否成功
- 检查请求和响应的状态码

### 3. 数据库检查
```sql
-- 查看课程状态
SELECT id, title, status FROM course WHERE instructor_id = 2;

-- 查看状态更新
SELECT id, title, status, updated_at FROM course ORDER BY updated_at DESC;
```

现在应该可以正常工作了！如果还有问题，请查看浏览器控制台的调试信息。🎉
