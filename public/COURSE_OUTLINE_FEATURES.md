# 课程大纲管理功能

## 🎯 功能概述

您说得对！虽然数据库表结构完整，后端API也实现了，但前端确实缺少课程大纲管理功能。现在已经创建了完整的课程大纲管理系统。

## 📚 功能特性

### 1. 课程大纲管理页面
- **页面路径**：`/teacher/courses/:id/outline`
- **访问方式**：教师工作台 → 课程卡片 → "大纲"按钮
- **权限控制**：只有教师和管理员可以访问

### 2. 章节管理功能
- ✅ **添加章节**：创建新的课程章节
- ✅ **编辑章节**：修改章节标题和描述
- ✅ **删除章节**：删除章节及其所有课时
- ✅ **章节排序**：支持章节顺序管理
- ✅ **章节统计**：显示章节下的课时数量和总时长

### 3. 课时管理功能
- ✅ **添加课时**：为章节添加新的课时
- ✅ **编辑课时**：修改课时信息
- ✅ **删除课时**：删除单个课时
- ✅ **课时类型**：支持视频、文档、测验三种类型
- ✅ **免费设置**：可以设置课时是否免费
- ✅ **时长管理**：记录课时时长

### 4. 用户体验优化
- ✅ **直观界面**：树形结构展示章节和课时
- ✅ **空状态提示**：没有内容时显示引导信息
- ✅ **实时更新**：操作后立即刷新数据
- ✅ **响应式设计**：支持移动端访问
- ✅ **权限验证**：前端和后端双重权限控制

## 🗂️ 数据库表结构

### course_section (课程章节表)
```sql
CREATE TABLE course_section (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '章节标题',
    description TEXT COMMENT '章节描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) COMMENT '课程章节表';
```

### course_lesson (课程课时表)
```sql
CREATE TABLE course_lesson (
    id INT PRIMARY KEY AUTO_INCREMENT,
    section_id INT NOT NULL COMMENT '章节ID',
    title VARCHAR(200) NOT NULL COMMENT '课时标题',
    description TEXT COMMENT '课时描述',
    type ENUM('video', 'document', 'quiz') DEFAULT 'video' COMMENT '课时类型',
    duration VARCHAR(20) COMMENT '课时时长',
    video_url VARCHAR(500) COMMENT '视频URL',
    document_url VARCHAR(500) COMMENT '文档URL',
    is_free TINYINT DEFAULT 0 COMMENT '是否免费：1-免费，0-付费',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (section_id) REFERENCES course_section(id) ON DELETE CASCADE
) COMMENT '课程课时表';
```

## 🔧 后端API接口

### 章节管理API
- `GET /teacher/courses/{courseId}/sections` - 获取课程章节列表
- `POST /teacher/courses/{courseId}/sections` - 创建新章节
- `PUT /teacher/sections/{sectionId}` - 更新章节信息
- `DELETE /teacher/sections/{sectionId}` - 删除章节

### 课时管理API
- `GET /teacher/sections/{sectionId}/lessons` - 获取章节课时列表
- `POST /teacher/sections/{sectionId}/lessons` - 创建新课时
- `PUT /teacher/lessons/{lessonId}` - 更新课时信息
- `DELETE /teacher/lessons/{lessonId}` - 删除课时

## 📱 前端页面结构

### 1. 课程大纲页面 (CourseOutline.vue)
```
课程大纲管理
├── 页面头部
│   ├── 返回按钮
│   ├── 课程标题
│   └── 添加章节按钮
├── 章节列表
│   ├── 章节头部
│   │   ├── 章节信息（标题、描述、统计）
│   │   └── 操作按钮（添加课时、编辑、删除）
│   └── 课时列表
│       ├── 课时信息（标题、类型、时长、免费标识）
│       └── 操作按钮（编辑、删除）
└── 对话框
    ├── 章节编辑对话框
    └── 课时编辑对话框
```

### 2. 教师工作台集成
- 在课程卡片中添加"大纲"按钮
- 点击后跳转到课程大纲管理页面
- 保持原有的编辑和预览功能

## 🎨 界面设计

### 视觉层次
- **章节**：使用文件夹图标，背景色区分
- **课时**：使用类型图标（视频、文档、测验）
- **操作按钮**：统一的尺寸和间距
- **状态标识**：免费标签、类型标签

### 交互体验
- **拖拽排序**：支持章节和课时的拖拽排序（未来功能）
- **批量操作**：支持批量删除和移动（未来功能）
- **实时预览**：课时内容实时预览（未来功能）

## 🚀 使用流程

### 1. 访问课程大纲
1. 登录教师账号
2. 进入教师工作台
3. 找到目标课程
4. 点击"大纲"按钮

### 2. 创建课程大纲
1. 点击"添加章节"创建第一个章节
2. 填写章节标题和描述
3. 为章节添加课时
4. 设置课时类型和内容
5. 重复以上步骤完善大纲

### 3. 管理课程内容
1. 编辑章节信息
2. 添加、编辑、删除课时
3. 设置课时是否免费
4. 调整章节和课时的顺序

## 🔒 权限控制

### 前端权限
- 登录状态检查
- 角色权限验证（teacher/admin）
- 页面访问控制

### 后端权限
- JWT身份验证
- 角色权限验证
- 数据所有权验证（只能操作自己的课程）

## 📈 未来扩展

### 计划功能
1. **拖拽排序**：支持章节和课时的拖拽排序
2. **批量操作**：批量删除、移动、复制
3. **模板功能**：课程大纲模板
4. **导入导出**：支持大纲的导入导出
5. **预览功能**：课时内容实时预览
6. **统计分析**：课程完成度统计

### 技术优化
1. **虚拟滚动**：大量课时时的性能优化
2. **缓存机制**：减少API调用
3. **离线支持**：支持离线编辑
4. **实时同步**：多用户协作编辑

现在课程大纲管理功能已经完整实现了！教师可以方便地管理课程的章节和课时内容。🎉
