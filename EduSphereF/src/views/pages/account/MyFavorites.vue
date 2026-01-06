<template>
  <div class="my-favorites-page">
    <Header />

    <main class="main-content">
      <div class="container">
        <div class="page-header">
          <h1>我的收藏</h1>
          <p>您收藏的所有课程</p>
        </div>

        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="4" animated />
        </div>

        <div v-else-if="favorites.length === 0" class="empty-state">
          <el-empty description="还没有收藏任何课程">
            <el-button type="primary" @click="goToCourses">去看看课程</el-button>
          </el-empty>
        </div>

        <div v-else class="favorites-grid">
          <div v-for="favorite in favorites" :key="favorite.id" class="course-card">
            <div class="course-image" @click="viewCourse(favorite.course.id)">
              <el-image
                :src="getImageUrl(favorite.course.coverImage) || `https://picsum.photos/300/200?random=${favorite.course.id}`"
                alt="课程封面"
                fit="cover"
                class="course-img"
              />
            </div>

            <div class="course-info">
              <h3 class="course-title" @click="viewCourse(favorite.course.id)">
                {{ favorite.course.title }}
              </h3>
              <p class="course-subtitle">{{ favorite.course.subtitle }}</p>

              <div class="course-meta">
                <div class="meta-item">
                  <el-icon><User /></el-icon>
                  <span>{{ favorite.course.studentCount || 0 }} 学员</span>
                </div>
                <div class="meta-item">
                  <el-icon><Star /></el-icon>
                  <span>{{ favorite.course.rating || 0 }} 分</span>
                </div>
              </div>

              <div class="course-footer">
                <div class="course-price">
                  <span v-if="favorite.course.price > 0" class="price">¥{{ favorite.course.price }}</span>
                  <span v-else class="free-tag">免费</span>
                </div>
                <div class="course-actions">
                  <el-button size="small" type="primary" @click="viewCourse(favorite.course.id)">
                    查看详情
                  </el-button>
                  <el-button size="small" type="danger" @click="removeFavorite(favorite.courseId)">
                    <el-icon><Delete /></el-icon>
                    移除
                  </el-button>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Star, Delete } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import request from '@/utils/request'
import { getImageUrl } from '@/utils/imageUtils'

const router = useRouter()

const loading = ref(false)
const favorites = ref([])

// 加载收藏列表
const loadFavorites = async () => {
  try {
    loading.value = true
    const response = await request.get('/favorites')
    if (response.code === 0) {
      favorites.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取收藏列表失败')
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 移除收藏
const removeFavorite = async (courseId) => {
  try {
    const response = await request.delete(`/favorites/remove/${courseId}`)
    if (response.code === 0) {
      ElMessage.success('已取消收藏')
      loadFavorites()
    } else {
      ElMessage.error(response.msg || '取消收藏失败')
    }
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error('取消收藏失败')
  }
}

// 查看课程详情
const viewCourse = (courseId) => {
  router.push(`/course/${courseId}`)
}

// 去看课程
const goToCourses = () => {
  router.push('/courses')
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.my-favorites-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
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

.loading-container {
  padding: 40px 0;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 30px;
}

.course-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.course-image {
  height: 200px;
  cursor: pointer;
  overflow: hidden;
}

.course-img {
  width: 100%;
  height: 100%;
  transition: transform 0.3s;
}

.course-card:hover .course-img {
  transform: scale(1.05);
}

.course-info {
  padding: 20px;
}

.course-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
  cursor: pointer;
  line-height: 1.4;
}

.course-title:hover {
  color: #409EFF;
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
  font-size: 0.85rem;
  color: #666;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.course-price {
  font-size: 1.2rem;
  font-weight: 600;
  color: #f56c6c;
}

.free-tag {
  color: #67c23a;
  font-weight: 600;
}

.course-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 768px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }

  .page-header h1 {
    font-size: 2rem;
  }

  .course-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .course-actions {
    flex-direction: column;
  }
}
</style>
