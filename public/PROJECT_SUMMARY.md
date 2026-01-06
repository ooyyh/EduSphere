# EduSphere 项目总结

## 项目概述
EduSphere是一个在线教育平台，包含前端Vue.js应用和后端Spring Boot应用。本项目实现了完整的在线教育平台功能，包括用户管理、课程管理、购物车、订单系统等。

## 技术栈

### 后端技术栈
- **框架**: Spring Boot 2.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis
- **认证**: JWT (JSON Web Token)
- **构建工具**: Maven
- **Java版本**: JDK 8+

### 前端技术栈
- **框架**: Vue 3
- **UI库**: Element Plus
- **构建工具**: Vite
- **路由**: Vue Router 4
- **HTTP客户端**: Axios

## 项目结构

```
EduSphere/
├── EduSphereB/                 # 后端项目
│   ├── src/main/java/
│   │   └── top/ooyyh/edusphere/
│   │       ├── config/         # 配置类
│   │       ├── controller/     # 控制器
│   │       ├── entity/         # 实体类
│   │       ├── mapper/         # 数据访问层
│   │       ├── request/        # 请求对象
│   │       ├── service/        # 服务层
│   │       └── utils/          # 工具类
│   └── src/main/resources/
│       ├── application.yml     # 配置文件
│       └── mapper/             # MyBatis XML文件
├── EduSphereF/                 # 前端项目
│   ├── src/
│   │   ├── components/         # 组件
│   │   ├── views/              # 页面
│   │   ├── router/             # 路由配置
│   │   └── utils/               # 工具类
│   └── package.json
└── public/                     # 公共文件
    ├── database_schema.sql     # 数据库表结构
    ├── API_DOCUMENTATION.md    # API文档
    └── PROJECT_SUMMARY.md      # 项目总结
```

## 功能模块

### 1. 用户管理
- ✅ 用户注册/登录
- ✅ JWT认证
- ✅ 用户信息管理
- ✅ 角色权限控制

### 2. 课程管理
- ✅ 课程列表展示
- ✅ 课程详情查看
- ✅ 课程搜索和筛选
- ✅ 课程分类管理
- ✅ 课程评价系统

### 3. 用户个人中心
- ✅ 我的课程
- ✅ 学习进度跟踪
- ✅ 课程评价管理

### 4. 购物车系统
- ✅ 添加/移除课程
- ✅ 购物车管理
- ✅ 批量操作

### 5. 订单系统
- ✅ 订单创建
- ✅ 订单支付
- ✅ 订单管理
- ✅ 订单状态跟踪

### 6. 首页功能
- ✅ 热门课程推荐
- ✅ 最新课程展示
- ✅ 分类导航
- ✅ 统计数据展示

## 数据库设计

### 核心表结构
1. **user** - 用户表
2. **category** - 课程分类表
3. **course** - 课程表
4. **course_section** - 课程章节表
5. **course_lesson** - 课程课时表
6. **course_review** - 课程评价表
7. **user_course** - 用户课程关系表
8. **cart** - 购物车表
9. **orders** - 订单表
10. **order_item** - 订单详情表
11. **learning_progress** - 学习进度表

## API接口

### 认证相关
- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册
- `GET /user/info` - 获取用户信息

### 课程相关
- `GET /course/{id}` - 获取课程详情
- `POST /course/search` - 搜索课程
- `GET /course/hot` - 获取热门课程
- `GET /course/new` - 获取最新课程
- `GET /course/category/{categoryId}` - 根据分类获取课程

### 分类相关
- `GET /category/list` - 获取所有分类
- `GET /category/{id}` - 获取分类详情

### 评价相关
- `GET /review/course/{courseId}` - 获取课程评价
- `POST /review/add` - 添加课程评价

### 用户中心
- `GET /user/courses` - 获取用户课程
- `POST /user/courses/{courseId}/purchase` - 购买课程
- `GET /user/cart` - 获取购物车
- `POST /user/cart/{courseId}` - 添加到购物车
- `DELETE /user/cart/{courseId}` - 从购物车移除
- `GET /user/orders` - 获取用户订单
- `POST /user/orders/{orderNo}/pay` - 支付订单

### 首页数据
- `GET /home/data` - 获取首页数据

## 部署说明

### 后端部署
1. 确保MySQL数据库已安装并运行
2. 执行`database_schema.sql`创建数据库和表
3. 修改`application.yml`中的数据库连接配置
4. 运行Spring Boot应用

### 前端部署
1. 安装Node.js和npm
2. 进入EduSphereF目录
3. 执行`npm install`安装依赖
4. 执行`npm run dev`启动开发服务器

## 开发规范

### 代码规范
- 使用Lombok减少样板代码
- 统一使用Result类封装API响应
- 使用JWT进行身份认证
- 遵循RESTful API设计规范

### 数据库规范
- 表名使用下划线命名法
- 字段名使用下划线命名法
- 主键统一使用自增ID
- 时间字段统一使用TIMESTAMP类型

## 扩展功能建议

### 短期扩展
1. 添加课程视频播放功能
2. 实现学习进度统计
3. 添加消息通知系统
4. 实现课程收藏功能

### 长期扩展
1. 添加直播功能
2. 实现在线考试系统
3. 添加社区功能
4. 实现移动端适配
5. 添加数据分析功能

## 总结

本项目成功实现了一个功能完整的在线教育平台后端系统，包含了用户管理、课程管理、购物车、订单等核心功能。代码结构清晰，遵循了良好的开发规范，具有良好的可扩展性和维护性。

前端Vue.js应用提供了现代化的用户界面，后端Spring Boot应用提供了稳定的API服务，两者配合实现了完整的在线教育平台功能。
