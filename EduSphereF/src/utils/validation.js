/**
 * 通用表单验证规则
 */
export const validationRules = {
  // 用户名验证
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  
  // 密码验证
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  
  // 邮箱验证
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  
  // 角色验证
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  
  // 课程标题验证
  title: [
    { required: true, message: '请输入课程标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  
  // 课程描述验证
  description: [
    { required: true, message: '请输入课程描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '描述长度在 10 到 1000 个字符', trigger: 'blur' }
  ],
  
  // 价格验证
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能小于0', trigger: 'blur' }
  ]
}

/**
 * 确认密码验证
 */
export const confirmPasswordRule = (passwordField) => ({
  validator: (rule, value, callback) => {
    if (value !== passwordField.value) {
      callback(new Error('两次输入密码不一致'))
    } else {
      callback()
    }
  },
  trigger: 'blur'
})

/**
 * 文件上传验证
 */
export const fileUploadRules = {
  // 图片上传验证
  image: (file) => {
    const isImage = file.type.startsWith('image/')
    const isLt2M = file.size / 1024 / 1024 < 2
    
    if (!isImage) {
      return '只能上传图片文件!'
    }
    if (!isLt2M) {
      return '图片大小不能超过 2MB!'
    }
    return true
  }
}
