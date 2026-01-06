<template>
  <div class="course-create">
    <Header />
    
    <main class="main-content">
      <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1>创建新课程</h1>
          <p>填写课程基本信息，开始您的创作之旅</p>
        </div>

        <!-- 课程表单 -->
        <div class="form-container">
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="120px"
            class="course-form"
          >
            <!-- 基本信息 -->
            <div class="form-section">
              <h3>基本信息</h3>
              
              <el-form-item label="课程标题" prop="title">
                <el-input
                  v-model="form.title"
                  placeholder="请输入课程标题"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="课程副标题" prop="subtitle">
                <el-input
                  v-model="form.subtitle"
                  placeholder="请输入课程副标题"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="课程描述" prop="description">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="6"
                  placeholder="请详细描述课程内容、学习目标等"
                  maxlength="1000"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="课程封面">
                <FileUpload v-model="form.coverImage" file-type="image" :max-size="2" />
              </el-form-item>
            </div>

            <!-- 课程设置 -->
            <div class="form-section">
              <h3>课程设置</h3>
              
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="课程分类" prop="categoryId">
                    <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                      <el-option
                        v-for="category in categories"
                        :key="category.id"
                        :label="category.name"
                        :value="category.id"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="难度等级" prop="level">
                    <el-select v-model="form.level" placeholder="请选择难度" style="width: 100%">
                      <el-option label="入门" value="beginner" />
                      <el-option label="进阶" value="intermediate" />
                      <el-option label="高级" value="advanced" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="课程时长" prop="duration">
                    <el-input
                      v-model="form.duration"
                      placeholder="例如：24小时"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="是否免费">
                    <el-switch
                      v-model="form.isFree"
                      active-text="免费"
                      inactive-text="付费"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20" v-if="!form.isFree">
                <el-col :span="12">
                  <el-form-item label="课程价格" prop="price">
                    <el-input-number
                      v-model="form.price"
                      :min="0"
                      :precision="2"
                      placeholder="0.00"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="原价">
                    <el-input-number
                      v-model="form.originalPrice"
                      :min="0"
                      :precision="2"
                      placeholder="0.00"
                      style="width: 100%"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="热门课程">
                    <el-switch
                      v-model="form.isHot"
                      active-text="是"
                      inactive-text="否"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="最新课程">
                    <el-switch
                      v-model="form.isNew"
                      active-text="是"
                      inactive-text="否"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <!-- 操作按钮 -->
            <div class="form-actions">
              <el-button size="large" @click="goBack">
                取消
              </el-button>
              <el-button size="large" @click="saveDraft" :loading="saving">
                保存草稿
              </el-button>
              <el-button type="primary" size="large" @click="createCourse" :loading="creating">
                创建课程
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import FileUpload from '@/components/common/FileUpload.vue'

const router = useRouter()
const authStore = useAuthStore()

// 表单数据
const form = reactive({
  title: '',
  subtitle: '',
  description: '',
  coverImage: '',
  categoryId: null,
  level: '',
  duration: '',
  isFree: false,
  price: 0,
  originalPrice: 0,
  isHot: false,
  isNew: true
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入课程标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  subtitle: [
    { required: true, message: '请输入课程副标题', trigger: 'blur' },
    { min: 2, max: 200, message: '副标题长度在 2 到 200 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入课程描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '描述长度在 10 到 1000 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择课程分类', trigger: 'change' }
  ],
  level: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入课程时长', trigger: 'blur' }
  ],
  price: [
    { 
      validator: (rule, value, callback) => {
        if (!form.isFree && (!value || value <= 0)) {
          callback(new Error('请输入有效的课程价格'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 响应式数据
const formRef = ref()
const categories = ref([])
const saving = ref(false)
const creating = ref(false)

// 获取分类列表
const loadCategories = async () => {
  try {
    const response = await request.get('/category/list')
    if (response.code === 0) {
      categories.value = response.data || []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 保存草稿
const saveDraft = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    
    const response = await request.post('/teacher/courses', {
      ...form,
      status: 'draft' // 草稿状态
    })
    
    if (response.code === 0) {
      ElMessage.success('草稿保存成功')
      router.push('/teacher')
    } else {
      ElMessage.error(response.msg || '保存草稿失败')
    }
  } catch (error) {
    if (error !== false) { // 不是验证错误
      ElMessage.error('保存草稿失败')
    }
  } finally {
    saving.value = false
  }
}

// 创建课程
const createCourse = async () => {
  try {
    await formRef.value.validate()
    creating.value = true
    
    const response = await request.post('/teacher/courses', {
      ...form,
      status: 'draft' // 默认草稿状态
    })
    
    if (response.code === 0) {
      ElMessage.success('课程创建成功，已保存为草稿')
      router.push('/teacher')
    } else {
      ElMessage.error(response.msg || '创建课程失败')
    }
  } catch (error) {
    if (error !== false) { // 不是验证错误
      ElMessage.error('创建课程失败')
    }
  } finally {
    creating.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 权限检查
const checkPermission = () => {
  if (!authStore.isLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/account/login')
    return false
  }
  
  if (authStore.user?.role !== 'teacher') {
    ElMessage.error('只有教师才能访问此页面')
    if (authStore.user?.role === 'admin') {
      router.push('/admin')
    } else {
      router.push('/')
    }
    return false
  }
  
  return true
}

onMounted(() => {
  if (checkPermission()) {
    loadCategories()
  }
})
</script>

<style scoped>
.course-create {
  min-height: 100vh;
  background: #f8f9fa;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  padding: 40px 0;
}

.page-header h1 {
  font-size: 2.5rem;
  margin-bottom: 16px;
  color: #333;
}

.page-header p {
  font-size: 1.1rem;
  color: #666;
}

.form-container {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 40px;
}

.form-section {
  margin-bottom: 40px;
}

.form-section h3 {
  font-size: 1.3rem;
  color: #333;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.course-form {
  max-width: 600px;
}

/* 表单操作 */
.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 32px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-container {
    padding: 24px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
}
</style>
