<template>
  <div class="register-container">
    <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-position="top">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="registerForm.username" placeholder="请输入用户名">
          <template #prefix><el-icon><User /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="registerForm.email" placeholder="请输入邮箱">
          <template #prefix><el-icon><Message /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password>
          <template #prefix><el-icon><Lock /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password>
          <template #prefix><el-icon><Lock /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item label="注册身份" prop="role">
        <el-select v-model="registerForm.role" placeholder="请选择注册身份" style="width: 100%">
          <el-option 
            v-for="(label, value) in ROLE_NAMES" 
            :key="value" 
            :label="label" 
            :value="value"
            v-if="value !== 'admin'"
          />
        </el-select>
      </el-form-item>
      <!-- <el-form-item>
        <el-checkbox v-model="registerForm.agreement">我已阅读并同意<el-button type="text">服务条款</el-button>和<el-button type="text">隐私政策</el-button></el-checkbox>
      </el-form-item> -->
      <el-button type="primary" class="register-btn" @click="handleRegister" :loading="loading">注册</el-button>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Message, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ROLES, ROLE_NAMES } from '@/utils/constants'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const registerFormRef = ref(null)
const loading = ref(false)
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreement: false,
  role: 'student' // 默认角色：admin, teacher, student
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value.validateField('confirmPassword');
    }
    callback();
  }
};

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

const validateAgreement = (rule, value, callback) => {
  if (!value) {
        callback(new Error('请阅读并同意服务条款和隐私政策'));
      } else {
        callback();
      }
    };

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择注册身份', trigger: 'change' }
  ],
  agreement: [
    { validator: validateAgreement, trigger: 'change' }
  ]
};

const handleRegister = () => {
  if (!registerFormRef.value) return;
  
  registerFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      
      // 调用注册接口
      request.post('/user/register', {
        username: registerForm.username,
        password: registerForm.password,
        email: registerForm.email,
        role: registerForm.role
      })
      .then(res => {
        if (res.code === 0) {
          ElMessage.success('注册成功');
          // 注册成功后跳转到登录页
          router.push('/account/login');
        } else {
          ElMessage.error(res.msg || '注册失败');
        }
      })
      .catch(error => {
        ElMessage.error(error.message || '注册失败');
      })
      .finally(() => {
        loading.value = false;
      });
    } else {
      ElMessage.error('请正确填写注册信息');
      return false;
    }
  });
};
</script>

<style scoped>
.register-container {
  padding: 20px 0;
}

.register-btn {
  width: 100%;
  margin-top: 10px;
  height: 40px;
}
</style>