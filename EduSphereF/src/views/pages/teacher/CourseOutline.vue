<template>
  <div class="course-outline">
    <Header />
    
    <main class="main-content">
      <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
          <div class="header-left">
            <el-button @click="goBack" icon="ArrowLeft" circle />
            <div class="title-info">
              <h1>{{ course?.title || '课程大纲' }}</h1>
              <p>管理课程章节和课时内容</p>
            </div>
          </div>
          <div class="header-actions">
            <el-button @click="addSection" type="primary" icon="Plus">
              添加章节
            </el-button>
          </div>
        </div>

        <!-- 课程大纲 -->
        <div class="outline-container" v-loading="loading">
          <div v-if="sections.length === 0" class="empty-state">
            <el-icon size="64"><Document /></el-icon>
            <h3>还没有章节内容</h3>
            <p>点击"添加章节"开始创建课程大纲</p>
            <el-button @click="addSection" type="primary" icon="Plus">
              添加第一个章节
            </el-button>
          </div>

          <div v-else class="sections-list">
            <div 
              v-for="(section, sectionIndex) in sections" 
              :key="section.id"
              class="section-item"
            >
              <!-- 章节头部 -->
              <div class="section-header">
                <div class="section-info">
                  <div class="section-title">
                    <el-icon><Folder /></el-icon>
                    <span>{{ section.title }}</span>
                    <el-tag v-if="section.description" size="small" type="info">
                      {{ section.description }}
                    </el-tag>
                  </div>
                  <div class="section-meta">
                    <span>{{ section.lessons?.length || 0 }} 个课时</span>
                    <span>{{ getSectionDuration(section) }}</span>
                  </div>
                </div>
                <div class="section-actions">
                  <el-button @click="addLesson(section)" size="small" icon="Plus">
                    添加课时
                  </el-button>
                  <el-button @click="editSection(section)" size="small" icon="Edit">
                    编辑
                  </el-button>
                  <el-button @click="deleteSection(section)" size="small" type="danger" icon="Delete">
                    删除
                  </el-button>
                </div>
              </div>

              <!-- 课时列表 -->
              <div class="lessons-list" v-if="section.lessons && section.lessons.length > 0">
                <div 
                  v-for="(lesson, lessonIndex) in section.lessons" 
                  :key="lesson.id"
                  class="lesson-item"
                >
                  <div class="lesson-info">
                    <div class="lesson-title">
                      <el-icon>
                        <VideoPlay v-if="lesson.type === 'video'" />
                        <Document v-else-if="lesson.type === 'document'" />
                        <QuestionFilled v-else />
                      </el-icon>
                      <span>{{ lesson.title }}</span>
                      <el-tag v-if="lesson.isFree" size="small" type="success">免费</el-tag>
                    </div>
                    <div class="lesson-meta">
                      <span>{{ lesson.type === 'video' ? '视频' : lesson.type === 'document' ? '文档' : '测验' }}</span>
                      <span v-if="lesson.duration">{{ lesson.duration }}</span>
                    </div>
                  </div>
                  <div class="lesson-actions">
                    <el-button @click="editLesson(lesson)" size="small" icon="Edit">
                      编辑
                    </el-button>
                    <el-button @click="deleteLesson(lesson)" size="small" type="danger" icon="Delete">
                      删除
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 空课时状态 -->
              <div v-else class="empty-lessons">
                <p>该章节还没有课时</p>
                <el-button @click="addLesson(section)" size="small" type="primary" icon="Plus">
                  添加第一个课时
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 章节编辑对话框 -->
    <el-dialog
      v-model="sectionDialogVisible"
      :title="editingSection ? '编辑章节' : '添加章节'"
      width="600px"
    >
      <el-form
        ref="sectionFormRef"
        :model="sectionForm"
        :rules="sectionRules"
        label-width="80px"
      >
        <el-form-item label="章节标题" prop="title">
          <el-input
            v-model="sectionForm.title"
            placeholder="请输入章节标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="章节描述" prop="description">
          <el-input
            v-model="sectionForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入章节描述（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sectionDialogVisible = false">取消</el-button>
        <el-button @click="saveSection" type="primary" :loading="saving">
          {{ editingSection ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 课时编辑对话框 -->
    <el-dialog
      v-model="lessonDialogVisible"
      :title="editingLesson ? '编辑课时' : '添加课时'"
      width="600px"
    >
      <el-form
        ref="lessonFormRef"
        :model="lessonForm"
        :rules="lessonRules"
        label-width="80px"
      >
        <el-form-item label="课时标题" prop="title">
          <el-input
            v-model="lessonForm.title"
            placeholder="请输入课时标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="课时描述" prop="description">
          <el-input
            v-model="lessonForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课时描述（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="课时类型" prop="type">
          <el-select v-model="lessonForm.type" placeholder="请选择课时类型">
            <el-option label="视频" value="video" />
            <el-option label="文档" value="document" />
            <el-option label="测验" value="quiz" />
          </el-select>
        </el-form-item>
        <el-form-item label="课时时长" prop="duration">
          <el-input
            v-model="lessonForm.duration"
            placeholder="如：10分钟"
            maxlength="20"
          />
        </el-form-item>
        <el-form-item
          v-if="lessonForm.type === 'video'"
          label="视频"
          prop="content"
        >
          <FileUpload v-model="lessonForm.content" file-type="video" :max-size="100" />
        </el-form-item>
        <el-form-item
          v-else-if="lessonForm.type === 'document'"
          label="文档"
          prop="content"
        >
          <FileUpload v-model="lessonForm.content" file-type="document" :max-size="50" />
        </el-form-item>
        <el-form-item
          v-else
          label="测验内容"
          prop="content"
        >
          <el-input
            v-model="lessonForm.content"
            type="textarea"
            :rows="3"
            placeholder="请输入测验内容"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="是否免费" prop="isFree">
          <el-switch v-model="lessonForm.isFree" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="lessonDialogVisible = false">取消</el-button>
        <el-button @click="saveLesson" type="primary" :loading="saving">
          {{ editingLesson ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus, Edit, Delete, Folder, VideoPlay, Document, QuestionFilled } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import FileUpload from '@/components/common/FileUpload.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const course = ref(null)
const sections = ref([])

// 对话框状态
const sectionDialogVisible = ref(false)
const lessonDialogVisible = ref(false)
const editingSection = ref(null)
const editingLesson = ref(null)

// 表单引用
const sectionFormRef = ref()
const lessonFormRef = ref()

// 章节表单
const sectionForm = reactive({
  title: '',
  description: ''
})

// 课时表单
const lessonForm = reactive({
  title: '',
  description: '',
  type: 'video',
  duration: '',
  content: '',
  isFree: false
})

// 表单验证规则
const sectionRules = {
  title: [
    { required: true, message: '请输入章节标题', trigger: 'blur' },
    { min: 1, max: 200, message: '章节标题长度在1到200个字符', trigger: 'blur' }
  ]
}

const lessonRules = {
  title: [
    { required: true, message: '请输入课时标题', trigger: 'blur' },
    { min: 1, max: 200, message: '课时标题长度在1到200个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择课时类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ]
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

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 获取课程详情
const loadCourse = async () => {
  try {
    loading.value = true
    const courseId = route.params.id
    const response = await request.get(`/course/${courseId}`)
    
    if (response.code === 0) {
      course.value = response.data
    } else {
      ElMessage.error(response.msg || '获取课程详情失败')
    }
  } catch (error) {
    ElMessage.error('获取课程详情失败')
  } finally {
    loading.value = false
  }
}

// 获取课程章节
const loadSections = async () => {
  try {
    const courseId = route.params.id
    const response = await request.get(`/teacher/courses/${courseId}/sections`)
    
    if (response.code === 0) {
      sections.value = response.data || []
      
      // 为每个章节加载课时
      for (let section of sections.value) {
        await loadSectionLessons(section)
      }
    } else {
      ElMessage.error(response.msg || '获取章节列表失败')
    }
  } catch (error) {
    ElMessage.error('获取章节列表失败')
  }
}

// 获取章节课时
const loadSectionLessons = async (section) => {
  try {
    const response = await request.get(`/teacher/sections/${section.id}/lessons`)
    
    if (response.code === 0) {
      section.lessons = response.data || []
    }
  } catch (error) {
    console.error('获取课时列表失败:', error)
    section.lessons = []
  }
}

// 计算章节总时长
const getSectionDuration = (section) => {
  if (!section.lessons || section.lessons.length === 0) {
    return '0分钟'
  }
  
  let totalMinutes = 0
  section.lessons.forEach(lesson => {
    if (lesson.duration) {
      const match = lesson.duration.match(/(\d+)/)
      if (match) {
        totalMinutes += parseInt(match[1])
      }
    }
  })
  
  return totalMinutes > 0 ? `${totalMinutes}分钟` : '0分钟'
}

// 添加章节
const addSection = () => {
  editingSection.value = null
  sectionForm.title = ''
  sectionForm.description = ''
  sectionDialogVisible.value = true
}

// 编辑章节
const editSection = (section) => {
  editingSection.value = section
  sectionForm.title = section.title
  sectionForm.description = section.description || ''
  sectionDialogVisible.value = true
}

// 保存章节
const saveSection = async () => {
  try {
    await sectionFormRef.value.validate()
    saving.value = true
    
    const courseId = route.params.id
    const data = {
      title: sectionForm.title,
      description: sectionForm.description
    }
    
    let response
    if (editingSection.value) {
      // 更新章节
      response = await request.put(`/teacher/sections/${editingSection.value.id}`, data)
    } else {
      // 创建章节
      response = await request.post(`/teacher/courses/${courseId}/sections`, data)
    }
    
    if (response.code === 0) {
      ElMessage.success(editingSection.value ? '章节更新成功' : '章节创建成功')
      sectionDialogVisible.value = false
      await loadSections()
    } else {
      ElMessage.error(response.msg || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 删除章节
const deleteSection = async (section) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除章节"${section.title}"吗？删除后该章节下的所有课时也会被删除。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await request.delete(`/teacher/sections/${section.id}`)

    if (response.code === 0) {
      ElMessage.success('章节删除成功')
      await loadSections()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 添加课时
const addLesson = (section) => {
  editingLesson.value = null
  lessonForm.title = ''
  lessonForm.description = ''
  lessonForm.type = 'video'
  lessonForm.duration = ''
  lessonForm.content = ''
  lessonForm.isFree = false
  lessonForm.sectionId = section.id
  lessonDialogVisible.value = true
}

// 编辑课时
const editLesson = (lesson) => {
  editingLesson.value = lesson
  lessonForm.title = lesson.title
  lessonForm.description = lesson.description || ''
  lessonForm.type = lesson.type
  lessonForm.duration = lesson.duration || ''
  lessonForm.content = lesson.videoUrl || lesson.documentUrl || ''
  lessonForm.isFree = lesson.isFree
  lessonForm.sectionId = lesson.sectionId
  lessonDialogVisible.value = true
}

// 保存课时
const saveLesson = async () => {
  try {
    await lessonFormRef.value.validate()
    saving.value = true
    
    const data = {
      title: lessonForm.title,
      description: lessonForm.description,
      type: lessonForm.type,
      duration: lessonForm.duration,
      content: lessonForm.content,
      isFree: lessonForm.isFree
    }
    
    let response
    if (editingLesson.value) {
      // 更新课时
      response = await request.put(`/teacher/lessons/${editingLesson.value.id}`, data)
    } else {
      // 创建课时
      response = await request.post(`/teacher/sections/${lessonForm.sectionId}/lessons`, data)
    }
    
    if (response.code === 0) {
      ElMessage.success(editingLesson.value ? '课时更新成功' : '课时创建成功')
      lessonDialogVisible.value = false
      await loadSections()
    } else {
      ElMessage.error(response.msg || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 删除课时
const deleteLesson = async (lesson) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课时"${lesson.title}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await request.delete(`/teacher/lessons/${lesson.id}`)
    
    if (response.code === 0) {
      ElMessage.success('课时删除成功')
      await loadSections()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 页面初始化
onMounted(() => {
  if (checkPermission()) {
    loadCourse()
    loadSections()
  }
})
</script>

<style scoped>
.course-outline {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.main-content {
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.title-info h1 {
  margin: 0 0 5px 0;
  font-size: 24px;
  color: #303133;
}

.title-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.outline-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-state .el-icon {
  margin-bottom: 20px;
  color: #c0c4cc;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #606266;
}

.empty-state p {
  margin: 0 0 20px 0;
  font-size: 14px;
}

.sections-list {
  padding: 20px;
}

.section-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}

.section-info {
  flex: 1;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.section-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

.section-actions {
  display: flex;
  gap: 8px;
}

.lessons-list {
  padding: 0;
}

.lesson-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.lesson-item:last-child {
  border-bottom: none;
}

.lesson-info {
  flex: 1;
}

.lesson-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
  font-size: 14px;
  color: #303133;
}

.lesson-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

.lesson-actions {
  display: flex;
  gap: 8px;
}

.empty-lessons {
  text-align: center;
  padding: 30px 20px;
  color: #909399;
  background: #fafafa;
}

.empty-lessons p {
  margin: 0 0 15px 0;
  font-size: 14px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .lesson-item {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>
