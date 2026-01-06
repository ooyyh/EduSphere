<template>
  <div class="my-courses-container">
    <div class="page-header">
      <h2>我的课程</h2>
      <p class="page-description">查看您已购买的所有课程</p>
    </div>
    
    <div class="courses-content">
      <!-- 搜索和筛选 -->
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称"
          class="search-input"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select
          v-model="selectedCategory"
          placeholder="选择分类"
          class="category-select"
          @change="handleCategoryChange"
        >
          <el-option label="全部分类" value="" />
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
      </div>
      
      <!-- 课程列表 -->
      <div class="courses-list" v-loading="loading">
        <div v-if="filteredCourses.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无已购买的课程">
            <el-button type="primary" @click="goToCourses">去购买课程</el-button>
          </el-empty>
        </div>
        
        <div v-else class="courses-grid">
          <div
            v-for="course in filteredCourses"
            :key="course.id"
            class="course-card"
            @click="goToCourseDetail(course.courseId)"
          >
            <div class="course-cover">
              <img
                :src="getImageUrl(course.coverImage) || '/default-course.jpg'"
                :alt="course.courseTitle"
                @error="handleImageError"
              />
              <div class="course-status">
                <el-tag type="success" size="small">已购买</el-tag>
              </div>
            </div>
            
            <div class="course-info">
              <h3 class="course-title">{{ course.courseTitle }}</h3>
              <p class="course-instructor">讲师：{{ course.instructorName }}</p>
              <div class="course-meta">
                <span class="course-price">¥{{ course.purchasePrice }}</span>
                <span class="course-date">{{ formatDate(course.createdAt) }}</span>
              </div>
            </div>
            
            <div class="course-actions">
              <el-button type="primary" size="small" @click.stop="startLearning(course)">
                <el-icon><VideoPlay /></el-icon>
                开始学习
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-section" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, VideoPlay } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { formatDate } from '@/utils/dateFormat'
import { getImageUrl } from '@/utils/imageUtils'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const courses = ref([])
const categories = ref([])
const searchKeyword = ref('')
const selectedCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 计算属性
const filteredCourses = computed(() => {
  let filtered = courses.value
  
  // 按关键词搜索
  if (searchKeyword.value) {
    filtered = filtered.filter(course =>
      course.courseTitle.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  
  // 按分类筛选
  if (selectedCategory.value) {
    filtered = filtered.filter(course => course.categoryId === selectedCategory.value)
  }
  
  return filtered
})

// 获取我的课程
const getMyCourses = async () => {
  try {
    loading.value = true
    const response = await request.get('/purchase/my-courses')
    if (response.code === 0) {
      courses.value = response.data || []
      total.value = courses.value.length
    } else {
      ElMessage.error(response.msg || '获取课程失败')
    }
  } catch (error) {
    console.error('获取我的课程失败:', error)
    ElMessage.error('获取课程失败，请重试')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getCategories = async () => {
  try {
    const response = await request.get('/category/list')
    if (response.code === 0) {
      categories.value = response.data || []
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 搜索处理
const handleSearch = () => {
  // 实时搜索，不需要额外处理
}

// 分类筛选处理
const handleCategoryChange = () => {
  // 分类筛选，不需要额外处理
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  // 这里可以实现服务端分页，目前是前端分页
}

// 跳转到课程详情
const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

// 跳转到课程列表
const goToCourses = () => {
  router.push('/courses')
}

// 开始学习
const startLearning = (course) => {
  router.push(`/course/${course.courseId}/learn`)
}

// 图片加载错误处理
const handleImageError = (event) => {
  event.target.src = '/default-course.jpg'
}

// 组件挂载时获取数据
onMounted(() => {
  getMyCourses()
  getCategories()
})
</script>

<style scoped>
.my-courses-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.page-header h2 {
  color: #333;
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.page-description {
  color: #666;
  margin: 0;
  font-size: 16px;
}

.filter-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.search-input {
  flex: 1;
  max-width: 300px;
}

.category-select {
  width: 200px;
}

.courses-list {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.course-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.course-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-cover img {
  transform: scale(1.05);
}

.course-status {
  position: absolute;
  top: 10px;
  right: 10px;
}

.course-info {
  padding: 20px;
}

.course-title {
  color: #333;
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-instructor {
  color: #666;
  margin: 0 0 15px 0;
  font-size: 14px;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.course-price {
  color: #67c23a;
  font-size: 16px;
  font-weight: 600;
}

.course-date {
  color: #999;
  font-size: 12px;
}

.course-actions {
  padding: 0 20px 20px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

:deep(.el-input__inner) {
  height: 40px;
}

:deep(.el-button) {
  height: 40px;
  font-weight: 500;
}
</style>
