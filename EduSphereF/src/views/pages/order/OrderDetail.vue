<template>
  <div class="order-detail-page">
    <!-- 顶部导航栏 -->
    <Header />

    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="10" animated />
        </div>

        <!-- 订单详情 -->
        <div v-else-if="order" class="order-detail">
          <!-- 页面标题 -->
          <div class="page-header">
            <el-button @click="goBack" class="back-btn">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <h1 class="page-title">订单详情</h1>
          </div>

          <!-- 订单状态 -->
          <el-card class="status-card" shadow="hover">
            <div class="status-content">
              <div class="status-icon" :class="getStatusClass(order.status)">
                <el-icon size="60">
                  <SuccessFilled v-if="order.status === 'PAID'" />
                  <Clock v-else-if="order.status === 'PENDING'" />
                  <CircleClose v-else />
                </el-icon>
              </div>
              <div class="status-info">
                <h2 class="status-title">{{ getStatusTitle(order.status) }}</h2>
                <p class="status-desc">{{ getStatusDesc(order.status) }}</p>
              </div>
              <div v-if="order.status === 'PENDING'" class="status-action">
                <el-button
                  type="primary"
                  size="large"
                  :loading="paying"
                  @click="handlePay"
                >
                  <el-icon><CreditCard /></el-icon>
                  立即支付
                </el-button>
              </div>
            </div>
          </el-card>

          <!-- 订单信息 -->
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-title">订单信息</span>
              </div>
            </template>

            <el-descriptions :column="2" border>
              <el-descriptions-item label="订单号">
                {{ order.orderNo }}
              </el-descriptions-item>
              <el-descriptions-item label="订单状态">
                <el-tag :type="getStatusType(order.status)" size="large">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ formatDateTime(order.createdAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="支付时间">
                {{ order.payTime ? formatDateTime(order.payTime) : '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="订单金额" :span="2">
                <span class="order-amount">¥{{ order.totalAmount }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 课程列表 -->
          <el-card class="courses-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-title">课程清单</span>
                <span class="course-count">共 {{ order.items?.length || 0 }} 门课程</span>
              </div>
            </template>

            <div class="courses-list">
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
                  <h3 class="course-title">{{ item.courseTitle }}</h3>
                  <p class="course-instructor">讲师：{{ item.instructorName }}</p>
                  <div class="course-meta">
                    <el-tag size="small" type="info">{{ item.categoryName || '未分类' }}</el-tag>
                  </div>
                </div>
                <div class="course-price">
                  <span class="price">¥{{ item.price }}</span>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 金额明细 -->
          <el-card class="amount-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-title">金额明细</span>
              </div>
            </template>

            <div class="amount-details">
              <div class="amount-item">
                <span class="amount-label">课程小计</span>
                <span class="amount-value">¥{{ order.totalAmount }}</span>
              </div>
              <div class="amount-item">
                <span class="amount-label">优惠金额</span>
                <span class="amount-value discount">-¥0.00</span>
              </div>
              <el-divider />
              <div class="amount-item total">
                <span class="amount-label">实付金额</span>
                <span class="amount-value">¥{{ order.totalAmount }}</span>
              </div>
            </div>
          </el-card>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button
              v-if="order.status === 'PENDING'"
              type="primary"
              size="large"
              :loading="paying"
              @click="handlePay"
            >
              <el-icon><CreditCard /></el-icon>
              立即支付
            </el-button>
            <el-button
              v-if="order.status === 'PENDING'"
              type="danger"
              size="large"
              plain
              :loading="cancelling"
              @click="handleCancel"
            >
              取消订单
            </el-button>
            <el-button
              v-if="order.status === 'PAID'"
              type="success"
              size="large"
              @click="goToMyCourses"
            >
              <el-icon><VideoPlay /></el-icon>
              查看我的课程
            </el-button>
          </div>
        </div>

        <!-- 订单不存在 -->
        <div v-else class="not-found">
          <el-empty description="订单不存在">
            <el-button type="primary" @click="goToOrders">查看我的订单</el-button>
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
import request from '@/utils/request'
import { formatDateTime } from '@/utils/dateFormat'
import { getImageUrl } from '@/utils/imageUtils'
import {
  ArrowLeft,
  SuccessFilled,
  Clock,
  CircleClose,
  CreditCard,
  VideoPlay
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const order = ref(null)
const paying = ref(false)
const cancelling = ref(false)

// 获取订单详情
const loadOrderDetail = async () => {
  try {
    loading.value = true
    const orderNo = route.params.orderNo
    const response = await request.get(`/order/${orderNo}`)

    if (response.code === 0) {
      order.value = response.data
    } else {
      ElMessage.error(response.msg || '获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

// 获取订单状态类型
const getStatusType = (status) => {
  const types = {
    PENDING: 'warning',
    PAID: 'success',
    CANCELLED: 'info'
  }
  return types[status] || 'info'
}

// 获取订单状态文本
const getStatusText = (status) => {
  const texts = {
    PENDING: '待支付',
    PAID: '已支付',
    CANCELLED: '已取消'
  }
  return texts[status] || '未知'
}

// 获取订单状态样式类
const getStatusClass = (status) => {
  const classes = {
    PENDING: 'status-pending',
    PAID: 'status-paid',
    CANCELLED: 'status-cancelled'
  }
  return classes[status] || ''
}

// 获取订单状态标题
const getStatusTitle = (status) => {
  const titles = {
    PENDING: '等待支付',
    PAID: '支付成功',
    CANCELLED: '订单已取消'
  }
  return titles[status] || '未知状态'
}

// 获取订单状态描述
const getStatusDesc = (status) => {
  const descs = {
    PENDING: '请尽快完成支付，超时订单将自动取消',
    PAID: '您已成功支付订单，可以开始学习课程了',
    CANCELLED: '该订单已被取消'
  }
  return descs[status] || ''
}

// 支付订单
const handlePay = async () => {
  try {
    const confirmed = await ElMessageBox.confirm(
      `确认支付订单吗？\n订单号：${order.value.orderNo}\n金额：¥${order.value.totalAmount}`,
      '确认支付',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    if (confirmed) {
      paying.value = true

      const response = await request.post(`/order/pay/${order.value.orderNo}`)

      if (response.code === 0) {
        ElMessage.success('支付成功！')
        await loadOrderDetail()
      } else {
        ElMessage.error(response.msg || '支付失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败，请重试')
    }
  } finally {
    paying.value = false
  }
}

// 取消订单
const handleCancel = async () => {
  try {
    const confirmed = await ElMessageBox.confirm(
      `确定要取消订单吗？\n订单号：${order.value.orderNo}`,
      '取消订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    if (confirmed) {
      cancelling.value = true

      const response = await request.post(`/order/cancel/${order.value.orderNo}`)

      if (response.code === 0) {
        ElMessage.success('订单已取消')
        await loadOrderDetail()
      } else {
        ElMessage.error(response.msg || '取消订单失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败，请重试')
    }
  } finally {
    cancelling.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 跳转到订单列表
const goToOrders = () => {
  router.push('/orders')
}

// 跳转到课程详情
const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

// 跳转到我的课程
const goToMyCourses = () => {
  router.push('/my-courses')
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
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

/* 加载状态 */
.loading-container {
  padding: 40px 0;
}

/* 页面标题 */
.page-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.back-btn {
  flex-shrink: 0;
}

.page-title {
  font-size: 2rem;
  color: #333;
  margin: 0;
}

/* 订单详情 */
.order-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 订单状态卡片 */
.status-card {
  border-radius: 12px;
}

.status-content {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 20px;
}

.status-icon {
  flex-shrink: 0;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-icon.status-pending {
  background: #fef0e6;
  color: #e6a23c;
}

.status-icon.status-paid {
  background: #e8f5e9;
  color: #67c23a;
}

.status-icon.status-cancelled {
  background: #f0f0f0;
  color: #909399;
}

.status-info {
  flex: 1;
}

.status-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.status-desc {
  font-size: 1rem;
  color: #666;
  margin: 0;
}

.status-action {
  flex-shrink: 0;
}

/* 卡片通用样式 */
.info-card,
.courses-card,
.amount-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
}

.course-count {
  color: #999;
  font-size: 0.9rem;
}

/* 订单信息 */
.order-amount {
  font-size: 1.5rem;
  font-weight: bold;
  color: #e74c3c;
}

/* 课程列表 */
.courses-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
}

.course-item:hover {
  background: #e9ecef;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.course-cover {
  flex-shrink: 0;
  width: 160px;
  height: 100px;
  border-radius: 8px;
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
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.course-instructor {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 12px;
}

.course-meta {
  display: flex;
  gap: 8px;
}

.course-price {
  flex-shrink: 0;
}

.price {
  font-size: 1.3rem;
  font-weight: bold;
  color: #e74c3c;
}

/* 金额明细 */
.amount-details {
  padding: 10px 0;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 1rem;
}

.amount-label {
  color: #666;
}

.amount-value {
  color: #333;
  font-weight: 500;
}

.amount-value.discount {
  color: #67c23a;
}

.amount-item.total {
  font-size: 1.2rem;
  margin-top: 16px;
  margin-bottom: 0;
}

.amount-item.total .amount-label {
  color: #333;
  font-weight: 600;
}

.amount-item.total .amount-value {
  color: #e74c3c;
  font-size: 1.8rem;
  font-weight: bold;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px 0;
}

/* 订单不存在 */
.not-found {
  text-align: center;
  padding: 80px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .status-content {
    flex-direction: column;
    text-align: center;
  }

  .status-icon {
    width: 80px;
    height: 80px;
  }

  .status-title {
    font-size: 1.5rem;
  }

  .course-item {
    flex-wrap: wrap;
  }

  .course-cover {
    width: 100%;
    height: 150px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
