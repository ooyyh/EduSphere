<!--
  学习页面

  功能说明：
  - 展示课程学习内容，包括视频、文档、测验等
  - 左侧显示课程大纲树形结构
  - 支持视频播放、文档查看、测验答题
  - 自动记录学习进度
  - 支持全屏播放视频
  - 显示课程整体学习进度

  作者：EduSphere团队
  最后更新：2025-11-26
-->
<template>
  <div class="learning-page">
    <!-- 顶部导航栏 -->
    <Header />
    
    <!-- 学习内容区域 -->
    <main class="learning-main">
      <div class="learning-container">
        <!-- 左侧课程大纲 -->
        <aside class="course-sidebar">
          <div class="sidebar-header">
            <h3>{{ course?.title || '课程学习' }}</h3>
            <el-button type="text" @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回课程
            </el-button>
          </div>
          
          <div class="outline-tree">
            <el-tree
              :data="courseOutline"
              :props="treeProps"
              node-key="id"
              :default-expanded-keys="expandedKeys"
              :current-node-key="currentLessonId"
              @node-click="handleNodeClick"
              class="outline-tree"
            >
              <template #default="{ node, data }">
                <div class="tree-node">
                  <div class="node-content">
                    <el-icon v-if="data.type === 'section'">
                      <Folder />
                    </el-icon>
                    <el-icon v-else-if="data.type === 'video'">
                      <VideoPlay />
                    </el-icon>
                    <el-icon v-else-if="data.type === 'document'">
                      <Document />
                    </el-icon>
                    <el-icon v-else>
                      <QuestionFilled />
                    </el-icon>
                    <span class="node-title">{{ data.title }}</span>
                  </div>
                  <div class="node-meta">
                    <el-tag v-if="data.isFree" type="success" size="small">免费</el-tag>
                    <el-tag v-else-if="data.type === 'lesson'" type="warning" size="small">付费</el-tag>
                    <span v-if="data.duration" class="duration">{{ data.duration }}</span>
                  </div>
                </div>
              </template>
            </el-tree>
          </div>
        </aside>
        
        <!-- 右侧学习内容 -->
        <main class="learning-content">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="8" animated />
          </div>
          
          <!-- 课时内容 -->
          <div v-else-if="currentLesson" class="lesson-content">
            <!-- 课时头部 -->
            <div class="lesson-header">
              <div class="lesson-info">
                <h2>{{ currentLesson.title }}</h2>
                <div class="lesson-meta">
                  <el-tag :type="getLessonTypeColor(currentLesson.type)" size="small">
                    {{ getLessonTypeText(currentLesson.type) }}
                  </el-tag>
                  <span v-if="currentLesson.duration" class="duration">
                    <el-icon><Clock /></el-icon>
                    {{ currentLesson.duration }}
                  </span>
                </div>
              </div>
              <div class="lesson-actions">
                <el-button @click="toggleFullscreen" v-if="isVideoLesson">
                  <el-icon><Expand /></el-icon>
                  {{ isFullscreen ? '退出全屏' : '全屏' }}
                </el-button>
              </div>
            </div>
            
            <!-- 课时描述 -->
            <div v-if="currentLesson.description" class="lesson-description">
              <p>{{ currentLesson.description }}</p>
            </div>
            
            <!-- 学习内容区域 -->
            <div class="content-area" :class="{ 'fullscreen': isFullscreen }">
              <!-- 视频播放器 -->
              <div v-if="currentLesson.type === 'video'" class="video-player">
                <video
                  ref="videoPlayer"
                  :src="getImageUrl(currentLesson.videoUrl)"
                  controls
                  preload="metadata"
                  @loadedmetadata="onVideoLoaded"
                  @timeupdate="onVideoTimeUpdate"
                  @ended="onVideoEnded"
                  class="video-element"
                >
                  您的浏览器不支持视频播放
                </video>
                <div v-if="!currentLesson.videoUrl" class="no-video">
                  <el-empty description="暂无视频内容" />
                </div>
              </div>
              
              <!-- 文档查看器 -->
              <div v-else-if="currentLesson.type === 'document'" class="document-viewer">
                <div v-if="currentLesson.documentUrl" class="document-content">
                  <iframe
                    :src="getDocumentViewerUrl(getImageUrl(currentLesson.documentUrl))"
                    frameborder="0"
                    class="document-iframe"
                  ></iframe>
                  <div class="document-actions">
                    <el-button type="primary" @click="downloadDocument">
                      <el-icon><ArrowDown /></el-icon>
                      下载文档
                    </el-button>
                  </div>
                </div>
                <div v-else class="no-document">
                  <el-empty description="暂无文档内容" />
                </div>
              </div>
              
              <!-- 测验内容 -->
              <div v-else-if="currentLesson.type === 'quiz'" class="quiz-content">
                <QuizComponent
                  :quiz-data="currentLesson.quizData"
                  :lesson-id="currentLesson.id"
                  @quiz-completed="onQuizCompleted"
                />
              </div>
              
              <!-- 其他类型内容 -->
              <div v-else class="other-content">
                <el-empty description="该课时暂无学习内容" />
              </div>
            </div>
            
            <!-- 学习进度 -->
            <div class="learning-progress">
              <div class="progress-info">
                <span>学习进度</span>
                <span>{{ learningProgress }}%</span>
              </div>
              <el-progress :percentage="learningProgress" :show-text="false" />
            </div>
          </div>
          
          <!-- 无课时内容 -->
          <div v-else class="no-lesson">
            <el-empty description="请选择要学习的课时" />
          </div>
        </main>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, Folder, VideoPlay, Document, QuestionFilled, 
  Clock, Expand, ArrowDown 
} from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import QuizComponent from '@/components/learning/QuizComponent.vue'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'
import { getImageUrl } from '@/utils/imageUtils'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const course = ref(null)
const courseOutline = ref([])
const currentLesson = ref(null)
const currentLessonId = ref(null)
const expandedKeys = ref([])
const learningProgress = ref(0)
const isFullscreen = ref(false)
const videoPlayer = ref(null)

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'title'
}

// 计算属性
const isVideoLesson = computed(() => currentLesson.value?.type === 'video')

// 获取课程大纲数据
const loadCourseOutline = async () => {
  try {
    loading.value = true
    const courseId = route.params.courseId
    const response = await request.get(`/course/${courseId}/outline`)
    
    if (response.code === 0) {
      courseOutline.value = formatOutlineForTree(response.data)
      
      // 默认展开第一个章节
      if (courseOutline.value.length > 0) {
        expandedKeys.value = [courseOutline.value[0].id]
      }
    } else {
      ElMessage.error(response.msg || '获取课程大纲失败')
    }
  } catch (error) {
    console.error('获取课程大纲失败:', error)
    ElMessage.error('获取课程大纲失败')
  } finally {
    loading.value = false
  }
}

// 获取课程信息
const loadCourseInfo = async () => {
  try {
    const courseId = route.params.courseId
    const response = await request.get(`/course/${courseId}`)
    
    if (response.code === 0) {
      course.value = response.data
    }
  } catch (error) {
    console.error('获取课程信息失败:', error)
  }
}

// 格式化大纲数据为树形结构
const formatOutlineForTree = (sections) => {
  return sections.map(section => ({
    id: `section-${section.id}`,
    title: section.title,
    type: 'section',
    children: section.lessons?.map(lesson => ({
      id: lesson.id,
      title: lesson.title,
      type: lesson.type,
      duration: lesson.duration,
      isFree: lesson.isFree,
      videoUrl: lesson.videoUrl,
      documentUrl: lesson.documentUrl,
      description: lesson.description,
      quizData: lesson.quizData
    })) || []
  }))
}

// 处理节点点击
const handleNodeClick = (data) => {
  if (data.type !== 'section') {
    loadLesson(data)
  }
}

// 加载课时内容
const loadLesson = async (lesson) => {
  try {
    currentLesson.value = lesson
    currentLessonId.value = lesson.id
    
    // 记录学习进度
    await recordLearningProgress(lesson.id)
    
    // 更新学习进度
    await updateLearningProgress()
  } catch (error) {
    console.error('加载课时失败:', error)
  }
}

// 记录学习进度
const recordLearningProgress = async (lessonId) => {
  try {
    await request.post('/learning/progress', {
      lessonId: lessonId,
      courseId: route.params.courseId
    })
  } catch (error) {
    console.error('记录学习进度失败:', error)
  }
}

// 更新学习进度
const updateLearningProgress = async () => {
  try {
    const response = await request.get(`/learning/progress/${route.params.courseId}`)
    if (response.code === 0) {
      learningProgress.value = response.data.progress || 0
    }
  } catch (error) {
    console.error('获取学习进度失败:', error)
  }
}

// 视频相关事件
const onVideoLoaded = () => {
  // 视频加载完成
}

const onVideoTimeUpdate = () => {
  // 可以在这里记录视频观看进度
}

const onVideoEnded = () => {
  ElMessage.success('视频学习完成！')
  // 标记课时为已完成
  markLessonCompleted()
}

// 标记课时完成
const markLessonCompleted = async () => {
  try {
    await request.post('/learning/complete', {
      lessonId: currentLesson.value.id,
      courseId: route.params.courseId
    })
    await updateLearningProgress()
  } catch (error) {
    console.error('标记课时完成失败:', error)
  }
}

// 测验完成回调
const onQuizCompleted = (result) => {
  ElMessage.success(`测验完成！得分：${result.score}`)
  markLessonCompleted()
}

// 获取课时类型颜色
const getLessonTypeColor = (type) => {
  const colors = {
    video: 'primary',
    document: 'success',
    quiz: 'warning'
  }
  return colors[type] || 'info'
}

// 获取课时类型文本
const getLessonTypeText = (type) => {
  const texts = {
    video: '视频',
    document: '文档',
    quiz: '测验'
  }
  return texts[type] || '其他'
}

// 获取文档查看器URL
const getDocumentViewerUrl = (documentUrl) => {
  // 使用Google Docs Viewer或其他在线文档查看器
  return `https://docs.google.com/gview?url=${encodeURIComponent(documentUrl)}&embedded=true`
}

// 下载文档
const downloadDocument = () => {
  if (currentLesson.value.documentUrl) {
    window.open(currentLesson.value.documentUrl, '_blank')
  }
}

// 切换全屏
const toggleFullscreen = () => {
  if (!isFullscreen.value) {
    if (videoPlayer.value.requestFullscreen) {
      videoPlayer.value.requestFullscreen()
    }
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

// 返回课程
const goBack = () => {
  router.push(`/course/${route.params.courseId}`)
}

// 组件挂载
onMounted(() => {
  loadCourseInfo()
  loadCourseOutline()
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

// 组件卸载
onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style scoped>
.learning-page {
  min-height: 100vh;
  background: #f5f5f5;
}


.learning-container {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 60px);
}

/* 左侧课程大纲 */
.course-sidebar {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #f8f9fa;
}

.sidebar-header h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 1.1rem;
  line-height: 1.4;
}

.outline-tree {
  padding: 10px 0;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 8px 20px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.tree-node:hover {
  background: #f0f9ff;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.node-title {
  font-size: 0.9rem;
  color: #333;
}

.node-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.duration {
  font-size: 0.8rem;
  color: #999;
}

/* 右侧学习内容 */
.learning-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.loading-container {
  padding: 40px;
}

.lesson-content {
  height: 100%;
}

.lesson-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #f8f9fa;
}

.lesson-info h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 1.3rem;
}

.lesson-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.lesson-meta .duration {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  font-size: 0.9rem;
}

.lesson-description {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.lesson-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

/* 学习内容区域 */
.content-area {
  padding: 20px;
  min-height: 400px;
}

.content-area.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background: black;
  padding: 0;
}

.video-player {
  width: 100%;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-element {
  width: 100%;
  height: 500px;
  background: #000;
}

.no-video {
  height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.document-viewer {
  width: 100%;
}

.document-content {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.document-iframe {
  width: 100%;
  height: 600px;
  border: none;
}

.document-actions {
  padding: 15px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
  text-align: center;
}

.no-document {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quiz-content {
  min-height: 400px;
}

.other-content {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 学习进度 */
.learning-progress {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  background: #f8f9fa;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 0.9rem;
  color: #666;
}

.no-lesson {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .learning-container {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .course-sidebar {
    order: 2;
  }
  
  .learning-content {
    order: 1;
  }
}

@media (max-width: 768px) {
  .learning-container {
    padding: 10px;
  }
  
  .lesson-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .video-element {
    height: 300px;
  }
  
  .document-iframe {
    height: 400px;
  }
}
</style>
