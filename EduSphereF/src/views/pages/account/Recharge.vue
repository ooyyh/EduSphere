<template>
  <div class="recharge-container">
    <div class="recharge-card">
      <div class="recharge-header">
        <h2>账户充值</h2>
        <p class="balance-info">
          当前余额：<span class="balance-amount">¥{{ userBalance.balance || '0.00' }}</span>
        </p>
      </div>
      
      <el-form
        ref="rechargeForm"
        :model="rechargeForm"
        :rules="rechargeRules"
        label-width="100px"
        class="recharge-form"
      >
        <el-form-item label="充值金额" prop="amount">
          <el-input
            v-model="rechargeForm.amount"
            placeholder="请输入充值金额"
            type="number"
            :min="0.01"
            :step="0.01"
            @input="formatAmount"
          >
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="备注信息" prop="remark">
          <el-input
            v-model="rechargeForm.remark"
            placeholder="请输入备注信息（可选）"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRecharge"
            class="recharge-btn"
          >
            <el-icon><Wallet /></el-icon>
            确认充值
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 快速充值金额 -->
      <div class="quick-amounts">
        <h4>快速选择</h4>
        <div class="amount-buttons">
          <el-button
            v-for="amount in quickAmounts"
            :key="amount"
            :type="rechargeForm.amount == amount ? 'primary' : 'default'"
            @click="selectAmount(amount)"
            class="amount-btn"
          >
            ¥{{ amount }}
          </el-button>
        </div>
      </div>
      
      <!-- 充值记录 -->
      <div class="recharge-history">
        <h4>充值记录</h4>
        <el-table :data="rechargeHistory" style="width: 100%" v-loading="historyLoading">
          <el-table-column prop="amount" label="充值金额" width="120">
            <template #default="scope">
              <span class="amount-text">+¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="balanceAfter" label="充值后余额" width="120">
            <template #default="scope">
              ¥{{ scope.row.balanceAfter }}
            </template>
          </el-table-column>
          <el-table-column prop="rechargeType" label="类型" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.rechargeType === 'manual' ? 'success' : 'info'">
                {{ scope.row.rechargeType === 'manual' ? '手动充值' : '系统充值' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="充值时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Wallet } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'
import { formatDateTime } from '@/utils/dateFormat'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const historyLoading = ref(false)
const userBalance = ref({})
const rechargeHistory = ref([])

// 表单数据
const rechargeForm = reactive({
  amount: '',
  remark: ''
})

// 快速充值金额
const quickAmounts = [10, 50, 100, 200, 500, 1000]

// 表单验证规则
const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (!value || value <= 0) {
          callback(new Error('充值金额必须大于0'))
        } else if (value > 10000) {
          callback(new Error('单次充值金额不能超过10000元'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 格式化金额
const formatAmount = () => {
  if (rechargeForm.amount) {
    rechargeForm.amount = parseFloat(rechargeForm.amount).toFixed(2)
  }
}

// 选择快速金额
const selectAmount = (amount) => {
  rechargeForm.amount = amount.toString()
}

// 获取用户余额
const getUserBalance = async () => {
  try {
    const response = await request.get('/balance/info')
    if (response.code === 0) {
      userBalance.value = response.data
    }
  } catch (error) {
    console.error('获取余额失败:', error)
  }
}

// 获取充值记录
const getRechargeHistory = async () => {
  try {
    historyLoading.value = true
    const response = await request.get('/balance/recharge-history')
    if (response.code === 0) {
      rechargeHistory.value = response.data
    }
  } catch (error) {
    console.error('获取充值记录失败:', error)
  } finally {
    historyLoading.value = false
  }
}

// 处理充值
const handleRecharge = async () => {
  try {
    await rechargeFormRef.value.validate()
    
    const confirmed = await ElMessageBox.confirm(
      `确认充值 ¥${rechargeForm.amount} 吗？`,
      '确认充值',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (confirmed) {
      loading.value = true
      
      const response = await request.post('/balance/recharge', {
        amount: parseFloat(rechargeForm.amount),
        remark: rechargeForm.remark || '用户充值'
      })
      
      if (response.code === 0) {
        ElMessage.success('充值成功！')
        // 重新获取余额和记录
        await getUserBalance()
        await getRechargeHistory()
        // 重置表单
        rechargeForm.amount = ''
        rechargeForm.remark = ''
      } else {
        ElMessage.error(response.msg || '充值失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('充值失败:', error)
      ElMessage.error('充值失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

// 表单引用
const rechargeFormRef = ref()

// 组件挂载时获取数据
onMounted(() => {
  getUserBalance()
  getRechargeHistory()
})
</script>

<style scoped>
.recharge-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.recharge-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.recharge-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.recharge-header h2 {
  color: #333;
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.balance-info {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.balance-amount {
  color: #409eff;
  font-size: 20px;
  font-weight: 600;
}

.recharge-form {
  margin-bottom: 40px;
}

.recharge-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
}

.quick-amounts {
  margin-bottom: 40px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.quick-amounts h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.amount-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.amount-btn {
  min-width: 80px;
  height: 40px;
  border-radius: 20px;
  font-weight: 500;
}

.recharge-history h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.amount-text {
  color: #67c23a;
  font-weight: 600;
}

:deep(.el-input__inner) {
  height: 50px;
  font-size: 16px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
}
</style>
