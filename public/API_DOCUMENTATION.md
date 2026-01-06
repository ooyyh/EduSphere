# EduSphere 后端API文档

## 概述
EduSphere是一个在线教育平台的后端API，提供用户管理、课程管理、购物车、订单等功能。

## 基础信息
- 基础URL: `http://localhost:8080`
- 认证方式: JWT Token
- 请求头: `Authorization: Bearer <token>`

## API接口列表

### 1. 用户认证

#### 用户登录
- **URL**: `POST /user/login`
- **描述**: 用户登录获取JWT token
- **请求体**:
```json
{
  "username": "string",
  "password": "string",
  "role": "string"
}
```
- **响应**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "user": {
      "id": 1,
      "username": "string",
      "role": "string"
    },
    "token": "string"
  }
}
```

#### 用户注册
- **URL**: `POST /user/register`
- **描述**: 用户注册
- **请求体**:
```json
{
  "username": "string",
  "password": "string",
  "role": "string"
}
```

#### 获取用户信息
- **URL**: `GET /user/info`
- **描述**: 获取当前登录用户信息
- **认证**: 需要JWT token

### 2. 首页数据

#### 获取首页数据
- **URL**: `GET /home/data`
- **描述**: 获取首页展示数据（热门课程、最新课程、分类等）

### 3. 课程管理

#### 获取课程详情
- **URL**: `GET /course/{id}`
- **描述**: 根据ID获取课程详细信息

#### 搜索课程
- **URL**: `POST /course/search`
- **描述**: 根据条件搜索课程
- **请求体**:
```json
{
  "keyword": "string",
  "category": "string",
  "level": "string",
  "priceRange": "string",
  "sortBy": "string",
  "pageNum": 1,
  "pageSize": 12
}
```

#### 获取热门课程
- **URL**: `GET /course/hot?limit=6`
- **描述**: 获取热门课程列表

#### 获取最新课程
- **URL**: `GET /course/new?limit=6`
- **描述**: 获取最新课程列表

#### 根据分类获取课程
- **URL**: `GET /course/category/{categoryId}?limit=6`
- **描述**: 根据分类ID获取课程列表

### 4. 分类管理

#### 获取所有分类
- **URL**: `GET /category/list`
- **描述**: 获取所有课程分类

#### 获取分类详情
- **URL**: `GET /category/{id}`
- **描述**: 根据ID获取分类详情

### 5. 课程评价

#### 获取课程评价
- **URL**: `GET /review/course/{courseId}`
- **描述**: 获取指定课程的评价列表

#### 添加课程评价
- **URL**: `POST /review/add`
- **描述**: 添加课程评价
- **认证**: 需要JWT token
- **请求体**:
```json
{
  "courseId": 1,
  "rating": 5,
  "content": "string"
}
```

### 6. 用户个人中心

#### 获取用户课程
- **URL**: `GET /user/courses`
- **描述**: 获取用户购买的课程列表
- **认证**: 需要JWT token

#### 检查课程购买状态
- **URL**: `GET /user/courses/{courseId}/purchased`
- **描述**: 检查用户是否已购买指定课程
- **认证**: 需要JWT token

#### 购买课程
- **URL**: `POST /user/courses/{courseId}/purchase`
- **描述**: 购买指定课程
- **认证**: 需要JWT token

### 7. 购物车管理

#### 获取购物车
- **URL**: `GET /user/cart`
- **描述**: 获取用户购物车列表
- **认证**: 需要JWT token

#### 添加到购物车
- **URL**: `POST /user/cart/{courseId}`
- **描述**: 将课程添加到购物车
- **认证**: 需要JWT token

#### 从购物车移除
- **URL**: `DELETE /user/cart/{courseId}`
- **描述**: 从购物车移除指定课程
- **认证**: 需要JWT token

#### 清空购物车
- **URL**: `DELETE /user/cart`
- **描述**: 清空用户购物车
- **认证**: 需要JWT token

### 8. 订单管理

#### 获取用户订单
- **URL**: `GET /user/orders`
- **描述**: 获取用户订单列表
- **认证**: 需要JWT token

#### 获取订单详情
- **URL**: `GET /user/orders/{orderNo}`
- **描述**: 根据订单号获取订单详情

#### 支付订单
- **URL**: `POST /user/orders/{orderNo}/pay`
- **描述**: 支付指定订单

## 数据模型

### 用户 (User)
```json
{
  "id": 1,
  "username": "string",
  "email": "string",
  "avatar": "string",
  "role": "string",
  "status": 1,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### 课程 (Course)
```json
{
  "id": 1,
  "title": "string",
  "subtitle": "string",
  "description": "string",
  "coverImage": "string",
  "instructorId": 1,
  "categoryId": 1,
  "price": 299.00,
  "originalPrice": 399.00,
  "isFree": false,
  "level": "intermediate",
  "duration": "32小时",
  "studentCount": 1000,
  "rating": 4.8,
  "ratingCount": 100,
  "isHot": true,
  "isNew": false,
  "status": 1,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### 分类 (Category)
```json
{
  "id": 1,
  "name": "string",
  "slug": "string",
  "description": "string",
  "icon": "string",
  "color": "string",
  "sortOrder": 1,
  "status": 1,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

## 错误码说明

- `0`: 成功
- `1`: 失败

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带JWT token
2. 分页参数：pageNum从1开始，pageSize默认为12
3. 角色类型：admin（管理员）、teacher（教师）、student（学生）
4. 课程难度：beginner（入门）、intermediate（进阶）、advanced（高级）
5. 订单状态：pending（待支付）、paid（已支付）、cancelled（已取消）、refunded（已退款）
