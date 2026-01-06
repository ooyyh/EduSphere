# 教师知识创作功能说明

## 功能概述

为教师提供完整的知识创作和管理平台，支持课程创建、编辑、发布、章节管理、课时管理等功能。

## 核心功能

### 1. 教师工作台 (`/teacher`)
- **课程总览**：显示教师创建的所有课程
- **数据统计**：总课程数、学员总数、平均评分、总浏览量
- **课程管理**：创建、编辑、删除、发布、下架课程
- **状态筛选**：按草稿/已发布状态筛选课程
- **快速操作**：一键发布、预览、统计查看

### 2. 课程创作 (`/teacher/courses/create`)
- **基本信息**：课程标题、副标题、描述、封面
- **课程设置**：分类、难度、时长、价格、标签
- **实时预览**：封面上传预览
- **草稿保存**：支持保存草稿和直接发布
- **表单验证**：完整的输入验证和错误提示

### 3. 课程编辑 (`/teacher/courses/:id/edit`)
- **信息修改**：编辑课程的所有基本信息
- **状态管理**：草稿保存和课程更新
- **数据回填**：自动填充现有课程数据
- **版本控制**：保持课程版本一致性

## 技术实现

### 后端API设计

#### 课程管理API
```
GET    /teacher/courses              # 获取教师课程列表
POST   /teacher/courses              # 创建课程
PUT    /teacher/courses/{id}         # 更新课程
DELETE /teacher/courses/{id}         # 删除课程
POST   /teacher/courses/{id}/publish # 发布课程
POST   /teacher/courses/{id}/unpublish # 下架课程
```

#### 章节管理API
```
GET    /teacher/courses/{id}/sections # 获取课程章节
POST   /teacher/courses/{id}/sections # 创建章节
PUT    /teacher/sections/{id}         # 更新章节
DELETE /teacher/sections/{id}         # 删除章节
```

#### 课时管理API
```
GET    /teacher/sections/{id}/lessons # 获取章节课时
POST   /teacher/sections/{id}/lessons # 创建课时
PUT    /teacher/lessons/{id}          # 更新课时
DELETE /teacher/lessons/{id}          # 删除课时
```

#### 统计API
```
GET    /teacher/courses/{id}/stats    # 获取课程统计
```

### 前端页面设计

#### 1. 教师工作台 (`TeacherDashboard.vue`)
- **响应式布局**：适配不同屏幕尺寸
- **统计卡片**：直观展示关键数据
- **课程网格**：卡片式展示课程列表
- **操作菜单**：下拉菜单提供更多操作
- **状态管理**：实时更新课程状态

#### 2. 课程创作页面 (`CourseCreate.vue`)
- **分步表单**：基本信息 + 课程设置
- **文件上传**：支持封面图片上传
- **实时验证**：表单字段实时验证
- **保存机制**：草稿保存和直接发布

#### 3. 课程编辑页面 (`CourseEdit.vue`)
- **数据回填**：自动填充现有课程数据
- **状态保持**：保持课程当前状态
- **更新机制**：支持草稿保存和课程更新

### 数据库设计

#### 课程表 (course)
```sql
- id: 课程ID
- title: 课程标题
- subtitle: 课程副标题
- description: 课程描述
- cover_image: 封面图片
- instructor_id: 讲师ID
- category_id: 分类ID
- price: 课程价格
- original_price: 原价
- is_free: 是否免费
- level: 难度等级
- duration: 课程时长
- student_count: 学员数
- rating: 评分
- rating_count: 评分人数
- is_hot: 是否热门
- is_new: 是否最新
- status: 状态 (0-草稿, 1-已发布)
- created_at: 创建时间
- updated_at: 更新时间
```

#### 章节表 (course_section)
```sql
- id: 章节ID
- course_id: 课程ID
- title: 章节标题
- description: 章节描述
- sort_order: 排序
- created_at: 创建时间
- updated_at: 更新时间
```

#### 课时表 (course_lesson)
```sql
- id: 课时ID
- section_id: 章节ID
- title: 课时标题
- description: 课时描述
- type: 课时类型 (video/document/quiz)
- content: 课时内容
- duration: 课时时长
- is_free: 是否免费
- sort_order: 排序
- created_at: 创建时间
- updated_at: 更新时间
```

## 功能特色

### 1. 用户体验
- **直观界面**：清晰的操作流程和视觉设计
- **响应式设计**：适配各种设备屏幕
- **实时反馈**：操作结果即时反馈
- **状态管理**：课程状态清晰可见

### 2. 数据管理
- **完整CRUD**：支持课程的完整生命周期管理
- **状态控制**：草稿/发布状态管理
- **数据验证**：前后端双重验证
- **错误处理**：友好的错误提示

### 3. 扩展性
- **模块化设计**：功能模块独立，易于扩展
- **API标准化**：RESTful API设计
- **组件复用**：前端组件高度复用
- **配置灵活**：支持多种配置选项

## 使用流程

### 教师创作课程流程
1. **登录系统** → 点击"教师工作台"
2. **查看工作台** → 了解课程统计和现有课程
3. **创建课程** → 点击"创建新课程"
4. **填写信息** → 完善课程基本信息和设置
5. **保存草稿** → 保存为草稿或直接发布
6. **管理课程** → 编辑、发布、下架、删除课程

### 课程管理流程
1. **课程列表** → 查看所有课程
2. **状态筛选** → 按状态筛选课程
3. **快速操作** → 发布、预览、统计
4. **编辑课程** → 修改课程信息
5. **删除课程** → 删除不需要的课程

## 技术栈

### 后端
- **Spring Boot**：主框架
- **MyBatis-Plus**：ORM框架
- **MySQL**：数据库
- **JWT**：身份认证
- **Lombok**：代码简化

### 前端
- **Vue 3**：主框架
- **Element Plus**：UI组件库
- **Vue Router**：路由管理
- **Axios**：HTTP客户端
- **Composition API**：组合式API

## 部署说明

### 后端部署
1. 确保MySQL数据库运行
2. 执行数据库脚本
3. 配置数据库连接
4. 启动Spring Boot应用

### 前端部署
1. 安装依赖：`npm install`
2. 启动开发服务器：`npm run dev`
3. 构建生产版本：`npm run build`

## 后续扩展

### 计划功能
1. **章节管理**：章节的拖拽排序
2. **课时管理**：视频上传、文档编辑
3. **课程预览**：实时预览功能
4. **数据分析**：详细的课程统计
5. **批量操作**：批量发布、删除
6. **模板系统**：课程模板功能

### 技术优化
1. **文件上传**：集成云存储服务
2. **缓存优化**：Redis缓存
3. **搜索功能**：Elasticsearch集成
4. **消息队列**：异步处理
5. **监控系统**：应用监控

## 总结

教师知识创作功能提供了完整的课程创作和管理解决方案，支持教师从课程创建到发布的完整流程。通过直观的界面设计和强大的功能支持，帮助教师高效地创作和管理优质课程内容。
