import request from './request'
import { ElMessage } from 'element-plus'

/**
 * API接口管理模块
 * 统一管理所有API接口和错误处理
 */

/**
 * 用户相关API
 */
export const api = {
  // 用户相关API
  user: {
    // 登录
    login: (data) => request.post('/user/login', data),

    // 注册
    register: (data) => request.post('/user/register', data),

    // 获取用户信息
    getProfile: () => request.get('/user/profile'),

    // 更新用户信息
    updateProfile: (data) => request.put('/user/profile', data),

    // 修改密码
    updatePassword: (data) => request.put('/user/password', data)
  },

  // 课程相关API
  course: {
    // 获取课程列表
    getList: (params) => request.get('/course/list', params),

    // 获取课程详情
    getDetail: (id) => request.get(`/course/${id}`),

    // 创建课程
    create: (data) => request.post('/course', data),

    // 更新课程
    update: (id, data) => request.put(`/course/${id}`, data),

    // 删除课程
    delete: (id) => request.delete(`/course/${id}`)
  },

  // 教师相关API
  teacher: {
    // 获取教师课程
    getCourses: () => request.get('/teacher/courses'),

    // 创建课程
    createCourse: (data) => request.post('/teacher/courses', data),

    // 更新课程
    updateCourse: (id, data) => request.put(`/teacher/courses/${id}`, data),

    // 删除课程
    deleteCourse: (id) => request.delete(`/teacher/courses/${id}`),

    // 发布课程
    publishCourse: (id) => request.put(`/teacher/courses/${id}/publish`),

    // 取消发布课程
    unpublishCourse: (id) => request.put(`/teacher/courses/${id}/unpublish`)
  },

  // 分类相关API
  category: {
    // 获取分类列表
    getList: () => request.get('/category/list')
  },

  // 购物车相关API
  cart: {
    // 获取购物车
    getCart: () => request.get('/cart'),

    // 添加到购物车
    addToCart: (courseId) => request.post(`/cart/add/${courseId}`),

    // 从购物车移除
    removeFromCart: (courseId) => request.delete(`/cart/remove/${courseId}`),

    // 清空购物车
    clearCart: () => request.delete('/cart/clear')
  },

  // 订单相关API
  order: {
    // 创建订单
    createOrder: (data) => request.post('/order/create', data),

    // 获取订单列表
    getOrders: () => request.get('/order/list'),

    // 获取订单详情
    getOrderDetail: (orderNo) => request.get(`/order/${orderNo}`),

    // 支付订单
    payOrder: (orderNo) => request.post(`/order/pay/${orderNo}`)
  },

  // 评价相关API
  review: {
    // 获取课程评价
    getCourseReviews: (courseId) => request.get(`/review/course/${courseId}`),

    // 添加评价
    addReview: (courseId, data) => request.post(`/review/course/${courseId}`, data)
  },

  // 充值相关API
  balance: {
    // 获取余额信息
    getBalance: () => request.get('/balance/info'),

    // 充值
    recharge: (data) => request.post('/balance/recharge', data),

    // 获取充值记录
    getRecords: () => request.get('/balance/records')
  }
}

/**
 * 统一处理API响应
 * @param {Promise} apiCall - API调用Promise
 * @param {object} options - 配置选项
 * @param {string} options.successMessage - 成功提示消息
 * @param {string} options.errorMessage - 错误提示消息
 * @param {boolean} options.showSuccess - 是否显示成功提示，默认false
 * @param {boolean} options.showError - 是否显示错误提示，默认true
 * @returns {Promise<{success: boolean, data?: any, message?: string}>}
 */
export const handleApiCall = async (apiCall, options = {}) => {
  const {
    successMessage,
    errorMessage = '操作失败',
    showSuccess = false,
    showError = true
  } = options

  try {
    const response = await apiCall

    // 处理成功响应
    if (response.code === 0) {
      if (showSuccess && successMessage) {
        ElMessage.success(successMessage)
      }
      return { success: true, data: response.data }
    } else {
      // 处理业务错误
      const message = response.msg || errorMessage
      if (showError) {
        ElMessage.error(message)
      }
      return { success: false, message }
    }
  } catch (error) {
    // 处理异常错误
    console.error('API调用失败:', error)
    const message = error.message || errorMessage
    if (showError) {
      ElMessage.error(message)
    }
    return { success: false, message }
  }
}

/**
 * 处理API响应的通用方法（向后兼容）
 * @deprecated 建议使用 handleApiCall 代替
 */
export const handleApiResponse = (response, successMessage, errorMessage) => {
  if (response.code === 0) {
    if (successMessage) {
      ElMessage.success(successMessage)
    }
    return { success: true, data: response.data }
  } else {
    ElMessage.error(response.msg || errorMessage || '操作失败')
    return { success: false, message: response.msg }
  }
}

/**
 * 处理API错误的通用方法（向后兼容）
 * @deprecated 建议使用 handleApiCall 代替
 */
export const handleApiError = (error, defaultMessage = '操作失败') => {
  console.error('API请求失败:', error)
  ElMessage.error(error.message || defaultMessage)
  return { success: false, message: error.message || defaultMessage }
}
