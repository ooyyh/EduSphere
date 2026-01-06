/**
 * 日期格式化工具函数
 */

/**
 * 格式化日期时间为完整格式
 * @param {string|Date|number} dateInput - 日期输入（ISO字符串、Date对象或时间戳）
 * @returns {string} 格式化后的日期时间字符串 (YYYY-MM-DD HH:mm:ss)
 */
export function formatDateTime(dateInput) {
  if (!dateInput) return ''

  try {
    let date

    // 处理不同类型的输入
    if (typeof dateInput === 'string') {
      // 处理数组格式的日期 [2024, 11, 26, 10, 30, 45]
      if (dateInput.startsWith('[')) {
        try {
          const dateArray = JSON.parse(dateInput)
          if (Array.isArray(dateArray)) {
            // JavaScript月份是0-11，所以需要减1
            date = new Date(dateArray[0], dateArray[1] - 1, dateArray[2],
                           dateArray[3] || 0, dateArray[4] || 0, dateArray[5] || 0)
          }
        } catch (e) {
          console.error('解析数组日期失败:', e)
          date = new Date(dateInput)
        }
      } else {
        // ISO 8601 格式或其他字符串格式
        date = new Date(dateInput)
      }
    } else if (dateInput instanceof Date) {
      date = dateInput
    } else if (typeof dateInput === 'number') {
      date = new Date(dateInput)
    } else if (Array.isArray(dateInput)) {
      // 直接处理数组格式
      date = new Date(dateInput[0], dateInput[1] - 1, dateInput[2],
                     dateInput[3] || 0, dateInput[4] || 0, dateInput[5] || 0)
    } else {
      return '无效日期'
    }

    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      console.warn('无效的日期:', dateInput)
      return '无效日期'
    }

    // 使用 toLocaleString 格式化
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    })
  } catch (error) {
    console.error('日期格式化错误:', error, '原始数据:', dateInput)
    return '无效日期'
  }
}

/**
 * 格式化日期为日期格式（不含时间）
 * @param {string|Date|number} dateInput - 日期输入
 * @returns {string} 格式化后的日期字符串 (YYYY-MM-DD)
 */
export function formatDate(dateInput) {
  if (!dateInput) return ''

  try {
    let date

    if (typeof dateInput === 'string') {
      if (dateInput.startsWith('[')) {
        try {
          const dateArray = JSON.parse(dateInput)
          if (Array.isArray(dateArray)) {
            date = new Date(dateArray[0], dateArray[1] - 1, dateArray[2])
          }
        } catch (e) {
          date = new Date(dateInput)
        }
      } else {
        date = new Date(dateInput)
      }
    } else if (dateInput instanceof Date) {
      date = dateInput
    } else if (typeof dateInput === 'number') {
      date = new Date(dateInput)
    } else if (Array.isArray(dateInput)) {
      date = new Date(dateInput[0], dateInput[1] - 1, dateInput[2])
    } else {
      return '无效日期'
    }

    if (isNaN(date.getTime())) {
      return '无效日期'
    }

    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  } catch (error) {
    console.error('日期格式化错误:', error, '原始数据:', dateInput)
    return '无效日期'
  }
}

/**
 * 格式化相对时间（如：刚刚、5分钟前、3天前等）
 * @param {string|Date|number} dateInput - 日期输入
 * @returns {string} 相对时间字符串
 */
export function formatRelativeTime(dateInput) {
  if (!dateInput) return ''

  try {
    let date

    if (typeof dateInput === 'string') {
      if (dateInput.startsWith('[')) {
        const dateArray = JSON.parse(dateInput)
        if (Array.isArray(dateArray)) {
          date = new Date(dateArray[0], dateArray[1] - 1, dateArray[2],
                         dateArray[3] || 0, dateArray[4] || 0, dateArray[5] || 0)
        }
      } else {
        date = new Date(dateInput)
      }
    } else if (dateInput instanceof Date) {
      date = dateInput
    } else if (typeof dateInput === 'number') {
      date = new Date(dateInput)
    } else if (Array.isArray(dateInput)) {
      date = new Date(dateInput[0], dateInput[1] - 1, dateInput[2],
                     dateInput[3] || 0, dateInput[4] || 0, dateInput[5] || 0)
    } else {
      return '无效日期'
    }

    if (isNaN(date.getTime())) {
      return '无效日期'
    }

    const now = new Date()
    const diff = now - date
    const seconds = Math.floor(diff / 1000)
    const minutes = Math.floor(seconds / 60)
    const hours = Math.floor(minutes / 60)
    const days = Math.floor(hours / 24)

    if (seconds < 60) {
      return '刚刚'
    } else if (minutes < 60) {
      return `${minutes}分钟前`
    } else if (hours < 24) {
      return `${hours}小时前`
    } else if (days < 30) {
      return `${days}天前`
    } else {
      return formatDate(date)
    }
  } catch (error) {
    console.error('相对时间格式化错误:', error, '原始数据:', dateInput)
    return '无效日期'
  }
}

export default {
  formatDateTime,
  formatDate,
  formatRelativeTime
}
