<template>
  <header class="header">
    <div class="header-container">
      <!-- Logo区域 -->
      <div class="logo-section">
        <router-link to="/" class="logo">
          <el-icon size="28" color="#409EFF"><School /></el-icon>
          <span class="logo-text">EduSphere</span>
        </router-link>
      </div>

      <!-- 导航菜单 -->
      <nav class="nav-menu">
        <el-menu
          :default-active="activeIndex"
          mode="horizontal"
          @select="handleSelect"
          background-color="transparent"
          text-color="#333"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/courses">课程浏览</el-menu-item>
          <el-sub-menu index="2">
            <template #title>分类</template>
            <el-menu-item index="/courses?category=programming">编程开发</el-menu-item>
            <el-menu-item index="/courses?category=design">设计创意</el-menu-item>
            <el-menu-item index="/courses?category=business">商业管理</el-menu-item>
            <el-menu-item index="/courses?category=language">语言学习</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </nav>

      <!-- 用户操作区域 -->
      <div class="user-section">
        <template v-if="isLoggedIn">
          <!-- 余额显示 -->
          <div class="balance-info">
            <el-tag type="success" size="small">
              <el-icon><Wallet /></el-icon>
              余额: ¥{{ userBalance.balance || '0.00' }}
            </el-tag>
            <el-button type="primary" size="small" @click="showRechargeDialog">
              <el-icon><Plus /></el-icon>
              充值
            </el-button>
          </div>

          <!-- 购物车图标 -->
          <div class="cart-icon" @click="goToCart">
            <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">
              <el-icon :size="24"><ShoppingCart /></el-icon>
            </el-badge>
          </div>

          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="avatarUrl">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ userInfo.username }}</span>
              <el-icon class="arrow-down"><ArrowDown /></el-icon>
            </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="my-courses">我的课程</el-dropdown-item>
            <el-dropdown-item command="my-orders">我的订单</el-dropdown-item>
            <el-dropdown-item
              v-if="authStore.user?.role === 'admin'"
              command="admin"
              divided
            >
              管理后台
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="text" @click="goToLogin">登录</el-button>
          <el-button type="primary" @click="goToRegister">注册</el-button>
        </template>
        
        <!-- 教师入口 -->
        <div class="teacher-entry" v-if="isLoggedIn && userInfo.role === 'teacher'">
          <el-button type="success" @click="goToTeacher">
            <el-icon><EditPen /></el-icon>
            教师工作台
          </el-button>
        </div>
        
        <!-- 管理员入口 -->
        <div class="admin-entry" v-if="isLoggedIn && userInfo.role === 'admin'">
          <el-button type="danger" @click="goToAdmin">
            <el-icon><Setting /></el-icon>
            管理后台
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 充值弹窗 -->
    <RechargeDialog 
      v-model="showRecharge" 
      @recharge-success="onRechargeSuccess"
    />
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { School, User, ArrowDown, EditPen, Wallet, Plus, Setting, ShoppingCart } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'
import RechargeDialog from './RechargeDialog.vue'
import { getImageUrl } from '@/utils/imageUtils'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const activeIndex = computed(() => route.path)
const userBalance = ref({})
const showRecharge = ref(false)
const cartCount = ref(0)

// 使用Pinia store中的状态
const isLoggedIn = computed(() => authStore.isLoggedIn)
const userInfo = computed(() => authStore.user)

// 计算处理后的头像URL
const avatarUrl = computed(() => {
  return userInfo.value?.avatar ? getImageUrl(userInfo.value.avatar) : ''
})

// 导航选择处理
const handleSelect = (key) => {
  router.push(key)
}

// 用户下拉菜单处理
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'my-courses':
      router.push('/my-courses')
      break
    case 'my-orders':
      router.push('/orders')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 登录跳转
const goToLogin = () => {
  router.push('/account/login')
}

// 注册跳转
const goToRegister = () => {
  router.push('/account/register')
}

// 跳转到教师工作台
const goToTeacher = () => {
  router.push('/teacher')
}

// 跳转到管理后台
const goToAdmin = () => {
  router.push('/admin')
}

// 显示充值弹窗
const showRechargeDialog = () => {
  showRecharge.value = true
}

// 充值成功回调
const onRechargeSuccess = () => {
  // 重新获取余额
  getUserBalance()
}

// 获取用户余额
const getUserBalance = async () => {
  if (!isLoggedIn.value) return

  try {
    const response = await request.get('/balance/info')
    if (response.code === 0) {
      userBalance.value = response.data
    }
  } catch (error) {
    console.error('获取余额失败:', error)
  }
}

// 获取购物车数量
const getCartCount = async () => {
  if (!isLoggedIn.value) return

  try {
    const response = await request.get('/cart')
    if (response.code === 0) {
      cartCount.value = response.data?.length || 0
    }
  } catch (error) {
    console.error('获取购物车数量失败:', error)
  }
}

// 跳转到购物车
const goToCart = () => {
  router.push('/cart')
}

// 退出登录
const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

onMounted(() => {
  // 初始化用户状态
  authStore.initUser()
  // 获取用户余额
  getUserBalance()
  // 获取购物车数量
  getCartCount()
})
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 64px;
}

.logo-section {
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #333;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  margin-left: 8px;
  color: #409EFF;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
}

.nav-menu .el-menu {
  border-bottom: none;
}

.user-section {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.balance-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.cart-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.cart-icon:hover {
  background-color: #f5f7fa;
}

.cart-icon .el-icon {
  color: #409EFF;
}

.teacher-entry {
  margin-left: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  font-size: 14px;
  color: #333;
}

.arrow-down {
  font-size: 12px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }

  .nav-menu {
    display: none;
  }

  .logo-text {
    display: none;
  }
}
</style>