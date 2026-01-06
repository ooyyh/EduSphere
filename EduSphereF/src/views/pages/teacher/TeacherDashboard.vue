<!--
  教师工作台页面

  功能说明：
  - 展示教师的课程统计信息（总课程数、学员数、评分、浏览量）
  - 显示教师创建的所有课程列表
  - 支持课程筛选（按状态）
  - 提供课程管理操作（编辑、发布、预览、删除等）
  - 支持创建新课程和管理课程大纲

  作者：EduSphere团队
  最后更新：2025-11-26
-->
<template>
  <div class="teacher-dashboard">
    <!-- 顶部导航栏 -->
    <Header />
    
    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1>教师工作台</h1>
          <p>管理您的课程，创作优质内容</p>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon size="24"><VideoPlay /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalCourses || 0 }}</div>
              <div class="stat-label">总课程数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon size="24"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalStudents || 0 }}</div>
              <div class="stat-label">学员总数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon size="24"><Star /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.averageRating || 0 }}</div>
              <div class="stat-label">平均评分</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon size="24"><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalViews || 0 }}</div>
              <div class="stat-label">总浏览量</div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-bar">
          <el-button type="primary" size="large" @click="createCourse">
            <el-icon><Plus /></el-icon>
            创建新课程
          </el-button>
          <el-button size="large" @click="refreshCourses">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>

        <!-- 课程列表 -->
        <div class="courses-section">
          <div class="section-header">
            <h2>我的课程</h2>
            <div class="filter-options">
              <el-select v-model="statusFilter" placeholder="筛选状态" @change="filterCourses">
                <el-option label="全部" value="" />
                <el-option label="草稿" value="0" />
                <el-option label="已发布" value="1" />
              </el-select>
            </div>
          </div>

          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="4" animated />
          </div>

          <div v-else-if="courses.length === 0" class="empty-state">
            <el-empty description="还没有创建任何课程">
              <el-button type="primary" @click="createCourse">创建第一个课程</el-button>
            </el-empty>
          </div>

          <div v-else class="courses-grid">
            <div 
              v-for="course in filteredCourses" 
              :key="course.id"
              class="course-card"
            >
              <div class="course-image">
                <el-image
                  :src="getImageUrl(course.coverImage) || 'https://picsum.photos/300/200?random=' + course.id"
                  alt="课程封面"
                  fit="cover"
                  class="course-img"
                />
                <div class="course-status">
                  <el-tag :type="getStatusType(course.status)">
                    {{ getStatusText(course.status) }}
                  </el-tag>
                </div>
              </div>
              
              <div class="course-info">
                <h3 class="course-title">{{ course.title }}</h3>
                <p class="course-subtitle">{{ course.subtitle }}</p>
                
                <div class="course-meta">
                  <div class="meta-item">
                    <el-icon><User /></el-icon>
                    <span>{{ course.studentCount || 0 }} 学员</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Star /></el-icon>
                    <span>{{ course.rating || 0 }} 分</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Clock /></el-icon>
                    <span>{{ course.duration || '0小时' }}</span>
                  </div>
                </div>

                <div class="course-actions">
                  <el-button size="small" @click="editCourse(course.id)">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  <el-button size="small" @click="manageOutline(course.id)" type="primary">
                    <el-icon><Document /></el-icon>
                    大纲
                  </el-button>
                  <el-button 
                    v-if="course.status === 'draft'" 
                    size="small" 
                    type="success" 
                    @click="handleCourseAction({action: 'publish', course: course})"
                  >
                    <el-icon><Upload /></el-icon>
                    发布
                  </el-button>
                  <el-button size="small" @click="viewCourse(course.id)">
                    <el-icon><View /></el-icon>
                    预览
                  </el-button>
                  <el-dropdown @command="handleCourseAction">
                    <el-button size="small">
                      更多<el-icon><ArrowDown /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item :command="{action: 'stats', course: course}">
                          查看统计
                        </el-dropdown-item>
                        <el-dropdown-item :command="{action: 'delete', course: course}" divided>
                          删除课程
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Header from '@/components/Header.vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import { getImageUrl } from '@/utils/imageUtils'
import { 
  VideoPlay, User, Star, TrendCharts, Plus, Refresh, 
  Edit, View, ArrowDown, Clock, Upload, Document
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const courses = ref([])
const statusFilter = ref('')
const stats = ref({
  totalCourses: 0,
  totalStudents: 0,
  averageRating: 0,
  totalViews: 0
})

// 计算属性
const filteredCourses = computed(() => {
  if (!statusFilter.value) {
    return courses.value
  }
  return courses.value.filter(course => course.status.toString() === statusFilter.value)
})

// 获取教师课程
const loadCourses = async () => {
  try {
    loading.value = true
    const response = await request.get('/teacher/courses') // 移除teacherId参数，从JWT中获取
    
    if (response.code === 0) {
      courses.value = response.data || []
      updateStats()
    } else {
      ElMessage.error(response.msg || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.value.totalCourses = courses.value.length
  stats.value.totalStudents = courses.value.reduce((sum, course) => sum + (course.studentCount || 0), 0)
  stats.value.averageRating = courses.value.length > 0 
    ? (courses.value.reduce((sum, course) => sum + (course.rating || 0), 0) / courses.value.length).toFixed(1)
    : 0
  stats.value.totalViews = courses.value.reduce((sum, course) => sum + (course.viewCount || 0), 0)
}

// 创建课程
const createCourse = () => {
  router.push('/teacher/courses/create')
}

// 编辑课程
const editCourse = (courseId) => {
  router.push(`/teacher/courses/${courseId}/edit`)
}

// 预览课程
const viewCourse = (courseId) => {
  router.push(`/course/${courseId}`)
}

// 管理课程大纲
const manageOutline = (courseId) => {
  router.push(`/teacher/courses/${courseId}/outline`)
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'draft': '草稿',
    'pending': '审核中',
    'published': '已发布',
    'rejected': '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'draft': 'info',
    'pending': 'warning',
    'published': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

// 筛选课程
const filterCourses = () => {
  // 筛选逻辑在computed中处理
}

// 刷新课程
const refreshCourses = () => {
  loadCourses()
}

// 处理课程操作
const handleCourseAction = async (command) => {
  const { action, course } = command
  
  switch (action) {
    case 'publish':
      await publishCourse(course.id)
      break
    case 'unpublish':
      await unpublishCourse(course.id)
      break
    case 'stats':
      viewCourseStats(course.id)
      break
    case 'delete':
      await deleteCourse(course.id)
      break
  }
}

// 发布课程
const publishCourse = async (courseId) => {
  try {
    const response = await request.post(`/teacher/courses/${courseId}/publish`)
    if (response.code === 0) {
      ElMessage.success('课程已提交审核，等待管理员审核')
      loadCourses()
    } else {
      ElMessage.error(response.msg || '提交审核失败')
    }
  } catch (error) {
    console.error('发布课程失败:', error)
    ElMessage.error('发布课程失败')
  }
}

// 下架课程
const unpublishCourse = async (courseId) => {
  try {
    const response = await request.post(`/teacher/courses/${courseId}/unpublish`)
    if (response.code === 0) {
      ElMessage.success('课程下架成功')
      loadCourses()
    } else {
      ElMessage.error(response.msg || '下架课程失败')
    }
  } catch (error) {
    ElMessage.error('下架课程失败')
  }
}

// 查看课程统计
const viewCourseStats = (courseId) => {
  router.push(`/teacher/courses/${courseId}/stats`)
}

// 删除课程
const deleteCourse = async (courseId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个课程吗？删除后无法恢复。', '确认删除', {
      type: 'warning'
    })
    
    const response = await request.delete(`/teacher/courses/${courseId}`)
    if (response.code === 0) {
      ElMessage.success('课程删除成功')
      loadCourses()
    } else {
      ElMessage.error(response.msg || '删除课程失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除课程失败')
    }
  }
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
    loadCourses()
  }
})
</script>

<style scoped>
.teacher-dashboard {
  min-height: 100vh;
  background: #f8f9fa;
}

.container {
  max-width: 1200px;
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

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: #409EFF;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

/* 课程区域 */
.courses-section {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 1.5rem;
  color: #333;
}

.loading-container {
  padding: 40px 0;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

/* 课程网格 */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.course-card {
  border: 1px solid #e9ecef;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.course-image {
  position: relative;
  height: 200px;
}

.course-img {
  width: 100%;
  height: 100%;
}

.course-status {
  position: absolute;
  top: 12px;
  right: 12px;
}

.course-info {
  padding: 20px;
}

.course-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
  line-height: 1.4;
}

.course-subtitle {
  color: #666;
  margin-bottom: 16px;
  font-size: 0.9rem;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.8rem;
  color: #666;
}

.course-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .courses-grid {
    grid-template-columns: 1fr;
  }
  
  .action-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
}
</style>
