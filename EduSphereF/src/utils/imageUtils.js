/**
 * 图片处理工具类
 * 用于处理上传图片的URL路径
 */

/**
 * 获取完整的图片URL
 * @param {string} url - 原始URL
 * @returns {string} 完整的URL
 */
export const getImageUrl = (url) => {
  if (!url) return ''
  
  // 如果是完整的HTTP/HTTPS URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  
  // 如果是本地上传的文件路径，添加API前缀以通过代理访问
  if (url.startsWith('/uploads/')) {
    return '/api' + url
  }
  
  // 其他情况直接返回
  return url
}