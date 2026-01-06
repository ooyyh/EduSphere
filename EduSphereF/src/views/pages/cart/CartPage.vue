<template>
  <div class="cart-page">
    <!-- 顶部导航栏 -->
    <Header />

    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1 class="page-title">购物车</h1>
          <p class="page-subtitle">共 {{ cartItems.length }} 门课程</p>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>

        <!-- 空购物车 -->
        <div v-else-if="cartItems.length === 0" class="empty-cart">
          <el-empty description="购物车是空的">
            <el-button type="primary" @click="goToCourses">去选购课程</el-button>
          </el-empty>
        </div>

        <!-- 购物车内容 -->
        <div v-else class="cart-content">
          <!-- 购物车列表 -->
          <div class="cart-list">
            <el-card class="cart-card" shadow="hover">
              <!-- 全选区域 -->
              <div class="cart-header">
                <el-checkbox
                  v-model="selectAll"
                  @change="handleSelectAll"
                  :indeterminate="isIndeterminate"
                >
                  全选
                </el-checkbox>
                <div class="header-actions">
                  <el-button
                    type="danger"
                    text
                    :disabled="selectedItems.length === 0"
                    @click="handleBatchDelete"
                  >
                    <el-icon><Delete /></el-icon>
                    批量删除
                  </el-button>
                </div>
              </div>

              <!-- 课程列表 -->
              <div class="course-list">
                <div
                  v-for="item in cartItems"
                  :key="item.id"
                  class="course-item"
                >
                  <el-checkbox
                    v-model="item.selected"
                    @change="handleItemSelect"
                    class="item-checkbox"
                  />

                  <div class="course-cover" @click="goToCourseDetail(item.courseId)">
                    <el-image
                      :src="getImageUrl(item.coverImage)"
                      alt="课程封面"
                      fit="cover"
                      class="cover-img"
                    />
                  </div>

                  <div class="course-info" @click="goToCourseDetail(item.courseId)">
                    <h3 class="course-title">{{ item.courseTitle }}</h3>
                    <p class="course-instructor">讲师：{{ item.instructorName }}</p>
                    <div class="course-meta">
                      <el-rate
                        :model-value="item.rating || 0"
                        disabled
                        show-score
                        text-color="#ff9900"
                        score-template="{value}"
                      />
                      <span class="student-count">{{ item.studentCount || 0 }} 学员</span>
                    </div>
                  </div>

                  <div class="course-price">
                    <span class="current-price">¥{{ item.price }}</span>
                    <span v-if="item.originalPrice" class="original-price">¥{{ item.originalPrice }}</span>
                  </div>

                  <div class="course-actions">
                    <el-button
                      type="danger"
                      text
                      :loading="removingItemId === item.id"
                      @click="handleRemove(item)"
                    >
                      <el-icon><Delete /></el-icon>
                      移除
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 结算区域 -->
          <div class="checkout-section">
            <el-card class="checkout-card" shadow="hover">
              <h3 class="checkout-title">订单摘要</h3>

              <div class="price-details">
                <div class="price-item">
                  <span class="price-label">已选课程</span>
                  <span class="price-value">{{ selectedItems.length }} 门</span>
                </div>
                <div class="price-item">
                  <span class="price-label">原价</span>
                  <span class="price-value">¥{{ totalOriginalPrice.toFixed(2) }}</span>
                </div>
                <div class="price-item discount">
                  <span class="price-label">优惠</span>
                  <span class="price-value">-¥{{ discount.toFixed(2) }}</span>
                </div>
                <el-divider />
                <div class="price-item total">
                  <span class="price-label">合计</span>
                  <span class="price-value">¥{{ totalPrice.toFixed(2) }}</span>
                </div>
              </div>

              <el-button
                type="primary"
                size="large"
                class="checkout-btn"
                :disabled="selectedItems.length === 0"
                :loading="checkingOut"
                @click="handleCheckout"
              >
                <el-icon><ShoppingCart /></el-icon>
                去结算（{{ selectedItems.length }}）
              </el-button>

              <div class="checkout-tips">
                <el-icon><InfoFilled /></el-icon>
                <span>选择课程后点击结算按钮进行支付</span>
              </div>
            </el-card>
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
import { Delete, ShoppingCart, InfoFilled } from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/imageUtils'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const cartItems = ref([])
const selectAll = ref(false)
const removingItemId = ref(null)
const checkingOut = ref(false)

// 计算属性 - 是否半选状态
const isIndeterminate = computed(() => {
  const selectedCount = selectedItems.value.length
  return selectedCount > 0 && selectedCount < cartItems.value.length
})

// 计算属性 - 已选中的课程
const selectedItems = computed(() => {
  return cartItems.value.filter(item => item.selected)
})

// 计算属性 - 总原价
const totalOriginalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + (parseFloat(item.originalPrice) || parseFloat(item.price) || 0)
  }, 0)
})

// 计算属性 - 优惠金额
const discount = computed(() => {
  const original = totalOriginalPrice.value
  const current = totalPrice.value
  return original - current
})

// 计算属性 - 总价
const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + (parseFloat(item.price) || 0)
  }, 0)
})

// 获取购物车数据
const loadCart = async () => {
  try {
    loading.value = true
    const response = await request.get('/cart')

    if (response.code === 0) {
      cartItems.value = (response.data || []).map(item => ({
        ...item,
        selected: false
      }))
    } else {
      ElMessage.error(response.msg || '获取购物车失败')
    }
  } catch (error) {
    console.error('获取购物车失败:', error)
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

// 全选/反选
const handleSelectAll = (val) => {
  cartItems.value.forEach(item => {
    item.selected = val
  })
}

// 单个课程选择
const handleItemSelect = () => {
  const allSelected = cartItems.value.every(item => item.selected)
  const noneSelected = cartItems.value.every(item => !item.selected)

  if (allSelected) {
    selectAll.value = true
  } else if (noneSelected) {
    selectAll.value = false
  } else {
    selectAll.value = false
  }
}

// 移除单个课程
const handleRemove = async (item) => {
  try {
    const confirmed = await ElMessageBox.confirm(
      `确定要从购物车移除《${item.courseTitle}》吗？`,
      '确认移除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    if (confirmed) {
      removingItemId.value = item.id
      const response = await request.delete(`/cart/remove/${item.courseId}`)

      if (response.code === 0) {
        ElMessage.success('移除成功')
        await loadCart()
      } else {
        ElMessage.error(response.msg || '移除失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('移除课程失败:', error)
      ElMessage.error('移除失败，请重试')
    }
  } finally {
    removingItemId.value = null
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    const confirmed = await ElMessageBox.confirm(
      `确定要从购物车移除选中的 ${selectedItems.value.length} 门课程吗？`,
      '批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    if (confirmed) {
      loading.value = true

      // 批量删除
      const deletePromises = selectedItems.value.map(item =>
        request.delete(`/cart/remove/${item.courseId}`)
      )

      await Promise.all(deletePromises)
      ElMessage.success('批量删除成功')
      await loadCart()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

// 去结算
const handleCheckout = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要购买的课程')
    return
  }

  try {
    checkingOut.value = true

    // 创建订单
    const courseIds = selectedItems.value.map(item => item.courseId)
    const response = await request.post('/order/create', {
      courseIds: courseIds
    })

    if (response.code === 0) {
      const orderNo = response.data.orderNo
      ElMessage.success('订单创建成功')

      // 跳转到订单详情页进行支付
      router.push(`/order/${orderNo}`)
    } else {
      ElMessage.error(response.msg || '创建订单失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败，请重试')
  } finally {
    checkingOut.value = false
  }
}

// 跳转到课程详情
const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

// 跳转到课程列表
const goToCourses = () => {
  router.push('/courses')
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-content {
  padding: 40px 0;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 2rem;
  color: #333;
  margin-bottom: 8px;
}

.page-subtitle {
  color: #666;
  font-size: 1rem;
}

/* 加载状态 */
.loading-container {
  padding: 40px 0;
}

/* 空购物车 */
.empty-cart {
  text-align: center;
  padding: 80px 20px;
}

/* 购物车内容 */
.cart-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 20px;
  align-items: start;
}

/* 购物车列表 */
.cart-list {
  min-height: 400px;
}

.cart-card {
  border-radius: 12px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 课程列表 */
.course-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.course-item:hover {
  background: #e9ecef;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.item-checkbox {
  flex-shrink: 0;
}

.course-cover {
  flex-shrink: 0;
  width: 160px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.cover-img {
  width: 100%;
  height: 100%;
}

.course-info {
  flex: 1;
  cursor: pointer;
}

.course-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-instructor {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 12px;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.student-count {
  color: #999;
  font-size: 0.85rem;
}

.course-price {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.current-price {
  font-size: 1.5rem;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 0.9rem;
  color: #999;
  text-decoration: line-through;
}

.course-actions {
  flex-shrink: 0;
}

/* 结算区域 */
.checkout-section {
  position: sticky;
  top: 100px;
}

.checkout-card {
  border-radius: 12px;
  padding: 10px;
}

.checkout-title {
  font-size: 1.3rem;
  color: #333;
  margin-bottom: 24px;
}

.price-details {
  margin-bottom: 24px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 0.95rem;
}

.price-label {
  color: #666;
}

.price-value {
  color: #333;
  font-weight: 500;
}

.price-item.discount .price-value {
  color: #67c23a;
}

.price-item.total {
  font-size: 1.2rem;
  margin-top: 16px;
  margin-bottom: 0;
}

.price-item.total .price-label {
  color: #333;
  font-weight: 600;
}

.price-item.total .price-value {
  color: #e74c3c;
  font-size: 1.8rem;
  font-weight: bold;
}

.checkout-btn {
  width: 100%;
  height: 50px;
  font-size: 1.1rem;
  margin-bottom: 16px;
}

.checkout-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f0f9ff;
  border-radius: 8px;
  color: #409EFF;
  font-size: 0.85rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cart-content {
    grid-template-columns: 1fr;
  }

  .checkout-section {
    position: static;
  }

  .course-item {
    flex-wrap: wrap;
  }

  .course-cover {
    width: 100%;
    height: 200px;
  }

  .course-price {
    align-items: flex-start;
  }

  .cart-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
