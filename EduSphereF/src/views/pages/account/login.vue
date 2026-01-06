<template>
  <div class="login-container">
    <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-position="top">
      <el-form-item label="用户名/邮箱" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入用户名或邮箱">
          <template #prefix><el-icon><User /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password></el-input>
      </el-form-item>
      <el-form-item label="登录身份" prop="role">
        <el-select v-model="loginForm.role" placeholder="请选择登录身份" style="width: 100%">
          <el-option 
            v-for="(label, value) in ROLE_NAMES" 
            :key="value" 
            :label="label" 
            :value="value" 
          />
        </el-select>
      </el-form-item>
      <div class="remember-forgot">
        <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
        <el-button type="text" class="forgot-btn">忘记密码?</el-button>
      </div>
      <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">登录</el-button>
      <!-- <div class="other-login">
        <p>其他登录方式</p>
        <div class="social-icons">
          <el-button circle><el-icon><ChatDotRound /></el-icon></el-button>
          <el-button circle><el-icon><Share /></el-icon></el-button>
          <el-button circle><el-icon><Connection /></el-icon></el-button>
        </div>
      </div> -->
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, ChatDotRound, Share, Connection } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { api, handleApiResponse, handleApiError } from '@/utils/api'
import { validationRules } from '@/utils/validation'
import { useAuthStore } from '@/stores/auth'
import { ROLES, ROLE_NAMES } from '@/utils/constants'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const loginForm = reactive({
  username: '',
  password: '',
  remember: false,
  role: 'student' // 默认角色：admin, teacher, student
})

const rules = {
  username: validationRules.username,
  password: validationRules.password,
  role: validationRules.role
}

const authStore = useAuthStore()

const handleLogin = async () => {
  if (!loginFormRef.value) return;
  
  try {
    await loginFormRef.value.validate()
    loading.value = true;
    
    const result = await authStore.login({
      username: loginForm.username,
      password: loginForm.password,
      role: loginForm.role
    })
    
    if (result.success) {
      ElMessage.success('登录成功');
      // 根据用户角色跳转到不同页面
      if (result.data.user.role === 'admin') {
        router.push('/admin');
      } else if (result.data.user.role === 'teacher') {
        router.push('/teacher');
      } else {
        router.push('/');
      }
    } else {
      ElMessage.error(result.message || '登录失败');
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('请正确填写登录信息');
    }
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-container {
  padding: 20px 0;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  height: 40px;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.forgot-btn {
  padding: 0;
  font-size: 14px;
}

.other-login {
  margin-top: 30px;
  text-align: center;
}

.other-login p {
  color: #909399;
  font-size: 14px;
  margin-bottom: 15px;
  position: relative;
}

.other-login p::before,
.other-login p::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 60px;
  height: 1px;
  background-color: #e4e7ed;
}

.other-login p::before {
  left: 30px;
}

.other-login p::after {
  right: 30px;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>