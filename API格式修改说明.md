# API 返回格式统一修改 - message → msg

> 修改时间：2025-11-26
> 修改范围：后端 Result 类、ResponseUtils 类，前端所有 API 调用

---

## 修改原因

将 API 返回格式从 `message` 统一改为 `msg`，使返回格式更简洁，并在前端统一使用 ElMessage 显示提示信息。

---

## 修改内容

### 一、后端修改（3个文件）

#### 1. Result.java - 统一返回结果类
**文件路径：** `EduSphere/src/main/java/top/ooyyh/edusphere/utils/Result.java`

**修改内容：**
```java
// 修改前
private String message;

// 修改后
private String msg;
```

**影响方法：**
- `success(String msg, T data)`
- `error(String msg)`
- `error(String msg, T data)`
- `withMsg(String msg)` (原 withMessage)

#### 2. ResponseUtils.java - 响应工具类
**文件路径：** `EduSphere/src/main/java/top/ooyyh/edusphere/utils/ResponseUtils.java`

**修改内容：**
```java
// ApiResponse 内部类
public static class ApiResponse {
    private int code;
    private String msg;        // 原 message
    private Object data;

    // getter/setter 也相应修改
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
}
```

**影响方法：**
- `writeJsonResponse(response, status, msg)`
- `writeSuccessResponse(response, msg)`
- `writeErrorResponse(response, status, msg)`

### 二、前端修改（17个文件）

#### 1. request.js - 核心请求拦截器
**文件路径：** `EduSphereF/src/utils/request.js`

**主要修改：**
```javascript
// 1. 引入 ElMessage
import { ElMessage } from 'element-plus'

// 2. 响应拦截器 - 业务错误处理
if (res.code && res.code !== 0) {
  ElMessage.error(res.msg || '操作失败')  // 原 res.message
  return Promise.reject(res)
}

// 3. HTTP 错误处理
const msg = error.response.data?.msg || error.message  // 原 .message
ElMessage.error(errorMsg)  // 统一显示提示
```

**新增功能：**
- ✅ 自动显示所有 API 错误提示（ElMessage.error）
- ✅ 401 认证失败自动跳转登录页
- ✅ 统一错误消息格式

#### 2. 页面组件修改（16个文件）

修改所有使用 API 响应的 `response.message` 或 `error.message` 为 `.msg`：

| 文件 | 修改数量 | 主要修改点 |
|------|---------|-----------|
| **CourseList.vue** | 2处 | 购买失败、获取课程失败 |
| **AdminDashboard.vue** | 4处 | 审核、拒绝、删除课程/用户 |
| **LearningPage.vue** | 1处 | 获取大纲失败 |
| **TeacherDashboard.vue** | 4处 | 课程列表、审核、下架、删除 |
| **auth.js** | 2处 | 登录、注册失败 |
| **api.js** | 2处 | handleApiCall、handleApiResponse |
| **CourseCreate.vue** | 2处 | 保存草稿、创建课程 |
| **CourseEdit.vue** | 3处 | 获取详情、保存、更新 |
| **CourseOutline.vue** | 6处 | 章节、课时的增删改查 |
| **CourseDetail.vue** | 3处 | 获取详情、大纲、购买 |
| **RechargeDialog.vue** | 1处 | 充值失败 |
| **Recharge.vue** | 1处 | 充值失败 |
| **register.vue** | 1处 | 注册失败 |
| **MyCourses.vue** | 1处 | 获取课程失败 |
| **Profile.vue** | 3处 | 获取信息、更新、修改密码 |

**总计修改：36处**

---

## API 返回格式

### 统一格式
```json
{
  "code": 0,           // 0=成功, 1=失败
  "msg": "操作成功",    // 提示消息（原 message）
  "data": {}           // 数据
}
```

### 示例

#### 成功响应
```json
{
  "code": 0,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGc...",
    "user": {
      "id": 1,
      "username": "admin"
    }
  }
}
```

#### 失败响应
```json
{
  "code": 1,
  "msg": "余额不足，当前余额: ¥0.00，需要: ¥129.00",
  "data": null
}
```

---

## 前端提示效果

### 自动提示
所有 API 错误会自动通过 ElMessage 显示：

```javascript
// 后端返回
{
  "code": 1,
  "msg": "余额不足，当前余额: ¥0.00，需要: ¥129.00"
}

// 前端自动显示
ElMessage.error("余额不足，当前余额: ¥0.00，需要: ¥129.00")
```

### 手动提示（页面内）
如果页面需要特殊处理错误，仍然可以访问 `error.msg`：

```javascript
try {
  await purchaseCourse(courseId)
} catch (error) {
  // 除了自动弹窗，还可以做其他处理
  console.error('购买失败:', error.msg)
  // 例如：跳转到充值页面
  if (error.msg.includes('余额不足')) {
    router.push('/recharge')
  }
}
```

---

## 验证测试

### 后端测试
```bash
# 重新编译
cd EduSphere
mvn clean compile

# 测试响应格式
curl http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"wrong"}'

# 应返回
{"code":1,"msg":"用户名或密码错误","data":null}
```

### 前端测试
1. 启动前端：`npm run dev`
2. 测试登录失败 → 应显示 ElMessage 错误提示
3. 测试余额不足购买 → 应显示完整的余额不足信息
4. 打开控制台 → 不应有 `undefined` 或 `null` 的错误

---

## 兼容性说明

### 向后不兼容
⚠️ **重要：** 此次修改是 **破坏性更新**，旧版本前端无法使用新版本后端。

如果有移动端或其他客户端，也需要同步修改：
- iOS/Android: `response.message` → `response.msg`
- 其他服务: 使用该 API 的地方都需要更新

### 数据库
✅ 无需修改数据库，仅影响 API 层

---

## 修改清单

### 后端文件（2个）
- ✅ Result.java
- ✅ ResponseUtils.java

### 前端文件（17个）
- ✅ request.js（核心）
- ✅ api.js
- ✅ auth.js
- ✅ CourseList.vue
- ✅ CourseDetail.vue
- ✅ CourseCreate.vue
- ✅ CourseEdit.vue
- ✅ CourseOutline.vue
- ✅ TeacherDashboard.vue
- ✅ AdminDashboard.vue
- ✅ LearningPage.vue
- ✅ login.vue
- ✅ register.vue
- ✅ Profile.vue
- ✅ MyCourses.vue
- ✅ Recharge.vue
- ✅ RechargeDialog.vue

---

## 注意事项

### 1. ElMessage 已全局引入
✅ request.js 已自动处理所有错误提示，页面无需重复 ElMessage.error

### 2. 成功提示仍需手动
```javascript
// 成功操作需要手动显示提示
const response = await createCourse(data)
ElMessage.success('课程创建成功')
```

### 3. JavaScript Error 不受影响
```javascript
try {
  // some code
} catch (err) {
  // JavaScript 原生错误仍使用 err.message
  console.error(err.message)  // 正确
}
```

---

## 回滚方案

如果需要回滚到 `message` 字段：

### 后端回滚
```bash
git checkout HEAD -- Result.java ResponseUtils.java
mvn clean compile
```

### 前端回滚
```bash
git checkout HEAD -- src/utils/request.js
# 批量替换其他文件
find src -name "*.vue" -o -name "*.js" | xargs sed -i 's/\.msg/\.message/g'
```

---

## 完成时间
**2025-11-26**

所有修改已完成并测试通过！✅
