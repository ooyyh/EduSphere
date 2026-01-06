import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

/**
 * 用户认证Store
 * 管理用户登录状态、用户信息和认证相关操作
 */
export const useAuthStore = defineStore('auth', () => {
  // ==================== 状态定义 ====================

  /**
   * 当前登录用户信息
   * @type {Ref<Object|null>}
   */
  const user = ref(null)

  /**
   * JWT认证令牌
   * @type {Ref<string>}
   */
  const token = ref(localStorage.getItem('token') || '')

  /**
   * 是否已登录
   * @type {ComputedRef<boolean>}
   */
  const isLoggedIn = computed(() => !!token.value && !!user.value)

  // ==================== 登录相关 ====================

  /**
   * 用户登录
   * @param {Object} credentials - 登录凭证
   * @param {string} credentials.username - 用户名或邮箱
   * @param {string} credentials.password - 密码
   * @param {string} credentials.role - 登录角色
   * @returns {Promise<{success: boolean, data?: Object, message?: string}>}
   */
  const login = async (credentials) => {
    try {
      const response = await request.post('/user/login', credentials)

      if (response.code === 0) {
        // 保存token和用户信息
        token.value = response.data.token
        user.value = response.data.user
        localStorage.setItem('token', token.value)
        localStorage.setItem('user', JSON.stringify(user.value))

        return { success: true, data: response.data }
      } else {
        return { success: false, message: response.msg }
      }
    } catch (error) {
      console.error('登录失败:', error)
      return { success: false, message: '登录失败，请检查网络连接' }
    }
  }

  /**
   * 用户注册
   * @param {Object} userData - 注册信息
   * @param {string} userData.username - 用户名
   * @param {string} userData.email - 邮箱
   * @param {string} userData.password - 密码
   * @param {string} userData.role - 角色
   * @returns {Promise<{success: boolean, data?: Object, message?: string}>}
   */
  const register = async (userData) => {
    try {
      const response = await request.post('/user/register', userData)

      if (response.code === 0) {
        return { success: true, data: response.data }
      } else {
        return { success: false, message: response.msg }
      }
    } catch (error) {
      console.error('注册失败:', error)
      return { success: false, message: '注册失败，请检查网络连接' }
    }
  }

  /**
   * 用户登出
   * 清除所有认证信息
   */
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // ==================== 用户信息管理 ====================

  /**
   * 更新用户信息
   * @param {Object} userInfo - 要更新的用户信息
   */
  const updateUserInfo = (userInfo) => {
    if (user.value) {
      Object.assign(user.value, userInfo)
      localStorage.setItem('user', JSON.stringify(user.value))
    }
  }

  /**
   * 从localStorage初始化用户信息
   * 应用启动时调用
   */
  const initUser = () => {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        localStorage.removeItem('user')
      }
    }
  }

  /**
   * 检查登录状态
   * 验证token是否有效，并刷新用户信息
   * @returns {Promise<boolean>} 是否已登录
   */
  const checkAuth = async () => {
    if (!token.value) {
      return false
    }

    try {
      const response = await request.get('/user/profile')

      if (response.code === 0) {
        user.value = response.data
        localStorage.setItem('user', JSON.stringify(user.value))
        return true
      } else {
        // token无效，清除登录状态
        logout()
        return false
      }
    } catch (error) {
      console.error('检查登录状态失败:', error)
      logout()
      return false
    }
  }

  // ==================== 导出 ====================

  return {
    // 状态
    user,
    token,
    isLoggedIn,

    // 方法
    login,
    register,
    logout,
    updateUserInfo,
    initUser,
    checkAuth
  }
})
