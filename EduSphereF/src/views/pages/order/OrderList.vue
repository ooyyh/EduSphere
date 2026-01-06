<template>
  <div class="order-list-page">
    <!-- 顶部导航栏 -->
    <Header />

    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1 class="page-title">我的订单</h1>
          <p class="page-subtitle">查看和管理您的订单</p>
        </div>

        <!-- 订单筛选 -->
        <div class="filter-section">
          <el-radio-group v-model="filterStatus" @change="handleFilterChange">
            <el-radio-button label="">全部订单</el-radio-button>
            <el-radio-button label="PENDING">待支付</el-radio-button>
            <el-radio-button label="PAID">已支付</el-radio-button>
            <el-radio-button label="CANCELLED">已取消</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <!-- 空订单 -->
        <div v-else-if="filteredOrders.length === 0" class="empty-orders">
          <el-empty description="暂无订单">
            <el-button type="primary" @click="goToCourses">去选购课程</el-button>
          </el-empty>
        </div>

        <!-- 订单列表 -->
        <div v-else class="orders-section">
          <div class="orders-list">
            <el-card
              v-for="order in paginatedOrders"
              :key="order.id"
              class="order-card"
              shadow="hover"
            >
              <!-- 订单头部 -->
              <div class="order-header">
                <div class="order-info">
                  <span class="order-no">订单号：{{ order.orderNo }}</span>
                  <span class="order-time">{{ formatDateTime(order.createdAt) }}</span>
                </div>
                <el-tag :type="getStatusType(order.status)" size="large">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </div>

              <!-- 订单课程列表 -->
              <div class="order-courses">
                <div
                  v-for="item in order.items"
                  :key="item.id"
                  class="course-item"
                  @click="goToCourseDetail(item.courseId)"
                >
                  <div class="course-cover">
                    <el-image
                      :src="getImageUrl(item.coverImage)"
                      alt="课程封面"
                      fit="cover"
                      class="cover-img"
                    />
                  </div>
                  <div class="course-info">
                    <h4 class="course-title">{{ item.courseTitle }}</h4>
                    <p class="course-instructor">讲师：{{ item.instructorName }}</p>
                  </div>
                  <div class="course-price">
                    <span class="price">¥{{ item.price }}</span>
                  </div>
                </div>
              </div>

              <!-- 订单底部 -->
              <div class="order-footer">
                <div class="order-total">
                  <span class="total-label">订单总额：</span>
                  <span class="total-price">¥{{ order.totalAmount }}</span>
                </div>
                <div class="order-actions">
                  <el-button
                    size="small"
                    @click="goToOrderDetail(order.orderNo)"
                  >
                    查看详情
                  </el-button>
                  <el-button
                    v-if="order.status === 'PENDING'"
                    type="primary"
                    size="small"
                    :loading="payingOrderNo === order.orderNo"
                    @click="handlePay(order)"
                  >
                    <el-icon><CreditCard /></el-icon>
                    立即支付
                  </el-button>
                  <el-button
                    v-if="order.status === 'PENDING'"
                    type="danger"
                    size="small"
                    text
                    :loading="cancellingOrderNo === order.orderNo"
                    @click="handleCancel(order)"
                  >
                    取消订单
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[5, 10, 20]"
              :total="filteredOrders.length"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
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
import { formatDateTime } from '@/utils/dateFormat'
import { CreditCard } from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/imageUtils'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const orders = ref([])  // 将这个数组重用为购买记录
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const payingOrderNo = ref(null)
const cancellingOrderNo = ref(null)

// 计算属性 - 筛选后的订单
const filteredOrders = computed(() => {
  if (!filterStatus.value) {
    return orders.value
  }
  return orders.value.filter(order => order.status === filterStatus.value)
})

// 计算属性 - 分页后的订单
const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrders.value.slice(start, end)
})

// 获取购买记录列表（替代原来的订单列表）
const loadOrders = async () => {
  try {
    loading.value = true
    // 改为调用购买记录API，而不是订单API
    const response = await request.get('/purchase/my-courses')

    if (response.code === 0) {
      // 将购买记录转换为订单格式以适配现有UI
      const purchaseRecords = response.data || []
      const formattedOrders = purchaseRecords.map(record => ({
        id: record.id,
        orderNo: `PURCHASE_${record.id}`, // 使用购买记录ID作为订单号
        userId: record.userId,
        totalAmount: record.purchasePrice,
        status: 'PAID', // 购买记录默认为已支付
        paymentMethod: 'Balance',
        paymentTime: record.createdAt,
        createdAt: record.createdAt,
        updatedAt: record.updatedAt,
        items: [{
          id: record.courseId,
          courseId: record.courseId,
          courseTitle: record.courseTitle || '未知课程',
          price: record.purchasePrice,
          coverImage: record.coverImage,
          instructorName: record.instructorName || '未知讲师'
        }]
      }))
      orders.value = formattedOrders
    } else {
      ElMessage.error(response.msg || '获取购买记录失败')
    }
  } catch (error) {
    console.error('获取购买记录失败:', error)
    ElMessage.error('获取购买记录失败')
  } finally {
    loading.value = false
  }
}

// 筛选状态改变
const handleFilterChange = () => {
  currentPage.value = 1
}

// 分页大小改变
const handleSizeChange = () => {
  currentPage.value = 1
}

// 当前页改变
const handleCurrentChange = () => {
  // 页码改变时自动滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 获取订单状态类型
const getStatusType = (status) => {
  const types = {
    PENDING: 'warning',
    PAID: 'success',
    CANCELLED: 'info'
  }
  return types[status] || 'success' // 默认为成功状态
}

// 获取订单状态文本
const getStatusText = (status) => {
  const texts = {
    PENDING: '待支付',
    PAID: '已支付',
    CANCELLED: '已取消'
  }
  return texts[status] || '已支付' // 默认为已支付
}

// 支付订单 - 对于购买记录，不需要支付
const handlePay = async (order) => {
  ElMessage.info('该课程已购买，无需重复支付')
}

// 取消订单 - 对于购买记录，不能取消
const handleCancel = async (order) => {
  ElMessage.info('已购买的课程无法取消')
}

// 跳转到订单详情 - 改为跳转到课程详情
const goToOrderDetail = (orderNo) => {
  // 从订单号中提取课程ID
  const order = orders.value.find(o => o.orderNo === orderNo)
  if (order && order.items && order.items.length > 0) {
    router.push(`/course/${order.items[0].courseId}`)
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
  loadOrders()
})
</script>

<style scoped>
.order-list-page {
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
  margin-bottom: 30px;
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

/* 筛选区域 */
.filter-section {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 加载状态 */
.loading-container {
  padding: 40px 0;
}

/* 空订单 */
.empty-orders {
  text-align: center;
  padding: 80px 20px;
}

/* 订单列表 */
.orders-section {
  min-height: 400px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
}

.order-card {
  border-radius: 12px;
  transition: all 0.3s;
}

.order-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
  margin-bottom: 20px;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-no {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
}

.order-time {
  font-size: 0.9rem;
  color: #999;
}

/* 订单课程列表 */
.order-courses {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s;
  cursor: pointer;
}

.course-item:hover {
  background: #e9ecef;
}

.course-cover {
  flex-shrink: 0;
  width: 120px;
  height: 75px;
  border-radius: 6px;
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 100%;
}

.course-info {
  flex: 1;
}

.course-title {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.course-instructor {
  color: #666;
  font-size: 0.85rem;
  margin: 0;
}

.course-price {
  flex-shrink: 0;
}

.price {
  font-size: 1.1rem;
  font-weight: bold;
  color: #e74c3c;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.order-total {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-label {
  font-size: 1rem;
  color: #666;
}

.total-price {
  font-size: 1.5rem;
  font-weight: bold;
  color: #e74c3c;
}

.order-actions {
  display: flex;
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
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .course-item {
    flex-wrap: wrap;
  }

  .course-cover {
    width: 100%;
    height: 150px;
  }

  .order-footer {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .order-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .order-actions .el-button {
    flex: 1;
  }
}
</style>
