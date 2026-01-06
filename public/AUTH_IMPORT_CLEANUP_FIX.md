# Auth导入清理修复

## 🚨 问题描述

前端出现404错误：
```
GET http://localhost:5173/src/utils/auth net::ERR_ABORTED 404 (Not Found)
```

## 🔍 问题分析

### 根本原因
在代码优化过程中，我们删除了`@/utils/auth.js`文件，但前端代码中还有多个地方在引用这个文件，导致404错误。

### 问题文件
- `Header.vue`
- `App.vue`
- `TeacherDashboard.vue`
- `CourseCreate.vue`
- `CourseEdit.vue`
- `CourseOutline.vue`
- `register.vue`

## 🛠️ 修复方案

### 1. 统一使用Pinia store

#### 修改前
```javascript
import { isLoggedIn, userInfo, initAuth, logout } from '@/utils/auth'

// 使用
if (isLoggedIn.value) { ... }
if (userInfo.value.role === 'teacher') { ... }
```

#### 修改后
```javascript
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 使用
if (authStore.isLoggedIn) { ... }
if (authStore.user?.role === 'teacher') { ... }
```

### 2. 修复所有引用文件

#### Header.vue
```javascript
// 修改前
import { isLoggedIn, userInfo, initAuth, logout } from '@/utils/auth'

// 修改后
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const isLoggedIn = computed(() => authStore.isLoggedIn)
const userInfo = computed(() => authStore.user)
```

#### App.vue
```javascript
// 修改前
import { initAuth } from '@/utils/auth'
onMounted(() => {
  initAuth()
})

// 修改后
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()
onMounted(() => {
  authStore.initUser()
})
```

#### 教师页面
```javascript
// 修改前
import { isLoggedIn, userInfo } from '@/utils/auth'

// 修改后
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()

// 权限检查
const checkPermission = () => {
  if (!authStore.isLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/account/login')
    return false
  }
  
  if (authStore.user?.role !== 'teacher' && authStore.user?.role !== 'admin') {
    ElMessage.error('只有教师才能访问此页面')
    router.push('/')
    return false
  }
  
  return true
}
```

## 🚀 修复效果

### 1. 404错误解决
- 所有文件不再引用已删除的`@/utils/auth`
- 统一使用Pinia store进行状态管理
- 页面可以正常加载

### 2. 状态管理统一
- 所有组件使用同一个状态源
- 状态更新自动同步到所有组件
- 减少状态不一致问题

### 3. 代码更清晰
- 使用Vue 3的Composition API
- 响应式状态管理
- 更好的类型支持

## 🔧 技术细节

### 1. Pinia store使用
```javascript
// 在组件中使用
const authStore = useAuthStore()

// 访问状态
authStore.isLoggedIn
authStore.user
authStore.token

// 调用方法
authStore.login(credentials)
authStore.logout()
authStore.updateUserInfo(userInfo)
```

### 2. 响应式绑定
```javascript
// 使用computed属性确保响应式
const isLoggedIn = computed(() => authStore.isLoggedIn)
const userInfo = computed(() => authStore.user)
```

### 3. 权限检查
```javascript
// 统一的权限检查逻辑
const checkPermission = () => {
  if (!authStore.isLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/account/login')
    return false
  }
  
  if (authStore.user?.role !== 'teacher' && authStore.user?.role !== 'admin') {
    ElMessage.error('只有教师才能访问此页面')
    router.push('/')
    return false
  }
  
  return true
}
```

## ⚠️ 注意事项

### 1. 状态同步
- 确保所有组件使用同一个store实例
- 状态更新后自动同步到所有组件
- 避免直接修改store状态

### 2. 生命周期
- 在组件挂载时初始化用户状态
- 在组件卸载时清理状态
- 处理页面刷新时的状态恢复

### 3. 错误处理
- 处理网络请求失败
- 处理token过期
- 提供友好的错误提示

## 🔄 后续优化

### 1. 状态持久化
```javascript
// 自动保存到localStorage
const authStore = useAuthStore()
authStore.$subscribe((mutation, state) => {
  localStorage.setItem('auth', JSON.stringify(state))
})
```

### 2. 状态验证
```javascript
// 验证token有效性
const validateToken = async () => {
  try {
    const response = await request.get('/user/profile')
    return response.code === 0
  } catch (error) {
    return false
  }
}
```

### 3. 自动登出
```javascript
// 监听token过期
const checkTokenExpiry = () => {
  if (authStore.token) {
    const decoded = jwt.decode(authStore.token)
    if (decoded.exp < Date.now() / 1000) {
      authStore.logout()
    }
  }
}
```

现在所有页面都应该可以正常加载了！🎉
