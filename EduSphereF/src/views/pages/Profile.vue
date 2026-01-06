<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>个人中心</h1>
      <p>管理您的个人信息和账户设置</p>
    </div>

    <div class="profile-content">
      <el-card class="profile-card">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
          </div>
        </template>

        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          label-width="100px"
          class="profile-form"
        >
          <!-- 头像上传 -->
          <el-form-item label="头像">
            <FileUpload v-model="profileForm.avatar" file-type="image" :max-size="2" />
          </el-form-item>

          <!-- 用户名 -->
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="profileForm.username"
              placeholder="请输入用户名"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>

          <!-- 邮箱 -->
          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="profileForm.email"
              placeholder="请输入邮箱"
              type="email"
            />
          </el-form-item>

          <!-- 角色（只读） -->
          <el-form-item label="角色">
            <el-tag :type="getRoleType(profileForm.role)">
              {{ getRoleText(profileForm.role) }}
            </el-tag>
          </el-form-item>

          <!-- 注册时间（只读） -->
          <el-form-item label="注册时间">
            <span class="readonly-text">{{ formatDateTime(profileForm.createdAt) }}</span>
          </el-form-item>

          <!-- 操作按钮 -->
          <el-form-item>
            <el-button type="primary" @click="updateProfile" :loading="updating">
              <el-icon><Check /></el-icon>
              保存修改
            </el-button>
            <el-button @click="resetForm">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 修改密码 -->
      <el-card class="password-card">
        <template #header>
          <div class="card-header">
            <span>修改密码</span>
          </div>
        </template>

        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
          class="password-form"
        >
          <!-- 旧密码 -->
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入旧密码"
              show-password
            />
          </el-form-item>

          <!-- 新密码 -->
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>

          <!-- 确认密码 -->
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>

          <!-- 操作按钮 -->
          <el-form-item>
            <el-button type="primary" @click="updatePassword" :loading="updatingPassword">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-button>
            <el-button @click="resetPasswordForm">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Refresh, Lock } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import FileUpload from '@/components/common/FileUpload.vue'
import { formatDateTime } from '@/utils/dateFormat'

const authStore = useAuthStore()

// 表单引用
const profileFormRef = ref()
const passwordFormRef = ref()

// 加载状态
const updating = ref(false)
const updatingPassword = ref(false)

// 个人信息表单
const profileForm = reactive({
  id: null,
  username: '',
  email: '',
  avatar: '',
  role: '',
  createdAt: null
})

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取角色类型
const getRoleType = (role) => {
  const roleMap = {
    admin: 'danger',
    teacher: 'success',
    student: 'primary'
  }
  return roleMap[role] || 'info'
}

// 获取角色文本
const getRoleText = (role) => {
  const roleMap = {
    admin: '管理员',
    teacher: '教师',
    student: '学生'
  }
  return roleMap[role] || '未知'
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await request.get('/user/profile')
    if (response.code === 0) {
      Object.assign(profileForm, response.data)
    } else {
      ElMessage.error(response.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 更新个人信息
const updateProfile = async () => {
  try {
    await profileFormRef.value.validate()
    updating.value = true

    const response = await request.put('/user/profile', {
      username: profileForm.username,
      email: profileForm.email,
      avatar: profileForm.avatar
    })

    if (response.code === 0) {
      ElMessage.success('个人信息更新成功')
      // 更新本地存储的用户信息
      authStore.updateUserInfo({
        username: profileForm.username,
        email: profileForm.email,
        avatar: profileForm.avatar
      })
    } else {
      ElMessage.error(response.msg || '更新失败')
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('更新失败')
    }
  } finally {
    updating.value = false
  }
}

// 更新密码
const updatePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    updatingPassword.value = true

    const response = await request.put('/user/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    if (response.code === 0) {
      ElMessage.success('密码修改成功')
      resetPasswordForm()
    } else {
      ElMessage.error(response.msg || '密码修改失败')
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('密码修改失败')
    }
  } finally {
    updatingPassword.value = false
  }
}

// 重置个人信息表单
const resetForm = () => {
  loadUserInfo()
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordFormRef.value?.resetFields()
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

// 组件挂载时加载用户信息
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  text-align: center;
  margin-bottom: 30px;
}

.profile-header h1 {
  color: #303133;
  margin-bottom: 10px;
}

.profile-header p {
  color: #909399;
  font-size: 14px;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-card,
.password-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.profile-form,
.password-form {
  max-width: 500px;
}

.readonly-text {
  color: #909399;
  font-size: 14px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-button) {
  margin-right: 10px;
}
</style>
