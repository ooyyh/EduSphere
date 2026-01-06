<!--
  管理员控制台页面

  功能说明：
  - 系统概览：展示用户数、课程数、待审核课程等统计信息
  - 课程管理：查看、审核、删除所有课程
  - 用户管理：查看、管理所有用户
  - 课程审核：审核教师提交的课程（通过/拒绝）
  - 数据统计：展示课程、用户、收入、学习等各类统计图表

  权限：仅管理员可访问

  作者：EduSphere团队
  最后更新：2025-11-26
-->
<template>
  <div class="admin-dashboard">
    <!-- 顶部导航栏 -->
    <Header />
    
    <!-- 管理员内容区域 -->
    <main class="admin-main">
      <div class="admin-container">
        <!-- 侧边栏 -->
        <aside class="admin-sidebar">
          <div class="sidebar-header">
            <h3>管理后台</h3>
          </div>
          <nav class="sidebar-nav">
            <el-menu
              :default-active="activeMenu"
              @select="handleMenuSelect"
              class="admin-menu"
            >
              <el-menu-item index="overview">
                <el-icon><DataBoard /></el-icon>
                <span>系统概览</span>
              </el-menu-item>
              <el-menu-item index="courses">
                <el-icon><VideoPlay /></el-icon>
                <span>课程管理</span>
              </el-menu-item>
              <el-menu-item index="users">
                <el-icon><User /></el-icon>
                <span>用户管理</span>
              </el-menu-item>
              <el-menu-item index="reviews">
                <el-icon><EditPen /></el-icon>
                <span>课程审核</span>
              </el-menu-item>
              <el-menu-item index="statistics">
                <el-icon><TrendCharts /></el-icon>
                <span>数据统计</span>
              </el-menu-item>
            </el-menu>
          </nav>
        </aside>
        
        <!-- 主内容区 -->
        <main class="admin-content">
          <!-- 系统概览 -->
          <div v-if="activeMenu === 'overview'" class="overview-section">
            <h2>系统概览</h2>
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-icon">
                  <el-icon><User /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ overviewData.totalUsers || 0 }}</div>
                  <div class="stat-label">总用户数</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <el-icon><VideoPlay /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ overviewData.totalCourses || 0 }}</div>
                  <div class="stat-label">总课程数</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <el-icon><Check /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ overviewData.publishedCourses || 0 }}</div>
                  <div class="stat-label">已发布课程</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <el-icon><Clock /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ overviewData.pendingCourses || 0 }}</div>
                  <div class="stat-label">待审核课程</div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 课程管理 -->
          <div v-if="activeMenu === 'courses'" class="courses-section">
            <div class="section-header">
              <h2>课程管理</h2>
              <div class="section-actions">
                <el-button type="primary" @click="loadCourses">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
            </div>
            
            <el-table :data="courses" v-loading="coursesLoading" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="课程标题" min-width="200" />
              <el-table-column prop="instructorName" label="讲师" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="price" label="价格" width="100">
                <template #default="scope">
                  {{ scope.row.isFree ? '免费' : '¥' + scope.row.price }}
                </template>
              </el-table-column>
              <el-table-column prop="studentCount" label="学生数" width="100" />
              <el-table-column prop="createdAt" label="创建时间" width="160">
                <template #default="scope">
                  {{ formatDate(scope.row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button size="small" @click="viewCourse(scope.row)">查看</el-button>
                  <el-button 
                    v-if="scope.row.status === 'pending'"
                    type="success" 
                    size="small" 
                    @click="approveCourse(scope.row.id)"
                  >
                    通过
                  </el-button>
                  <el-button 
                    v-if="scope.row.status === 'pending'"
                    type="danger" 
                    size="small" 
                    @click="rejectCourse(scope.row.id)"
                  >
                    拒绝
                  </el-button>
                  <el-button 
                    type="danger" 
                    size="small" 
                    @click="deleteCourse(scope.row.id)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <!-- 用户管理 -->
          <div v-if="activeMenu === 'users'" class="users-section">
            <div class="section-header">
              <h2>用户管理</h2>
              <div class="section-actions">
                <el-button type="primary" @click="loadUsers">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
            </div>
            
            <el-table :data="users" v-loading="usersLoading" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="email" label="邮箱" min-width="200" />
              <el-table-column prop="role" label="角色" width="100">
                <template #default="scope">
                  <el-tag :type="getRoleType(scope.row.role)">
                    {{ getRoleText(scope.row.role) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="注册时间" width="160">
                <template #default="scope">
                  {{ formatDate(scope.row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button size="small" @click="viewUser(scope.row)">查看</el-button>
                  <el-button 
                    v-if="scope.row.role !== 'admin'"
                    type="danger" 
                    size="small" 
                    @click="deleteUser(scope.row.id)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <!-- 课程审核 -->
          <div v-if="activeMenu === 'reviews'" class="reviews-section">
            <div class="section-header">
              <h2>课程审核</h2>
              <div class="section-actions">
                <el-button type="primary" @click="loadPendingCourses">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
            </div>
            
            <el-table :data="pendingCourses" v-loading="pendingLoading" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="课程标题" min-width="200" />
              <el-table-column prop="instructorName" label="讲师" width="120" />
              <el-table-column prop="description" label="课程描述" min-width="300" show-overflow-tooltip />
              <el-table-column prop="price" label="价格" width="100">
                <template #default="scope">
                  {{ scope.row.isFree ? '免费' : '¥' + scope.row.price }}
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="提交时间" width="160">
                <template #default="scope">
                  {{ formatDate(scope.row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button size="small" @click="viewCourse(scope.row)">查看详情</el-button>
                  <el-button type="success" size="small" @click="approveCourse(scope.row.id)">
                    审核通过
                  </el-button>
                  <el-button type="danger" size="small" @click="rejectCourse(scope.row.id)">
                    审核拒绝
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <!-- 数据统计 -->
          <div v-if="activeMenu === 'statistics'" class="statistics-section">
            <h2>数据统计</h2>
            <div class="stats-tabs">
              <el-tabs v-model="activeStatsTab" @tab-click="handleStatsTabClick">
                <el-tab-pane label="课程统计" name="courses">
                  <div class="stats-content">
                    <div class="chart-container">
                      <CourseStatusChart ref="courseStatusChartRef" :data="courseStats" />
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="用户统计" name="users">
                  <div class="stats-content">
                    <div class="chart-container">
                      <UserRoleChart ref="userRoleChartRef" :data="userStats" />
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="收入统计" name="income">
                  <div class="stats-content">
                    <div class="chart-container">
                      <IncomeTrendChart ref="incomeTrendChartRef" :data="incomeStats" />
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="学习统计" name="learning">
                  <div class="stats-content">
                    <div class="chart-container">
                      <LearningProgressChart ref="learningProgressChartRef" :data="learningStats" />
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </main>
      </div>
    </main>
    
    <!-- 拒绝课程对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝课程" width="500px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="userDetailDialogVisible" title="用户详情" width="800px">
      <div v-if="currentUserDetail" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ currentUserDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentUserDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentUserDetail.email || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="getRoleType(currentUserDetail.role)">
              {{ getRoleText(currentUserDetail.role) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUserDetail.status === 1 ? 'success' : 'danger'">
              {{ currentUserDetail.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ formatDate(currentUserDetail.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4>统计信息</h4>
        <div v-if="userDetailStats" class="user-stats-grid">
          <div class="user-stat-card">
            <div class="stat-label">购买课程数</div>
            <div class="stat-value">{{ userDetailStats.purchasedCourses || 0 }}</div>
          </div>
          <div class="user-stat-card">
            <div class="stat-label">账户余额</div>
            <div class="stat-value">¥{{ userDetailStats.balance || 0 }}</div>
          </div>
          <div class="user-stat-card">
            <div class="stat-label">累计充值</div>
            <div class="stat-value">¥{{ userDetailStats.totalRecharge || 0 }}</div>
          </div>
          <div class="user-stat-card">
            <div class="stat-label">累计消费</div>
            <div class="stat-value">¥{{ userDetailStats.totalConsumption || 0 }}</div>
          </div>
        </div>

        <el-divider />

        <h4>最近购买课程</h4>
        <el-table v-if="userDetailCourses && userDetailCourses.length > 0" :data="userDetailCourses" style="width: 100%" max-height="300">
          <el-table-column prop="courseTitle" label="课程名称" />
          <el-table-column prop="purchasePrice" label="购买价格" width="120">
            <template #default="scope">
              ¥{{ scope.row.purchasePrice }}
            </template>
          </el-table-column>
          <el-table-column prop="purchaseTime" label="购买时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.purchaseTime) }}
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无购买记录" :image-size="100" />
      </div>
      <template #footer>
        <el-button @click="userDetailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  DataBoard, VideoPlay, User, EditPen, TrendCharts, 
  Refresh, Check, Clock 
} from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import CourseStatusChart from '@/components/charts/CourseStatusChart.vue'
import UserRoleChart from '@/components/charts/UserRoleChart.vue'
import IncomeTrendChart from '@/components/charts/IncomeTrendChart.vue'
import LearningProgressChart from '@/components/charts/LearningProgressChart.vue'

const authStore = useAuthStore()

// 响应式数据
const activeMenu = ref('overview')
const activeStatsTab = ref('courses')

// 概览数据
const overviewData = ref({})

// 课程数据
const courses = ref([])
const coursesLoading = ref(false)

// 用户数据
const users = ref([])
const usersLoading = ref(false)

// 待审核课程
const pendingCourses = ref([])
const pendingLoading = ref(false)

// 拒绝课程对话框
const rejectDialogVisible = ref(false)
const rejectForm = ref({
  courseId: null,
  reason: ''
})

// 用户详情对话框
const userDetailDialogVisible = ref(false)
const currentUserDetail = ref(null)
const userDetailStats = ref(null)
const userDetailCourses = ref([])

// 图表数据
const courseStats = ref({
  draftCourses: 0,
  pendingCourses: 0,
  publishedCourses: 0,
  rejectedCourses: 0
})

const userStats = ref({
  adminCount: 0,
  teacherCount: 0,
  studentCount: 0
})

const incomeStats = ref([])

const learningStats = ref({
  totalLessons: 0,
  completedLessons: 0,
  inProgressLessons: 0
})

// 图表组件引用
const courseStatusChartRef = ref(null)
const userRoleChartRef = ref(null)
const incomeTrendChartRef = ref(null)
const learningProgressChartRef = ref(null)

// 检查管理员权限
const checkAdminPermission = () => {
  if (!authStore.isLoggedIn || authStore.user?.role !== 'admin') {
    ElMessage.error('您没有管理员权限')
    return false
  }
  return true
}

// 加载系统概览
const loadOverview = async () => {
  try {
    const response = await request.get('/admin/overview')
    if (response.code === 0) {
      overviewData.value = response.data
    }
  } catch (error) {
    console.error('获取系统概览失败:', error)
  }
}

// 加载课程列表
const loadCourses = async () => {
  try {
    coursesLoading.value = true
    const response = await request.get('/admin/courses')
    if (response.code === 0) {
      courses.value = response.data
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
  } finally {
    coursesLoading.value = false
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    usersLoading.value = true
    const response = await request.get('/admin/users')
    if (response.code === 0) {
      users.value = response.data
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    usersLoading.value = false
  }
}

// 加载待审核课程
const loadPendingCourses = async () => {
  try {
    pendingLoading.value = true
    const response = await request.get('/admin/courses/pending')
    if (response.code === 0) {
      pendingCourses.value = response.data
    }
  } catch (error) {
    console.error('获取待审核课程失败:', error)
  } finally {
    pendingLoading.value = false
  }
}

// 加载图表数据
const loadChartData = async () => {
  try {
    // 加载课程统计数据
    const courseStatsResponse = await request.get('/admin/courses/stats')
    if (courseStatsResponse.code === 0) {
      courseStats.value = courseStatsResponse.data
    }
    
    // 加载用户统计数据
    const userStatsResponse = await request.get('/admin/users/stats')
    if (userStatsResponse.code === 0) {
      userStats.value = userStatsResponse.data
    }
    
    // 加载收入统计数据
    const incomeStatsResponse = await request.get('/admin/income/stats')
    if (incomeStatsResponse.code === 0) {
      incomeStats.value = incomeStatsResponse.data
    }
    
    // 加载学习统计数据
    const learningStatsResponse = await request.get('/admin/learning/stats')
    if (learningStatsResponse.code === 0) {
      learningStats.value = learningStatsResponse.data
    }
  } catch (error) {
    console.error('加载图表数据失败:', error)
  }
}

// 菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'courses') {
    loadCourses()
  } else if (index === 'users') {
    loadUsers()
  } else if (index === 'reviews') {
    loadPendingCourses()
  }
}

// 审核通过课程
const approveCourse = async (courseId) => {
  try {
    const response = await request.post(`/admin/courses/${courseId}/approve`)
    if (response.code === 0) {
      ElMessage.success('课程审核通过')
      loadCourses()
      loadPendingCourses()
    } else {
      ElMessage.error(response.msg || '审核失败')
    }
  } catch (error) {
    console.error('审核课程失败:', error)
    ElMessage.error('审核失败')
  }
}

// 拒绝课程
const rejectCourse = (courseId) => {
  rejectForm.value.courseId = courseId
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

// 确认拒绝
const confirmReject = async () => {
  try {
    const response = await request.post(`/admin/courses/${rejectForm.value.courseId}/reject`, {
      reason: rejectForm.value.reason
    })
    if (response.code === 0) {
      ElMessage.success('课程已拒绝')
      rejectDialogVisible.value = false
      loadCourses()
      loadPendingCourses()
    } else {
      ElMessage.error(response.msg || '拒绝失败')
    }
  } catch (error) {
    console.error('拒绝课程失败:', error)
    ElMessage.error('拒绝失败')
  }
}

// 删除课程
const deleteCourse = async (courseId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个课程吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await request.delete(`/admin/courses/${courseId}`)
    if (response.code === 0) {
      ElMessage.success('课程删除成功')
      loadCourses()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课程失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 删除用户
const deleteUser = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await request.delete(`/admin/users/${userId}`)
    if (response.code === 0) {
      ElMessage.success('用户删除成功')
      loadUsers()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 查看课程
const viewCourse = (course) => {
  // 跳转到课程详情页
  window.open(`/course/${course.id}`, '_blank')
}

// 查看用户
const viewUser = async (user) => {
  try {
    currentUserDetail.value = user
    userDetailDialogVisible.value = true

    // 加载用户详细统计信息
    const response = await request.get(`/admin/users/${user.id}/detail`)
    if (response.code === 0) {
      userDetailStats.value = response.data.stats
      userDetailCourses.value = response.data.courses || []
    }
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  }
}

// 统计标签页切换
const handleStatsTabClick = (tab) => {
  // 加载图表数据
  loadChartData()
  
  // 延迟重新初始化图表，确保标签页切换完成
  setTimeout(() => {
    // 根据当前标签页重新初始化对应的图表
    const tabName = tab.props.name
    if (tabName === 'courses' && courseStatusChartRef.value) {
      courseStatusChartRef.value.reinitChart()
    } else if (tabName === 'users' && userRoleChartRef.value) {
      userRoleChartRef.value.reinitChart()
    } else if (tabName === 'income' && incomeTrendChartRef.value) {
      incomeTrendChartRef.value.reinitChart()
    } else if (tabName === 'learning' && learningProgressChartRef.value) {
      learningProgressChartRef.value.reinitChart()
    }
    
    // 触发窗口resize事件，让图表重新计算尺寸
    window.dispatchEvent(new Event('resize'))
  }, 200)
}

// 工具函数
const getStatusType = (status) => {
  const types = {
    'draft': 'info',
    'pending': 'warning',
    'published': 'success',
    'rejected': 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'draft': '草稿',
    'pending': '待审核',
    'published': '已发布',
    'rejected': '已拒绝'
  }
  return texts[status] || '未知'
}

const getRoleType = (role) => {
  const types = {
    'admin': 'danger',
    'teacher': 'warning',
    'student': 'success'
  }
  return types[role] || 'info'
}

const getRoleText = (role) => {
  const texts = {
    'admin': '管理员',
    'teacher': '教师',
    'student': '学生'
  }
  return texts[role] || '未知'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

// 组件挂载
onMounted(() => {
  if (checkAdminPermission()) {
    loadOverview()
    loadChartData() // 加载图表数据
  }
})
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: #f5f5f5;
}


.admin-container {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 0;
  min-height: calc(100vh - 60px);
}

/* 侧边栏 */
.admin-sidebar {
  background: white;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #f8f9fa;
}

.sidebar-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.2rem;
}

.admin-menu {
  border: none;
}

.admin-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.admin-menu .el-menu-item span {
  margin-left: 8px;
}

/* 主内容区 */
.admin-content {
  padding: 20px;
  background: #f5f5f5;
  overflow-y: auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  color: #333;
}

/* 概览统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  background: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

/* 统计标签页 */
.stats-tabs {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-container {
  min-height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  width: 100%;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .admin-container {
    grid-template-columns: 1fr;
  }
  
  .admin-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .admin-content {
    padding: 15px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
}

/* 用户详情样式 */
.user-detail h4 {
  margin: 16px 0;
  color: #333;
  font-size: 1.1rem;
}

.user-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin: 16px 0;
}

.user-stat-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
}

.user-stat-card .stat-label {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 8px;
}

.user-stat-card .stat-value {
  font-size: 1.5rem;
  font-weight: 600;
  color: #409EFF;
}

@media (max-width: 768px) {
  .user-stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
