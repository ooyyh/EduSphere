// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * HTTP请求工具类
 * 封装了axios实例，提供统一的请求和响应拦截器
 */

// 创建 Axios 实例
const service = axios.create({
  baseURL: '/api', // 使用代理，开发环境会自动转发到后端
  timeout: 10000, // 请求超时时间 10 秒
})

/**
 * 请求拦截器
 * 在请求发送前添加认证token和用户ID
 */
service.interceptors.request.use(
  config => {
    // 从localStorage获取token并添加到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 从localStorage获取用户信息并添加用户ID到请求头
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        if (user && user.id) {
          config.headers['X-User-Id'] = user.id
        }
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }

    return config
  },
  error => {
    console.error('请求配置错误:', error)
    ElMessage.error('请求配置错误')
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 统一处理响应数据和错误
 */
service.interceptors.response.use(
  response => {
    const res = response.data
    // 后端统一使用 code: 0 表示成功，非0表示失败
    if (res.code && res.code !== 0) {
      // 显示错误提示
      ElMessage.error(res.msg || '操作失败')
      return Promise.reject(res)
    }
    return res
  },
  error => {
    // 处理HTTP错误状态
    if (error.response) {
      const status = error.response.status
      const msg = error.response.data?.msg || error.message
      let errorMsg = ''

      switch (status) {
        case 401:
          errorMsg = '认证失败，请重新登录'
          // 可以在这里触发登出操作
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          window.location.href = '/#/login'
          break
        case 403:
          errorMsg = '权限不足'
          break
        case 404:
          errorMsg = '请求的资源不存在'
          break
        case 500:
          errorMsg = '服务器错误'
          break
        default:
          errorMsg = msg || '请求失败'
      }

      ElMessage.error(errorMsg)
      console.error('HTTP错误:', status, errorMsg)
    } else if (error.request) {
      ElMessage.error('网络错误，无法连接到服务器')
      console.error('网络错误: 无法连接到服务器')
    } else {
      ElMessage.error('请求配置错误')
      console.error('请求配置错误:', error.message)
    }

    return Promise.reject(error)
  }
)

/**
 * 封装请求方法
 */
const request = {
  /**
   * GET请求
   * @param {string} url - 请求URL
   * @param {object} params - URL参数
   * @param {object} config - axios配置
   * @returns {Promise} 返回Promise对象
   */
  get(url, params = {}, config = {}) {
    return service.get(url, { params, ...config })
  },

  /**
   * POST请求
   * @param {string} url - 请求URL
   * @param {object} data - 请求体数据
   * @param {object} config - axios配置
   * @returns {Promise} 返回Promise对象
   */
  post(url, data = {}, config = {}) {
    return service.post(url, data, config)
  },

  /**
   * PUT请求
   * @param {string} url - 请求URL
   * @param {object} data - 请求体数据
   * @param {object} config - axios配置
   * @returns {Promise} 返回Promise对象
   */
  put(url, data = {}, config = {}) {
    return service.put(url, data, config)
  },

  /**
   * DELETE请求
   * @param {string} url - 请求URL
   * @param {object} params - URL参数
   * @param {object} config - axios配置
   * @returns {Promise} 返回Promise对象
   */
  delete(url, params = {}, config = {}) {
    return service.delete(url, { params, ...config })
  },
}

export default request
