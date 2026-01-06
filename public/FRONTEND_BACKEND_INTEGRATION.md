# 前后端对接说明

## 对接状态 ✅

前后端已经完成对接，主要修复了以下问题：

### 1. 响应码统一
- **问题**：前端期望 `code === 0` 表示成功，但响应拦截器检查的是 `code !== 200`
- **解决**：修改前端响应拦截器，统一使用 `code === 0` 表示成功

### 2. 登录请求参数
- **问题**：前端发送了多余的 `id` 字段
- **解决**：移除登录请求中的 `id` 字段

### 3. API基础URL配置
- **问题**：前端需要配置正确的API地址
- **解决**：配置Vite代理，前端请求 `/api/*` 会自动转发到后端 `http://localhost:8080/*`

## 配置说明

### 前端配置 (EduSphereF)

#### 1. Vite代理配置 (vite.config.js)
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

#### 2. 请求配置 (src/utils/request.js)
```javascript
const service = axios.create({
  baseURL: '/api', // 使用代理
  timeout: 10000,
})
```

#### 3. 响应拦截器
```javascript
// 后端统一使用 code: 0 表示成功，1 表示失败
if (res.code && res.code !== 0) {
  console.error(res.message || 'Error')
  return Promise.reject(res)
}
```

### 后端配置 (EduSphereB)

#### 1. 跨域配置 (WebConfig.java)
```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

#### 2. JWT拦截器配置
- 公开路径：`/user/login`, `/user/register`, `/course/**`, `/category/**`, `/home/**`, `/review/course/**`
- 认证路径：其他所有路径需要JWT token

## 接口对接情况

### ✅ 已对接的接口

#### 用户认证
- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册
- `GET /user/info` - 获取用户信息

#### 首页数据
- `GET /home/data` - 获取首页数据

#### 课程管理
- `GET /course/{id}` - 获取课程详情
- `POST /course/search` - 搜索课程
- `GET /course/hot` - 获取热门课程
- `GET /course/new` - 获取最新课程
- `GET /course/category/{categoryId}` - 根据分类获取课程

#### 分类管理
- `GET /category/list` - 获取所有分类
- `GET /category/{id}` - 获取分类详情

#### 课程评价
- `GET /review/course/{courseId}` - 获取课程评价
- `POST /review/add` - 添加课程评价

#### 用户中心
- `GET /user/courses` - 获取用户课程
- `POST /user/courses/{courseId}/purchase` - 购买课程
- `GET /user/cart` - 获取购物车
- `POST /user/cart/{courseId}` - 添加到购物车
- `DELETE /user/cart/{courseId}` - 从购物车移除
- `DELETE /user/cart` - 清空购物车
- `GET /user/orders` - 获取用户订单
- `POST /user/orders/{orderNo}/pay` - 支付订单

## 启动说明

### 后端启动
1. 确保MySQL数据库运行
2. 执行 `database_schema.sql` 创建数据库和表
3. 修改 `application.yml` 中的数据库连接配置
4. 启动Spring Boot应用 (端口8080)

### 前端启动
1. 进入 `EduSphereF` 目录
2. 执行 `npm install` 安装依赖
3. 执行 `npm run dev` 启动开发服务器 (端口5173)

### 访问地址
- 前端：http://localhost:5173
- 后端：http://localhost:8080

## 测试建议

### 1. 基础功能测试
1. 用户注册/登录
2. 首页数据加载
3. 课程列表展示
4. 课程详情查看

### 2. 搜索功能测试
1. 关键词搜索
2. 分类筛选
3. 价格筛选
4. 排序功能

### 3. 用户功能测试
1. 购物车操作
2. 课程购买
3. 订单管理
4. 课程评价

## 注意事项

1. **数据库**：确保MySQL数据库已创建并执行了初始化SQL
2. **端口**：后端默认8080端口，前端默认5173端口
3. **跨域**：已配置CORS，支持跨域请求
4. **认证**：需要登录的接口会自动检查JWT token
5. **代理**：前端开发环境使用Vite代理转发请求到后端

## 故障排除

### 常见问题
1. **404错误**：检查后端接口路径是否正确
2. **跨域错误**：检查CORS配置
3. **认证失败**：检查JWT token是否正确
4. **数据库连接失败**：检查数据库配置和连接

### 调试方法
1. 查看浏览器开发者工具的网络面板
2. 查看后端控制台日志
3. 检查数据库连接状态
4. 验证JWT token有效性
