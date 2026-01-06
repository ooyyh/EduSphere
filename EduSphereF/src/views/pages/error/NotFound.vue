<template>
  <div class="not-found">
    <!-- 顶部导航栏 -->
    <Header />
    
    <!-- 404错误内容 -->
    <main class="error-content">
      <div class="container">
        <div class="error-wrapper">
          <!-- 404图标和动画 -->
          <div class="error-visual">
            <div class="error-number">
              <span class="digit">4</span>
              <span class="digit zero">0</span>
              <span class="digit">4</span>
            </div>
            <div class="error-animation">
              <div class="floating-elements">
                <div class="element element-1"></div>
                <div class="element element-2"></div>
                <div class="element element-3"></div>
                <div class="element element-4"></div>
              </div>
            </div>
          </div>

          <!-- 错误信息 -->
          <div class="error-info">
            <h1 class="error-title">页面走丢了</h1>
            <p class="error-description">
              抱歉，您访问的页面不存在或已被移除。<br>
              可能是链接错误或页面已被删除。
            </p>
            
            <!-- 可能的原因 -->
            <div class="error-reasons">
              <h3>可能的原因：</h3>
              <ul>
                <li>网址输入错误</li>
                <li>页面已被删除或移动</li>
                <li>链接已过期</li>
                <li>您没有访问权限</li>
              </ul>
            </div>

            <!-- 操作按钮 -->
            <div class="error-actions">
              <el-button 
                type="primary" 
                size="large" 
                @click="goHome"
                class="action-btn"
              >
                <el-icon><House /></el-icon>
                返回首页
              </el-button>
              
              <el-button 
                size="large" 
                @click="goBack"
                class="action-btn"
              >
                <el-icon><ArrowLeft /></el-icon>
                返回上页
              </el-button>
              
              <el-button 
                size="large" 
                @click="goCourses"
                class="action-btn"
              >
                <el-icon><Reading /></el-icon>
                浏览课程
              </el-button>
            </div>

            <!-- 搜索建议 -->
            <div class="search-suggestion">
              <h3>或者试试搜索：</h3>
              <el-input
                v-model="searchQuery"
                placeholder="搜索课程、讲师或关键词..."
                size="large"
                class="search-input"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
                <template #suffix>
                  <el-button type="primary" @click="handleSearch">搜索</el-button>
                </template>
              </el-input>
            </div>

            <!-- 热门推荐 -->
            <div class="popular-suggestions">
              <h3>热门课程推荐：</h3>
              <div class="suggestion-tags">
                <el-tag
                  v-for="course in popularCourses"
                  :key="course.id"
                  class="suggestion-tag"
                  @click="goToCourse(course.id)"
                >
                  {{ course.title }}
                </el-tag>
              </div>
            </div>

            <!-- 联系支持 -->
            <div class="support-info">
              <p class="support-text">
                如果问题持续存在，请
                <el-button type="text" @click="contactSupport">联系客服</el-button>
                或发送邮件至
                <a href="mailto:support@edusphere.com" class="support-email">
                  support@edusphere.com
                </a>
              </p>
            </div>
          </div>
        </div>

        <!-- 相关链接 -->
        <div class="helpful-links">
          <h3>您可能需要：</h3>
          <div class="links-grid">
            <div class="link-card" @click="goHome">
              <el-icon class="link-icon"><House /></el-icon>
              <h4>首页</h4>
              <p>返回网站首页</p>
            </div>
            
            <div class="link-card" @click="goCourses">
              <el-icon class="link-icon"><Reading /></el-icon>
              <h4>课程浏览</h4>
              <p>查看所有课程</p>
            </div>
            
            <div class="link-card" @click="goLogin">
              <el-icon class="link-icon"><User /></el-icon>
              <h4>登录/注册</h4>
              <p>访问个人中心</p>
            </div>
            
            <div class="link-card" @click="contactSupport">
              <el-icon class="link-icon"><Service /></el-icon>
              <h4>客服支持</h4>
              <p>获取帮助和支持</p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import { 
  House, ArrowLeft, Reading, Search, User, Service 
} from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const searchQuery = ref('')

// 热门课程数据
const popularCourses = ref([
  { id: 1, title: 'Vue.js 3.0 完整教程' },
  { id: 2, title: 'Spring Boot 微服务' },
  { id: 3, title: 'UI/UX 设计实战' },
  { id: 4, title: 'Python 数据分析' },
  { id: 5, title: 'React 开发指南' },
  { id: 6, title: 'Node.js 后端开发' }
])

// 方法
const goHome = () => {
  router.push('/')
}

const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    goHome()
  }
}

const goCourses = () => {
  router.push('/courses')
}

const goLogin = () => {
  router.push('/login')
}

const goToCourse = (courseId) => {
  router.push(`/course/${courseId}`)
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({
      path: '/courses',
      query: { search: searchQuery.value }
    })
  } else {
    ElMessage.warning('请输入搜索关键词')
  }
}

const contactSupport = () => {
  ElMessage.info('客服功能开发中，请发送邮件至 support@edusphere.com')
}

// 页面加载动画
onMounted(() => {
  // 添加页面加载动画效果
  const digits = document.querySelectorAll('.digit')
  digits.forEach((digit, index) => {
    setTimeout(() => {
      digit.style.animation = 'bounceIn 0.6s ease-out'
    }, index * 200)
  })
})
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.not-found::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  pointer-events: none;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.error-content {
  padding: 60px 0;
  min-height: calc(100vh - 80px);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.error-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
  margin-bottom: 60px;
}

/* 404视觉效果 */
.error-visual {
  position: relative;
  text-align: center;
}

.error-number {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.digit {
  font-size: 8rem;
  font-weight: bold;
  color: white;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  display: inline-block;
  opacity: 0;
}

.digit.zero {
  position: relative;
  animation: float 3s ease-in-out infinite;
}

.digit.zero::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  height: 80%;
  border: 8px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.error-animation {
  position: relative;
  height: 200px;
}

.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.element {
  position: absolute;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  animation: float 4s ease-in-out infinite;
}

.element-1 {
  width: 20px;
  height: 20px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.element-2 {
  width: 30px;
  height: 30px;
  top: 60%;
  right: 20%;
  animation-delay: 1s;
}

.element-3 {
  width: 15px;
  height: 15px;
  bottom: 30%;
  left: 70%;
  animation-delay: 2s;
}

.element-4 {
  width: 25px;
  height: 25px;
  top: 10%;
  right: 10%;
  animation-delay: 3s;
}

/* 错误信息 */
.error-info {
  color: white;
}

.error-title {
  font-size: 3rem;
  margin-bottom: 20px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.error-description {
  font-size: 1.2rem;
  line-height: 1.6;
  margin-bottom: 30px;
  opacity: 0.9;
}

.error-reasons {
  background: rgba(255, 255, 255, 0.1);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 30px;
  backdrop-filter: blur(10px);
}

.error-reasons h3 {
  margin-bottom: 15px;
  font-size: 1.1rem;
}

.error-reasons ul {
  list-style: none;
  padding: 0;
}

.error-reasons li {
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
}

.error-reasons li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: rgba(255, 255, 255, 0.7);
}

.error-actions {
  display: flex;
  gap: 15px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.action-btn {
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.search-suggestion {
  margin-bottom: 30px;
}

.search-suggestion h3 {
  margin-bottom: 15px;
  font-size: 1.1rem;
}

.search-input {
  max-width: 400px;
}

.popular-suggestions {
  margin-bottom: 30px;
}

.popular-suggestions h3 {
  margin-bottom: 15px;
  font-size: 1.1rem;
}

.suggestion-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.suggestion-tag {
  cursor: pointer;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.suggestion-tag:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.support-info {
  text-align: center;
  opacity: 0.8;
}

.support-text {
  margin: 0;
}

.support-email {
  color: white;
  text-decoration: underline;
}

/* 相关链接 */
.helpful-links {
  background: rgba(255, 255, 255, 0.1);
  padding: 40px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.helpful-links h3 {
  color: white;
  text-align: center;
  margin-bottom: 30px;
  font-size: 1.5rem;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.link-card {
  background: rgba(255, 255, 255, 0.1);
  padding: 30px 20px;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(5px);
}

.link-card:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.link-icon {
  font-size: 2.5rem;
  color: white;
  margin-bottom: 15px;
}

.link-card h4 {
  color: white;
  margin-bottom: 10px;
  font-size: 1.2rem;
}

.link-card p {
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  font-size: 0.9rem;
}

/* 动画效果 */
@keyframes bounceIn {
  0% {
    opacity: 0;
    transform: scale(0.3) translateY(-50px);
  }
  50% {
    opacity: 1;
    transform: scale(1.1) translateY(0);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.3;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.6;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .error-wrapper {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }
  
  .error-title {
    font-size: 2rem;
  }
  
  .digit {
    font-size: 4rem;
  }
  
  .error-actions {
    justify-content: center;
  }
  
  .links-grid {
    grid-template-columns: 1fr;
  }
  
  .helpful-links {
    padding: 30px 20px;
  }
}

@media (max-width: 480px) {
  .error-number {
    gap: 10px;
  }
  
  .digit {
    font-size: 3rem;
  }
  
  .error-actions {
    flex-direction: column;
  }
  
  .action-btn {
    width: 100%;
  }
}
</style>