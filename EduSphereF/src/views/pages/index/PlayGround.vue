<template>
  <div class="playground">
    <!-- 顶部导航栏 -->
    <Header />
    
    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 轮播图区域 -->
      <section class="carousel-section">
        <el-carousel :interval="5000" height="500px" arrow="always" indicator-position="outside">
          <el-carousel-item v-for="(banner, index) in banners" :key="index">
            <div class="carousel-item-wrapper" :style="{ backgroundImage: `url(${banner.image})` }">
              <div class="carousel-overlay">
                <div class="carousel-content">
                  <h2 class="carousel-title">{{ banner.title }}</h2>
                  <p class="carousel-subtitle">{{ banner.subtitle }}</p>
                  <el-button type="primary" size="large" @click="handleBannerClick(banner)">
                    {{ banner.buttonText }}
                  </el-button>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- 快速入口区域 -->
      <section class="quick-access">
        <div class="container">
          <div class="access-grid">
            <div class="access-item" @click="goToCourses">
              <el-icon :size="40" color="#409EFF"><VideoPlay /></el-icon>
              <h4>浏览课程</h4>
              <p>海量优质课程等你来学</p>
            </div>
            <div class="access-item" @click="goTo('/my-courses')">
              <el-icon :size="40" color="#67C23A"><Reading /></el-icon>
              <h4>我的学习</h4>
              <p>继续你的学习之旅</p>
            </div>
            <div class="access-item" @click="goTo('/teacher')">
              <el-icon :size="40" color="#E6A23C"><Edit /></el-icon>
              <h4>成为讲师</h4>
              <p>分享你的知识和经验</p>
            </div>
            <div class="access-item" @click="goTo('/my-favorites')">
              <el-icon :size="40" color="#F56C6C"><Star /></el-icon>
              <h4>我的收藏</h4>
              <p>收藏喜欢的课程</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 特色课程区域 -->
      <section class="featured-courses">
        <div class="container">
          <h2 class="section-title">热门课程推荐</h2>
          <div class="courses-grid">
            <div 
              v-for="course in featuredCourses" 
              :key="course.id"
              class="course-card"
              @click="goToCourseDetail(course.id)"
            >
              <div class="course-image">
                <el-image
                  :src="getImageUrl(course.coverImage)"
                  alt="课程封面"
                  fit="cover"
                  class="course-img"
                />
                <div class="course-badge" v-if="course.isHot">
                  <el-tag type="danger" size="small">热门</el-tag>
                </div>
              </div>
              <div class="course-info">
                <h3 class="course-title">{{ course.title }}</h3>
                <p class="course-instructor">{{ course.instructor?.username || '未知讲师' }}</p>
                <div class="course-meta">
                  <div class="course-rating">
                    <el-rate
                      :model-value="course.rating || 0"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}"
                    />
                  </div>
                  <div class="course-price">
                    <span v-if="course.isFree" class="current-price">免费</span>
                    <template v-else>
                      <span class="current-price">¥{{ course.price || 0 }}</span>
                      <span v-if="course.originalPrice" class="original-price">¥{{ course.originalPrice }}</span>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="view-more">
            <el-button type="primary" plain @click="goToCourses">查看更多课程</el-button>
          </div>
        </div>
      </section>

      <!-- 课程分类区域 -->
      <section class="categories-section">
        <div class="container">
          <h2 class="section-title">热门分类</h2>
          <div class="categories-grid">
            <div 
              v-for="category in categories" 
              :key="category.id"
              class="category-card"
              @click="goToCategory(category.slug)"
            >
              <div class="category-icon">
                <el-icon :size="40" :color="category.color || '#409EFF'">
                  <Monitor />
                </el-icon>
              </div>
              <h3 class="category-title">{{ category.name }}</h3>
              <p class="category-count">0 门课程</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 统计数据区域 -->
      <section class="stats-section">
        <div class="container">
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalCourses }}</div>
              <div class="stat-label">优质课程</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalStudents }}</div>
              <div class="stat-label">学员总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalInstructors }}</div>
              <div class="stat-label">专业讲师</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalHours }}</div>
              <div class="stat-label">学习时长</div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h4>关于EduSphere</h4>
            <p>专业的在线教育平台，致力于为学习者提供优质的课程内容和学习体验。</p>
          </div>
          <div class="footer-section">
            <h4>快速链接</h4>
            <ul>
              <li><router-link to="/courses">课程浏览</router-link></li>
              <li><router-link to="/about">关于我们</router-link></li>
              <li><router-link to="/contact">联系我们</router-link></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4>联系方式</h4>
            <p>邮箱: contact@edusphere.com</p>
            <p>电话: 400-123-4567</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 EduSphere. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import request from '@/utils/request'
import { Search, User, Monitor, Brush, TrendCharts, ChatDotRound, VideoPlay, Reading, Edit, Star } from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/imageUtils'

const router = useRouter()

// 轮播图数据
const banners = ref([
  {
    title: '探索知识的无限可能',
    subtitle: '在EduSphere，发现优质课程，提升专业技能，开启学习新旅程',
    image: 'https://picsum.photos/1920/500?random=1',
    buttonText: '立即开始',
    link: '/courses'
  },
  {
    title: '成为优秀的讲师',
    subtitle: '分享你的知识和经验，帮助更多人成长',
    image: 'https://picsum.photos/1920/500?random=2',
    buttonText: '申请成为讲师',
    link: '/teacher'
  },
  {
    title: '免费精品课程',
    subtitle: '海量免费课程，助力你的职业发展',
    image: 'https://picsum.photos/1920/500?random=3',
    buttonText: '查看免费课程',
    link: '/courses?type=free'
  }
])

// 特色课程数据
const featuredCourses = ref([])

// 课程分类数据
const categories = ref([])

// 统计数据
const stats = ref({
  totalCourses: '1000+',
  totalStudents: '50000+',
  totalInstructors: '500+',
  totalHours: '100000+'
})

// 处理轮播图点击
const handleBannerClick = (banner) => {
  if (banner.link) {
    router.push(banner.link)
  }
}

// 通用跳转方法
const goTo = (path) => {
  router.push(path)
}

// 页面跳转方法
const goToCourses = () => {
  router.push('/courses')
}

const goToRegister = () => {
  router.push('/account/register')
}

const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

const goToCategory = (categorySlug) => {
  router.push(`/courses?category=${categorySlug}`)
}

// 获取首页数据
const loadHomeData = async () => {
  try {
    const response = await request.get('/home/data')
    if (response.code === 0) {
      featuredCourses.value = response.data.hotCourses || []
      categories.value = response.data.categories || []
      if (response.data.stats) {
        stats.value = response.data.stats
      }
    }
  } catch (error) {
    console.error('获取首页数据失败:', error)
  }
}

onMounted(() => {
  loadHomeData()
})
</script>

<style scoped>
.playground {
  min-height: 100vh;
  background: #f8f9fa;
}

.main-content {
  padding-top: 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 轮播图区域样式 */
.carousel-section {
  width: 100%;
}

.carousel-item-wrapper {
  width: 100%;
  height: 500px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.8) 0%, rgba(118, 75, 162, 0.8) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-content {
  text-align: center;
  color: white;
  max-width: 800px;
  padding: 0 20px;
}

.carousel-title {
  font-size: 3.5rem;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.carousel-subtitle {
  font-size: 1.3rem;
  margin-bottom: 40px;
  line-height: 1.6;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.2);
}

/* 快速入口区域 */
.quick-access {
  padding: 60px 0;
  background: white;
}

.access-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.access-item {
  text-align: center;
  padding: 40px 20px;
  border-radius: 12px;
  background: #f8f9fa;
  transition: all 0.3s;
  cursor: pointer;
}

.access-item:hover {
  background: white;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.access-item h4 {
  margin: 16px 0 8px;
  color: #333;
  font-size: 1.2rem;
}

.access-item p {
  color: #666;
  font-size: 0.9rem;
  margin: 0;
}

/* 特色课程区域 */
.featured-courses {
  padding: 80px 0;
  background: white;
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 60px;
  color: #333;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
  margin-bottom: 60px;
}

.course-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-5px);
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

.course-badge {
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
}

.course-instructor {
  color: #666;
  margin-bottom: 16px;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-price {
  display: flex;
  align-items: center;
  gap: 8px;
}

.current-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  text-decoration: line-through;
  color: #999;
}

.view-more {
  text-align: center;
}

/* 分类区域 */
.categories-section {
  padding: 80px 0;
  background: #f8f9fa;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.category-card {
  background: white;
  padding: 40px 20px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
  cursor: pointer;
}

.category-card:hover {
  transform: translateY(-5px);
}

.category-icon {
  margin-bottom: 20px;
}

.category-title {
  font-size: 1.3rem;
  margin-bottom: 10px;
  color: #333;
}

.category-count {
  color: #666;
}

/* 统计区域 */
.stats-section {
  padding: 80px 0;
  background: #2c3e50;
  color: white;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 3rem;
  font-weight: bold;
  margin-bottom: 10px;
  color: #3498db;
}

.stat-label {
  font-size: 1.1rem;
  opacity: 0.9;
}

/* 页脚 */
.footer {
  background: #34495e;
  color: white;
  padding: 60px 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 40px;
}

.footer-section h4 {
  margin-bottom: 20px;
  color: #3498db;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 10px;
}

.footer-section ul li a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-section ul li a:hover {
  color: #3498db;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #4a5f7a;
  color: #bdc3c7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-container {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
  
  .courses-grid {
    grid-template-columns: 1fr;
  }
  
  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .categories-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>