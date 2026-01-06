<template>
  <div class="course-detail">
    <!-- 顶部导航栏 -->
    <Header />
    
    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="container">
          <el-skeleton :rows="10" animated />
        </div>
      </div>

      <!-- 课程详情内容 -->
      <div v-else-if="course" class="course-content">
        <!-- 课程头部信息 -->
        <section class="course-header">
          <div class="container">
            <div class="header-content">
              <div class="course-info">
                <div class="breadcrumb">
                  <el-breadcrumb separator="/">
                    <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                    <el-breadcrumb-item :to="{ path: '/courses' }">课程浏览</el-breadcrumb-item>
                    <el-breadcrumb-item>{{ course.title }}</el-breadcrumb-item>
                  </el-breadcrumb>
                </div>
                
                <h1 class="course-title">{{ course.title }}</h1>
                <p class="course-subtitle">{{ course.subtitle || '暂无副标题' }}</p>
                
                <div class="course-meta">
                  <div class="meta-item">
                    <el-rate
                      :model-value="course.rating || 0"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}"
                    />
                    <span class="rating-count">({{ course.ratingCount || 0 }} 评价)</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><User /></el-icon>
                    <span>{{ course.studentCount || 0 }} 学员</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Clock /></el-icon>
                    <span>{{ course.duration || '0小时' }}</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Calendar /></el-icon>
                    <span>更新于 {{ formatDate(course.updatedAt) }}</span>
                  </div>
                </div>

                <div class="course-badges">
                  <el-tag v-if="course.isHot" type="danger">热门课程</el-tag>
                  <el-tag v-if="course.isNew" type="success">最新发布</el-tag>
                  <el-tag v-if="course.isFree" type="info">免费课程</el-tag>
                  <el-tag :type="getLevelType(course.level)">{{ getLevelText(course.level) }}</el-tag>
                </div>
              </div>
              
              <div class="course-video">
                <div class="video-container">
                  <el-image
                    :src="getImageUrl(course.coverImage)"
                    alt="课程封面"
                    fit="cover"
                    class="course-cover"
                  />
                  <div class="play-button" @click="playPreview">
                    <el-icon size="60"><VideoPlay /></el-icon>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 课程主体内容 -->
        <section class="course-body">
          <div class="container">
            <div class="body-content">
              <!-- 左侧内容 -->
              <div class="left-content">
                <!-- 课程介绍 -->
                      <div class="course-intro">
                  <h3>课程介绍</h3>
                  <div class="description" v-html="course.description || '暂无课程描述'"></div>
                      </div>

                <!-- 讲师介绍 -->
                <div class="instructor-info" v-if="course.instructor">
                  <h4>讲师介绍</h4>
                  <div class="instructor-card">
                    <el-avatar :src="getImageUrl(course.instructor.avatar)" :size="60">
                      {{ course.instructor.username ? course.instructor.username.charAt(0) : 'T' }}
                                </el-avatar>
                    <div class="instructor-details">
                      <h5>{{ course.instructor.username || '讲师' }}</h5>
                      <p>{{ course.instructor.email || '专业讲师' }}</p>
                                </div>
                              </div>
                </div>

                <!-- 课程大纲 -->
                <div class="course-outline">
                  <h3>课程大纲</h3>
                  <div v-if="outlineLoading" class="outline-loading">
                    <el-skeleton :rows="5" animated />
                  </div>
                  <div v-else-if="courseOutline.length === 0" class="no-outline">
                    <el-empty description="暂无课程大纲" />
                  </div>
                  <div v-else class="outline-content">
                    <el-collapse v-model="activeSections" accordion>
                      <el-collapse-item 
                        v-for="(section, index) in courseOutline" 
                        :key="section.id"
                        :name="section.id"
                        class="outline-section"
                      >
                        <template #title>
                          <div class="section-header">
                            <div class="section-info">
                              <span class="section-number">第{{ index + 1 }}章</span>
                              <span class="section-title">{{ section.title }}</span>
                            </div>
                            <div class="section-meta">
                              <span class="lesson-count">{{ section.lessons?.length || 0 }} 课时</span>
                              <span v-if="section.duration" class="section-duration">{{ section.duration }}</span>
                            </div>
                          </div>
                        </template>
                        
                        <div class="section-description" v-if="section.description">
                          <p>{{ section.description }}</p>
                        </div>
                        
                        <div class="lessons-list">
                          <div 
                            v-for="(lesson, lessonIndex) in section.lessons" 
                            :key="lesson.id"
                            class="lesson-item"
                            :class="{ 'free-lesson': lesson.isFree }"
                          >
                            <div class="lesson-info">
                              <div class="lesson-header">
                                <span class="lesson-number">{{ index + 1 }}.{{ lessonIndex + 1 }}</span>
                                <span class="lesson-title">{{ lesson.title }}</span>
                                <el-tag v-if="lesson.isFree" type="success" size="small">免费</el-tag>
                                <el-tag v-else-if="!purchased" type="warning" size="small">付费</el-tag>
                              </div>
                              <div class="lesson-meta">
                                <span class="lesson-type">
                                  <el-icon>
                                    <VideoPlay v-if="lesson.type === 'video'" />
                                    <Download v-else-if="lesson.type === 'document'" />
                                    <Headset v-else />
                                  </el-icon>
                                  {{ lesson.type === 'video' ? '视频' : lesson.type === 'document' ? '文档' : '其他' }}
                                </span>
                                <span v-if="lesson.duration" class="lesson-duration">{{ lesson.duration }}</span>
                              </div>
                              <div v-if="lesson.description" class="lesson-description">
                                <p>{{ lesson.description }}</p>
                              </div>
                            </div>
                            <div class="lesson-actions">
                              <el-button 
                                v-if="lesson.isFree || purchased" 
                                type="primary" 
                                size="small"
                                @click="startLesson(lesson)"
                              >
                                <el-icon><VideoPlay /></el-icon>
                                开始学习
                              </el-button>
                              <el-button 
                                v-else 
                                type="warning" 
                                size="small"
                                disabled
                              >
                                <el-icon><Lock /></el-icon>
                                需要购买
                              </el-button>
                            </div>
                          </div>
                        </div>
                      </el-collapse-item>
                    </el-collapse>
                  </div>
                </div>
              </div>

              <!-- 右侧购买区域 -->
              <div class="right-content">
                <div class="purchase-card">
                  <div class="price-section">
                    <div v-if="course.isFree" class="price">
                      <span class="current-price">免费</span>
                    </div>
                    <div v-else class="price">
                      <span class="current-price">¥{{ course.price || 0 }}</span>
                      <span v-if="course.originalPrice" class="original-price">¥{{ course.originalPrice }}</span>
                    </div>
                  </div>

                  <div class="purchase-actions">
                    <el-button
                      :type="purchased ? 'success' : 'primary'"
                      size="large"
                      class="purchase-btn"
                      :loading="purchasing"
                      @click="handlePurchase"
                    >
                      <el-icon v-if="!purchasing"><VideoPlay /></el-icon>
                      {{ purchased ? '已购买 - 开始学习' : (course.isFree ? '免费学习' : '立即购买') }}
                    </el-button>
                    <el-button
                      size="large"
                      class="favorite-btn"
                      :type="isFavorited ? 'danger' : 'default'"
                      @click="toggleFavorite"
                    >
                      <el-icon><Star :filled="isFavorited" /></el-icon>
                      {{ isFavorited ? '已收藏' : '收藏课程' }}
                    </el-button>
                  </div>

                  <div class="course-features">
                    <div class="feature-item">
                      <el-icon><Clock /></el-icon>
                      <span>终身访问</span>
                    </div>
                    <div class="feature-item">
                      <el-icon><Download /></el-icon>
                      <span>课程资料</span>
                    </div>
                    <div class="feature-item">
                      <el-icon><Headset /></el-icon>
                      <span>在线答疑</span>
                    </div>
                    <div class="feature-item">
                      <el-icon><Medal /></el-icon>
                      <span>学习证书</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 课程评价区域 -->
        <section class="course-reviews">
          <div class="container">
            <CourseReview :courseId="route.params.id" :canReview="purchased" />
          </div>
        </section>
      </div>

      <!-- 课程不存在 -->
      <div v-else class="not-found">
        <div class="container">
          <el-empty description="课程不存在或已被删除">
            <el-button type="primary" @click="goBack">返回课程列表</el-button>
          </el-empty>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Header from '@/components/Header.vue'
import CourseReview from '@/components/CourseReview.vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import { getImageUrl } from '@/utils/imageUtils'
import {
  User, Clock, Calendar, VideoPlay, Download,
  Headset, Medal, Lock
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(false)
const course = ref(null)
const purchased = ref(false)
const purchasing = ref(false)
const courseOutline = ref([])
const outlineLoading = ref(false)
const activeSections = ref([])
const isFavorited = ref(false)

// 获取课程详情
const loadCourseDetail = async () => {
  try {
    loading.value = true
    const courseId = route.params.id
    const response = await request.get(`/course/${courseId}`)

    if (response.code === 0) {
      course.value = response.data

      // 如果用户已登录，检查购买状态和收藏状态
      if (authStore.isLoggedIn) {
        await checkPurchaseStatus()
        await checkFavoriteStatus()
      }

      // 加载课程大纲
      await loadCourseOutline()
    } else {
      ElMessage.error(response.msg || '获取课程详情失败')
    }
  } catch (error) {
    console.error('获取课程详情失败:', error)
    ElMessage.error('获取课程详情失败')
  } finally {
    loading.value = false
  }
}

// 检查购买状态
const checkPurchaseStatus = async () => {
  try {
    const response = await request.get('/purchase/my-courses')
    if (response.code === 0) {
      const purchasedCourseIds = response.data.map(record => record.courseId)
      purchased.value = purchasedCourseIds.includes(course.value.id)
    }
  } catch (error) {
    console.error('检查购买状态失败:', error)
  }
}

// 检查收藏状态
const checkFavoriteStatus = async () => {
  try {
    const response = await request.get(`/favorites/check/${course.value.id}`)
    if (response.code === 0) {
      isFavorited.value = response.data
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 切换收藏状态
const toggleFavorite = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/account/login')
    return
  }

  try {
    if (isFavorited.value) {
      // 取消收藏
      const response = await request.delete(`/favorites/remove/${course.value.id}`)
      if (response.code === 0) {
        isFavorited.value = false
        ElMessage.success('已取消收藏')
      } else {
        ElMessage.error(response.msg || '取消收藏失败')
      }
    } else {
      // 添加收藏
      const response = await request.post(`/favorites/add/${course.value.id}`)
      if (response.code === 0) {
        isFavorited.value = true
        ElMessage.success('收藏成功')
      } else {
        ElMessage.error(response.msg || '收藏失败')
      }
    }
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    ElMessage.error('操作失败')
  }
}

// 加载课程大纲
const loadCourseOutline = async () => {
  try {
    outlineLoading.value = true
    const courseId = route.params.id
    const response = await request.get(`/course/${courseId}/outline`)
    
    if (response.code === 0) {
      courseOutline.value = response.data || []
    } else {
      console.error('获取课程大纲失败:', response.msg)
    }
  } catch (error) {
    console.error('获取课程大纲失败:', error)
  } finally {
    outlineLoading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 获取难度类型
const getLevelType = (level) => {
  const types = {
    beginner: 'success',
    intermediate: 'warning',
    advanced: 'danger'
  }
  return types[level] || 'info'
}

// 获取难度文本
const getLevelText = (level) => {
  const texts = {
    beginner: '入门',
    intermediate: '进阶',
    advanced: '高级'
  }
  return texts[level] || '未知'
}

// 播放预览
const playPreview = () => {
  ElMessage.info('预览功能开发中...')
}

// 处理购买
const handlePurchase = async () => {
  // 检查用户是否登录
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/account/login')
    return
  }
  
  // 如果已经购买，直接跳转到学习页面
  if (purchased.value) {
    ElMessage.success('开始学习！')
    // 这里可以跳转到学习页面
    return
  }
  
  // 如果是免费课程
  if (course.value.isFree) {
    ElMessage.success('开始免费学习！')
    return
  }
  
  // 付费课程购买
  try {
    const confirmed = await ElMessageBox.confirm(
      `确认购买课程《${course.value.title}》吗？\n价格：¥${course.value.price}`,
      '确认购买',
    {
      confirmButtonText: '确认购买',
      cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (confirmed) {
      purchasing.value = true
      
      const response = await request.post('/purchase/course', {
        courseId: course.value.id
      })
      
      if (response.code === 0) {
        ElMessage.success('购买成功！')
        purchased.value = true
      } else {
        ElMessage.error(response.msg || '购买失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('购买失败:', error)
      ElMessage.error('购买失败，请重试')
    }
  } finally {
    purchasing.value = false
  }
}


// 开始学习课时
const startLesson = (lesson) => {
  // 跳转到学习页面
  router.push(`/learning/${route.params.id}`)
}

// 返回课程列表
const goBack = () => {
  router.push('/courses')
}

onMounted(() => {
  loadCourseDetail()
})
</script>

<style scoped>
.course-detail {
  min-height: 100vh;
  background: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.loading-container {
  padding: 40px 0;
}

/* 课程头部 */
.course-header {
  background: white;
  padding: 40px 0;
  border-bottom: 1px solid #e9ecef;
}

.header-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 60px;
  align-items: start;
}

.course-info {
  flex: 1;
}

.breadcrumb {
  margin-bottom: 20px;
}

.course-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 16px;
  color: #333;
  line-height: 1.2;
}

.course-subtitle {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 24px;
  line-height: 1.5;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 24px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 0.9rem;
}

.course-badges {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.course-video {
  position: relative;
}

.video-container {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.course-cover {
  width: 100%;
  height: 300px;
}

.play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border-radius: 50%;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.play-button:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: translate(-50%, -50%) scale(1.1);
}

/* 课程主体 */
.course-body {
  padding: 40px 0;
}

.body-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 60px;
}

.left-content {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.course-intro h3 {
  font-size: 1.5rem;
  margin-bottom: 20px;
  color: #333;
}

.description {
  line-height: 1.8;
  color: #666;
  margin-bottom: 40px;
}

.instructor-info {
  border-top: 1px solid #e9ecef;
  padding-top: 30px;
}

.instructor-info h4 {
  font-size: 1.3rem;
  margin-bottom: 20px;
  color: #333;
}

.instructor-card {
  display: flex;
  gap: 20px;
  align-items: center;
}

.instructor-details h5 {
  font-size: 1.1rem;
  margin-bottom: 8px;
  color: #333;
}

.instructor-details p {
  color: #666;
  font-size: 0.9rem;
}

/* 购买区域 */
.right-content {
  position: sticky;
  top: 100px;
}

.purchase-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.price-section {
  margin-bottom: 30px;
}

.price {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.current-price {
  font-size: 2.5rem;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 1.2rem;
  color: #999;
  text-decoration: line-through;
}

.purchase-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 30px;
}

.purchase-btn {
  width: 100%;
  height: 50px;
  font-size: 1.1rem;
}


.course-features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: #666;
}

.feature-item .el-icon {
  color: #409EFF;
}

/* 课程大纲样式 */
.course-outline {
  margin-top: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.course-outline h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 1.3rem;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 10px;
}

.outline-loading {
  padding: 20px 0;
}

.no-outline {
  text-align: center;
  padding: 40px 0;
}

.outline-content {
  margin-top: 10px;
}

.outline-section {
  margin-bottom: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  cursor: pointer;
  transition: background-color 0.3s;
}

.section-header:hover {
  background: #e9ecef;
}

.section-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.section-number {
  color: #409EFF;
  font-weight: 600;
  font-size: 0.9rem;
}

.section-title {
  color: #333;
  font-weight: 500;
  font-size: 1rem;
}

.section-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #666;
  font-size: 0.9rem;
}

.lesson-count {
  color: #409EFF;
}

.section-description {
  padding: 15px 20px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
}

.section-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.lessons-list {
  padding: 0;
}

.lesson-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s;
}

.lesson-item:last-child {
  border-bottom: none;
}

.lesson-item:hover {
  background: #f8f9fa;
}

.lesson-item.free-lesson {
  background: #f0f9ff;
}

.lesson-info {
  flex: 1;
  margin-right: 15px;
}

.lesson-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.lesson-number {
  color: #409EFF;
  font-weight: 600;
  font-size: 0.9rem;
  min-width: 40px;
}

.lesson-title {
  color: #333;
  font-weight: 500;
  flex: 1;
}

.lesson-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 5px;
  color: #666;
  font-size: 0.85rem;
}

.lesson-type {
  display: flex;
  align-items: center;
  gap: 5px;
}

.lesson-duration {
  color: #999;
}

.lesson-description {
  margin-top: 5px;
}

.lesson-description p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
}

.lesson-actions {
  flex-shrink: 0;
}

/* 课程评价区域 */
.course-reviews {
  padding: 0 0 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .body-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .right-content {
    position: static;
  }
  
  .course-title {
    font-size: 2rem;
  }
  
  .course-meta {
    flex-direction: column;
    gap: 16px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .lesson-item {
    flex-direction: column;
    gap: 10px;
  }
  
  .lesson-info {
    margin-right: 0;
  }
}
</style>