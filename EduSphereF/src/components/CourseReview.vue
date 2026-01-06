<template>
  <div class="course-review">
    <div class="review-header">
      <h3 class="review-title">课程评价</h3>
      <div class="review-stats">
        <div class="rating-summary">
          <div class="rating-score">
            <span class="score-value">{{ averageRating.toFixed(1) }}</span>
            <el-rate
              :model-value="averageRating"
              disabled
              show-score
              text-color="#ff9900"
              score-template=" "
            />
          </div>
          <div class="rating-count">{{ totalReviews }} 条评价</div>
        </div>
        <div class="rating-distribution">
          <div
            v-for="star in [5, 4, 3, 2, 1]"
            :key="star"
            class="distribution-item"
          >
            <span class="star-label">{{ star }}星</span>
            <el-progress
              :percentage="getStarPercentage(star)"
              :show-text="false"
              :stroke-width="8"
              color="#ff9900"
            />
            <span class="star-count">{{ getStarCount(star) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 评价筛选和排序 -->
    <div class="review-filter">
      <el-radio-group v-model="sortType" @change="handleSortChange">
        <el-radio-button label="latest">最新</el-radio-button>
        <el-radio-button label="helpful">最有帮助</el-radio-button>
        <el-radio-button label="rating">评分最高</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 评价列表 -->
    <div class="review-list">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- 空评价 -->
      <div v-else-if="reviews.length === 0" class="empty-reviews">
        <el-empty description="暂无评价">
          <el-button v-if="canReview" type="primary" @click="showReviewForm = true">
            写第一条评价
          </el-button>
        </el-empty>
      </div>

      <!-- 评价项 -->
      <div v-else class="reviews-content">
        <div
          v-for="review in paginatedReviews"
          :key="review.id"
          class="review-item"
        >
          <div class="review-user">
            <el-avatar :size="50" :src="review.userAvatar">
              {{ review.username ? review.username.charAt(0) : 'U' }}
            </el-avatar>
            <div class="user-info">
              <div class="username">{{ review.username || '匿名用户' }}</div>
              <div class="review-time">{{ formatRelativeTime(review.createdAt) }}</div>
            </div>
          </div>
          <div class="review-content">
            <div class="review-rating">
              <el-rate
                :model-value="review.rating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}"
              />
            </div>
            <div class="review-text">{{ review.content }}</div>
            <div class="review-actions">
              <el-button
                text
                size="small"
                @click="handleLike(review)"
                :loading="likingReviewId === review.id"
              >
                <el-icon><GoodsFilled /></el-icon>
                有帮助 ({{ review.likeCount || 0 }})
              </el-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="reviews.length"
            layout="prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>

    <!-- 写评价按钮 -->
    <div v-if="canReview && !showReviewForm" class="write-review-btn">
      <el-button type="primary" size="large" @click="showReviewForm = true">
        <el-icon><EditPen /></el-icon>
        写评价
      </el-button>
    </div>

    <!-- 评价表单 -->
    <el-dialog
      v-model="showReviewForm"
      title="写评价"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="reviewFormRef"
        :model="reviewForm"
        :rules="reviewRules"
        label-width="80px"
      >
        <el-form-item label="课程评分" prop="rating">
          <el-rate
            v-model="reviewForm.rating"
            show-text
            :texts="['非常差', '差', '一般', '好', '非常好']"
            size="large"
          />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="6"
            placeholder="请分享您的学习体验，帮助其他学员更好地了解这门课程..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewForm = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitting"
          @click="handleSubmitReview"
        >
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { GoodsFilled, EditPen } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { formatRelativeTime } from '@/utils/dateFormat'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  courseId: {
    type: [Number, String],
    required: true
  },
  canReview: {
    type: Boolean,
    default: false
  }
})

const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const reviews = ref([])
const sortType = ref('latest')
const currentPage = ref(1)
const pageSize = ref(5)
const showReviewForm = ref(false)
const submitting = ref(false)
const likingReviewId = ref(null)
const reviewFormRef = ref(null)

// 评价表单
const reviewForm = ref({
  rating: 5,
  content: ''
})

// 表单验证规则
const reviewRules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 10, message: '评价内容至少10个字', trigger: 'blur' }
  ]
}

// 计算属性 - 平均评分
const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((acc, review) => acc + review.rating, 0)
  return sum / reviews.value.length
})

// 计算属性 - 总评价数
const totalReviews = computed(() => {
  return reviews.value.length
})

// 计算属性 - 排序后的评价
const sortedReviews = computed(() => {
  const sorted = [...reviews.value]
  switch (sortType.value) {
    case 'latest':
      return sorted.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    case 'helpful':
      return sorted.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
    case 'rating':
      return sorted.sort((a, b) => b.rating - a.rating)
    default:
      return sorted
  }
})

// 计算属性 - 分页后的评价
const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return sortedReviews.value.slice(start, end)
})

// 获取星级数量
const getStarCount = (star) => {
  return reviews.value.filter(review => review.rating === star).length
}

// 获取星级百分比
const getStarPercentage = (star) => {
  if (totalReviews.value === 0) return 0
  return (getStarCount(star) / totalReviews.value) * 100
}

// 加载评价列表
const loadReviews = async () => {
  try {
    loading.value = true
    const response = await request.get(`/review/course/${props.courseId}`)

    if (response.code === 0) {
      reviews.value = response.data || []
    } else {
      console.error('获取评价失败:', response.msg)
    }
  } catch (error) {
    console.error('获取评价失败:', error)
  } finally {
    loading.value = false
  }
}

// 排序改变
const handleSortChange = () => {
  currentPage.value = 1
}

// 分页改变
const handlePageChange = () => {
  // 滚动到评价区域顶部
  const reviewElement = document.querySelector('.course-review')
  if (reviewElement) {
    reviewElement.scrollIntoView({ behavior: 'smooth' })
  }
}

// 点赞评价
const handleLike = async (review) => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    likingReviewId.value = review.id

    const response = await request.post(`/review/${review.id}/like`)

    if (response.code === 0) {
      review.likeCount = (review.likeCount || 0) + 1
      ElMessage.success('感谢您的反馈')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    likingReviewId.value = null
  }
}

// 提交评价
const handleSubmitReview = async () => {
  if (!reviewFormRef.value) return

  try {
    const valid = await reviewFormRef.value.validate()
    if (!valid) return

    submitting.value = true

    const response = await request.post(`/review/course/${props.courseId}`, {
      rating: reviewForm.value.rating,
      content: reviewForm.value.content
    })

    if (response.code === 0) {
      ElMessage.success('评价提交成功')
      showReviewForm.value = false
      reviewForm.value = { rating: 5, content: '' }
      await loadReviews()
    } else {
      ElMessage.error(response.msg || '评价提交失败')
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    ElMessage.error('评价提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.course-review {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  margin-top: 40px;
}

/* 评价头部 */
.review-header {
  margin-bottom: 30px;
}

.review-title {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 24px;
}

.review-stats {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 40px;
}

/* 评分概览 */
.rating-summary {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.rating-score {
  margin-bottom: 12px;
}

.score-value {
  font-size: 3rem;
  font-weight: bold;
  color: #ff9900;
  display: block;
  margin-bottom: 8px;
}

.rating-count {
  color: #666;
  font-size: 0.9rem;
}

/* 评分分布 */
.rating-distribution {
  display: flex;
  flex-direction: column;
  gap: 12px;
  justify-content: center;
}

.distribution-item {
  display: grid;
  grid-template-columns: 50px 1fr 60px;
  align-items: center;
  gap: 12px;
}

.star-label {
  color: #666;
  font-size: 0.9rem;
}

.star-count {
  color: #999;
  font-size: 0.85rem;
  text-align: right;
}

/* 评价筛选 */
.review-filter {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

/* 评价列表 */
.review-list {
  margin-bottom: 30px;
}

.loading-container {
  padding: 40px 0;
}

.empty-reviews {
  text-align: center;
  padding: 60px 20px;
}

.reviews-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 评价项 */
.review-item {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 24px;
  padding: 24px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.review-item:hover {
  background: #e9ecef;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.review-user {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.user-info {
  text-align: center;
}

.username {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.review-time {
  font-size: 0.85rem;
  color: #999;
}

.review-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-rating {
  display: flex;
  align-items: center;
}

.review-text {
  color: #666;
  line-height: 1.6;
  font-size: 0.95rem;
}

.review-actions {
  display: flex;
  gap: 16px;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding-top: 20px;
}

/* 写评价按钮 */
.write-review-btn {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  border-top: 1px solid #e9ecef;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-review {
    padding: 20px;
  }

  .review-stats {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .review-item {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .review-user {
    flex-direction: row;
    justify-content: flex-start;
  }

  .user-info {
    text-align: left;
  }
}
</style>
