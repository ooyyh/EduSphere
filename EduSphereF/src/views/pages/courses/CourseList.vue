<!--
  课程列表页面

  功能说明：
  - 展示所有已发布的课程
  - 支持多维度筛选（分类、价格、难度）
  - 支持搜索和排序功能
  - 网格视图和列表视图切换
  - 支持课程购买和学习
  - 分页显示课程

  作者：EduSphere团队
  最后更新：2025-11-26
-->
<template>
  <div class="course-list">
    <!-- 顶部导航栏 -->
    <Header />
    
    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 页面标题和搜索区域 -->
      <section class="page-header">
        <div class="container">
          <div class="header-content">
            <div class="title-section">
              <h1 class="page-title">课程浏览</h1>
              <p class="page-subtitle">发现适合你的优质课程</p>
            </div>
            <div class="search-section">
              <el-input
                v-model="searchQuery"
                placeholder="搜索课程名称、讲师或关键词..."
                class="search-input"
                size="large"
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
          </div>
        </div>
      </section>

      <!-- 筛选和排序区域 -->
      <section class="filter-section">
        <div class="container">
          <div class="filter-bar">
            <!-- 分类筛选 -->
            <div class="filter-group">
              <label class="filter-label">分类：</label>
              <el-select
                v-model="selectedCategory"
                placeholder="选择分类"
                @change="handleCategoryChange"
                clearable
              >
                <el-option
                  v-for="category in categories"
                  :key="category.value"
                  :label="category.label"
                  :value="category.value"
                />
              </el-select>
            </div>

            <!-- 价格筛选 -->
            <div class="filter-group">
              <label class="filter-label">价格：</label>
              <el-select
                v-model="selectedPriceRange"
                placeholder="选择价格范围"
                @change="handlePriceChange"
                clearable
              >
                <el-option label="免费" value="free" />
                <el-option label="0-100元" value="0-100" />
                <el-option label="100-300元" value="100-300" />
                <el-option label="300-500元" value="300-500" />
                <el-option label="500元以上" value="500+" />
              </el-select>
            </div>

            <!-- 难度筛选 -->
            <div class="filter-group">
              <label class="filter-label">难度：</label>
              <el-select
                v-model="selectedLevel"
                placeholder="选择难度"
                @change="handleLevelChange"
                clearable
              >
                <el-option label="入门" value="beginner" />
                <el-option label="进阶" value="intermediate" />
                <el-option label="高级" value="advanced" />
              </el-select>
            </div>

            <!-- 排序 -->
            <div class="filter-group">
              <label class="filter-label">排序：</label>
              <el-select
                v-model="sortBy"
                placeholder="排序方式"
                @change="handleSortChange"
              >
                <el-option label="最新发布" value="newest" />
                <el-option label="最受欢迎" value="popular" />
                <el-option label="评分最高" value="rating" />
                <el-option label="价格从低到高" value="price-asc" />
                <el-option label="价格从高到低" value="price-desc" />
              </el-select>
            </div>

            <!-- 清除筛选 -->
            <el-button @click="clearFilters" type="text">
              <el-icon><RefreshLeft /></el-icon>
              清除筛选
            </el-button>
          </div>
        </div>
      </section>

      <!-- 课程列表区域 -->
      <section class="courses-section">
        <div class="container">
          <!-- 结果统计 -->
          <div class="results-info">
            <span>共找到 {{ totalCourses }} 门课程</span>
            <div class="view-toggle">
              <el-button-group>
                <el-button 
                  :type="viewMode === 'grid' ? 'primary' : 'default'"
                  @click="viewMode = 'grid'"
                >
                  <el-icon><Grid /></el-icon>
                </el-button>
                <el-button 
                  :type="viewMode === 'list' ? 'primary' : 'default'"
                  @click="viewMode = 'list'"
                >
                  <el-icon><List /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="6" animated />
          </div>

          <!-- 课程网格视图 -->
          <div v-else-if="viewMode === 'grid'" class="courses-grid">
            <div 
              v-for="course in courses" 
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
                <div class="course-badges">
                  <el-tag v-if="course.isHot" type="danger" size="small">热门</el-tag>
                  <el-tag v-if="course.isNew" type="success" size="small">新课</el-tag>
                  <el-tag v-if="course.isFree" type="info" size="small">免费</el-tag>
                </div>
              </div>
              <div class="course-info">
                <h3 class="course-title">{{ course.title }}</h3>
                <p class="course-instructor">{{ course.instructor?.username || '未知讲师' }}</p>
                <p class="course-description">{{ course.description }}</p>
                <div class="course-meta">
                  <div class="course-rating">
                    <el-rate
                      :model-value="course.rating || 0"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}"
                    />
                    <span class="rating-count">({{ course.ratingCount || 0 }})</span>
                  </div>
                  <div class="course-stats">
                    <span class="student-count">{{ course.studentCount || 0 }} 学员</span>
                    <span class="duration">{{ course.duration || '0小时' }}</span>
                  </div>
                </div>
                <div class="course-footer">
                  <div class="course-price">
                    <span v-if="course.isFree" class="free-price">免费</span>
                    <template v-else>
                      <span class="current-price">¥{{ course.price || 0 }}</span>
                      <span v-if="course.originalPrice" class="original-price">¥{{ course.originalPrice }}</span>
                    </template>
                  </div>
                  <div class="course-actions">
                    <el-button 
                      v-if="course.isFree || course.purchased" 
                      :type="course.purchased ? 'success' : 'primary'"
                      size="small"
                      @click.stop="startLearning(course)"
                    >
                      <el-icon><VideoPlay /></el-icon>
                      {{ course.purchased ? '继续学习' : '免费学习' }}
                    </el-button>
                    <el-button 
                      v-else
                      type="success" 
                      size="small"
                      :loading="purchasingCourseId === course.id"
                      @click.stop="handlePurchase(course)"
                    >
                      <el-icon><ShoppingCart /></el-icon>
                      立即购买
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 课程列表视图 -->
          <div v-else class="courses-list">
            <div 
              v-for="course in courses" 
              :key="course.id"
              class="course-item"
              @click="goToCourseDetail(course.id)"
            >
              <div class="course-image">
                <el-image
                  :src="getImageUrl(course.coverImage)"
                  alt="课程封面"
                  fit="cover"
                  class="course-img"
                />
              </div>
              <div class="course-content">
                <div class="course-header">
                  <h3 class="course-title">{{ course.title }}</h3>
                  <div class="course-badges">
                    <el-tag v-if="course.isHot" type="danger" size="small">热门</el-tag>
                    <el-tag v-if="course.isNew" type="success" size="small">新课</el-tag>
                    <el-tag v-if="course.isFree" type="info" size="small">免费</el-tag>
                  </div>
                </div>
                <p class="course-instructor">讲师：{{ course.instructor?.username || '未知讲师' }}</p>
                <p class="course-description">{{ course.description }}</p>
                <div class="course-meta">
                  <div class="course-rating">
                    <el-rate
                      :model-value="course.rating || 0"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}"
                    />
                    <span class="rating-count">({{ course.ratingCount || 0 }})</span>
                  </div>
                  <div class="course-stats">
                    <span class="student-count">{{ course.studentCount || 0 }} 学员</span>
                    <span class="duration">{{ course.duration || '0小时' }}</span>
                    <el-tag :type="getLevelType(course.level)" size="small">
                      {{ getLevelText(course.level) }}
                    </el-tag>
                  </div>
                </div>
              </div>
              <div class="course-price-section">
                <div class="course-price">
                  <span v-if="course.isFree" class="free-price">免费</span>
                  <template v-else>
                    <span class="current-price">¥{{ course.price || 0 }}</span>
                    <span v-if="course.originalPrice" class="original-price">¥{{ course.originalPrice }}</span>
                  </template>
                </div>
                <el-button 
                  v-if="course.isFree || course.purchased" 
                  :type="course.purchased ? 'success' : 'primary'"
                  size="small"
                  @click.stop="startLearning(course)"
                >
                  <el-icon><VideoPlay /></el-icon>
                  {{ course.purchased ? '继续学习' : '免费学习' }}
                </el-button>
                <el-button 
                  v-else
                  type="success" 
                  size="small"
                  :loading="purchasingCourseId === course.id"
                  @click.stop="handlePurchase(course)"
                >
                  <el-icon><ShoppingCart /></el-icon>
                  立即购买
                </el-button>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[12, 24, 48]"
              :total="totalCourses"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import request from '@/utils/request'
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import { Search, RefreshLeft, Grid, List, VideoPlay, ShoppingCart } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getImageUrl } from '@/utils/imageUtils'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const purchasingCourseId = ref(null)
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedPriceRange = ref('')
const selectedLevel = ref('')
const sortBy = ref('newest')
const viewMode = ref('grid')
const currentPage = ref(1)
const pageSize = ref(12)
const totalCourses = ref(0)

// 分类选项
const categories = ref([
  { label: '编程开发', value: 'programming' },
  { label: '设计创意', value: 'design' },
  { label: '商业管理', value: 'business' },
  { label: '语言学习', value: 'language' },
  { label: '数据科学', value: 'data-science' },
  { label: '人工智能', value: 'ai' }
])

// 课程数据
const courses = ref([])

// 计算属性
const filteredCourses = computed(() => {
  let result = [...courses.value]
  
  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(course => 
      course.title.toLowerCase().includes(query) ||
      (course.instructor?.username || '').toLowerCase().includes(query) ||
      course.description.toLowerCase().includes(query)
    )
  }
  
  // 分类筛选
  if (selectedCategory.value) {
    result = result.filter(course => course.categoryId === selectedCategory.value)
  }
  
  // 价格筛选
  if (selectedPriceRange.value) {
    result = result.filter(course => {
      if (selectedPriceRange.value === 'free') return course.isFree
      const [min, max] = selectedPriceRange.value.split('-').map(v => v.replace('+', ''))
      const price = parseFloat(course.price) || 0
      if (max) {
        return price >= parseInt(min) && price <= parseInt(max)
      } else {
        return price >= parseInt(min)
      }
    })
  }
  
  // 难度筛选
  if (selectedLevel.value) {
    result = result.filter(course => course.level === selectedLevel.value)
  }
  
  // 排序
  result.sort((a, b) => {
    switch (sortBy.value) {
      case 'newest':
        return (b.isNew ? 1 : 0) - (a.isNew ? 1 : 0)
      case 'popular':
        return (b.studentCount || 0) - (a.studentCount || 0)
      case 'rating':
        return (b.rating || 0) - (a.rating || 0)
      case 'price-asc':
        return (parseFloat(a.price) || 0) - (parseFloat(b.price) || 0)
      case 'price-desc':
        return (parseFloat(b.price) || 0) - (parseFloat(a.price) || 0)
      default:
        return 0
    }
  })
  
  totalCourses.value = result.length
  return result
})

// 方法
const handleSearch = () => {
  currentPage.value = 1
  loadCourses()
}

const handleCategoryChange = () => {
  currentPage.value = 1
  loadCourses()
}

const handlePriceChange = () => {
  currentPage.value = 1
  loadCourses()
}

const handleLevelChange = () => {
  currentPage.value = 1
  loadCourses()
}

const handleSortChange = () => {
  currentPage.value = 1
  loadCourses()
}

const clearFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = ''
  selectedPriceRange.value = ''
  selectedLevel.value = ''
  sortBy.value = 'newest'
  currentPage.value = 1
  loadCourses()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

// 开始学习课程
const startLearning = (course) => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/account/login')
    return
  }
  
  if (!course.purchased && !course.isFree) {
    ElMessage.warning('请先购买课程')
    return
  }
  
  router.push(`/learning/${course.id}`)
}

// 处理购买课程
const handlePurchase = async (course) => {
  // 检查用户是否登录
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    // 确认购买
    const confirmed = await ElMessageBox.confirm(
      `确认购买课程《${course.title}》吗？\n价格：¥${course.price}`,
      '确认购买',
      {
        confirmButtonText: '确认购买',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (confirmed) {
      purchasingCourseId.value = course.id
      
      const response = await request.post('/purchase/course', {
        courseId: course.id
      })
      
      if (response.code === 0) {
        ElMessage.success('购买成功！')
        // 更新课程状态为已购买
        course.purchased = true
        // 不需要重新加载整个课程列表，只需要更新当前课程状态
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
    purchasingCourseId.value = null
  }
}

const getLevelType = (level) => {
  const types = {
    beginner: 'success',
    intermediate: 'warning',
    advanced: 'danger'
  }
  return types[level] || 'info'
}

const getLevelText = (level) => {
  const texts = {
    beginner: '入门',
    intermediate: '进阶',
    advanced: '高级'
  }
  return texts[level] || '未知'
}

// 监听路由参数变化
watch(() => route.query, (newQuery) => {
  if (newQuery.search) {
    searchQuery.value = newQuery.search
  }
  if (newQuery.category) {
    selectedCategory.value = newQuery.category
  }
}, { immediate: true })

// 检查课程购买状态
const checkPurchaseStatus = async () => {
  try {
    // 获取用户已购买的课程ID列表
    const response = await request.get('/purchase/my-courses')
    if (response.code === 0) {
      const purchasedCourseIds = response.data.map(record => record.courseId)
      
      // 更新课程列表中的购买状态
      courses.value.forEach(course => {
        course.purchased = purchasedCourseIds.includes(course.id)
      })
    }
  } catch (error) {
    console.error('检查购买状态失败:', error)
  }
}

// 获取课程数据
const loadCourses = async () => {
  try {
    loading.value = true
    const response = await request.post('/course/search', {
      keyword: searchQuery.value,
      category: selectedCategory.value,
      level: selectedLevel.value,
      priceRange: selectedPriceRange.value,
      sortBy: sortBy.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (response.code === 0) {
      courses.value = response.data.courses || []
      totalCourses.value = response.data.total || 0
      
      // 如果用户已登录，检查购买状态
      if (authStore.isLoggedIn) {
        await checkPurchaseStatus()
      }
    } else {
      console.error('获取课程数据失败:', response.msg)
    }
  } catch (error) {
    console.error('获取课程数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.course-list {
  min-height: 100vh;
  background: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 页面头部 */
.page-header {
  background: white;
  padding: 40px 0;
  border-bottom: 1px solid #e9ecef;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 40px;
}

.page-title {
  font-size: 2.5rem;
  margin-bottom: 10px;
  color: #333;
}

.page-subtitle {
  color: #666;
  font-size: 1.1rem;
}

.search-input {
  width: 400px;
}

/* 筛选区域 */
.filter-section {
  background: white;
  padding: 20px 0;
  border-bottom: 1px solid #e9ecef;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

/* 课程区域 */
.courses-section {
  padding: 30px 0;
}

.results-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  color: #666;
}

.loading-container {
  padding: 40px 0;
}

/* 网格视图 */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
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

.course-badges {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
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

.course-instructor {
  color: #666;
  margin-bottom: 12px;
  font-size: 0.9rem;
}

.course-description {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-meta {
  margin-bottom: 16px;
}

.course-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.rating-count {
  color: #999;
  font-size: 0.8rem;
}

.course-stats {
  display: flex;
  gap: 16px;
  font-size: 0.8rem;
  color: #999;
}

.course-footer {
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
  font-size: 0.9rem;
}

.free-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #27ae60;
}

/* 列表视图 */
.courses-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
}

.course-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.course-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.15);
}

.course-item .course-image {
  flex-shrink: 0;
  width: 200px;
  height: 120px;
}

.course-content {
  flex: 1;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.course-price-section {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .search-input {
    width: 100%;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .results-info {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .courses-grid {
    grid-template-columns: 1fr;
  }
  
  .course-item {
    flex-direction: column;
  }
  
  .course-item .course-image {
    width: 100%;
    height: 200px;
  }
  
  .course-price-section {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
}
</style>