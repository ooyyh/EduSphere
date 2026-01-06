<template>
  <el-dialog
    v-model="visible"
    title="账户充值"
    width="600px"
    :before-close="handleClose"
    class="recharge-dialog"
  >
    <div class="recharge-content">
      <!-- 当前余额显示 -->
      <div class="balance-display">
        <el-tag type="success" size="large">
          <el-icon><Wallet /></el-icon>
          当前余额: ¥{{ userBalance.balance || '0.00' }}
        </el-tag>
      </div>
      
      <!-- 充值表单 -->
      <el-form
        ref="rechargeFormRef"
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
            clearable
          >
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="备注信息" prop="remark">
          <el-input
            v-model="rechargeForm.remark"
            placeholder="请输入备注信息（可选）"
            type="textarea"
            :rows="2"
            maxlength="200"
            show-word-limit
          />
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
        <el-table 
          :data="rechargeHistory" 
          style="width: 100%" 
          v-loading="historyLoading"
          size="small"
        >
          <el-table-column prop="amount" label="充值金额" width="100">
            <template #default="scope">
              <span class="amount-text">+¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="balanceAfter" label="充值后余额" width="100">
            <template #default="scope">
              ¥{{ scope.row.balanceAfter }}
            </template>
          </el-table-column>
          <el-table-column prop="rechargeType" label="类型" width="80">
            <template #default="scope">
              <el-tag size="small" type="info">
                {{ scope.row.rechargeType === 'manual' ? '手动充值' : '系统充值' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="充值时间" width="160">
            <template #default="scope">
              {{ formatDateTime(scope.row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleRecharge"
        >
          <el-icon><Wallet /></el-icon>
          确认充值
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Wallet } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { formatDateTime } from '@/utils/dateFormat'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'recharge-success'])

// 响应式数据
const loading = ref(false)
const historyLoading = ref(false)
const userBalance = ref({})
const rechargeHistory = ref([])
const rechargeFormRef = ref()

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

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

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
    // 验证表单
    if (!rechargeFormRef.value) {
      ElMessage.error('表单未初始化')
      return
    }
    
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
        // 重新获取余额和充值记录
        await Promise.all([
          getUserBalance(),
          getRechargeHistory()
        ])
        // 重置表单
        rechargeForm.amount = ''
        rechargeForm.remark = ''
        // 触发成功事件
        emit('recharge-success')
        // 关闭弹窗
        handleClose()
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

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  // 重置表单
  rechargeForm.amount = ''
  rechargeForm.remark = ''
}

// 监听弹窗显示状态
watch(visible, (newVal) => {
  if (newVal) {
    Promise.all([
      getUserBalance(),
      getRechargeHistory()
    ])
  }
})
</script>

<style scoped>
.recharge-dialog {
  .recharge-content {
    padding: 20px 0;
  }
  
  .balance-display {
    text-align: center;
    margin-bottom: 30px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 8px;
  }
  
  .recharge-form {
    margin-bottom: 30px;
  }
  
  .quick-amounts {
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
  
  .recharge-history {
    margin-top: 20px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
  }
  
  .recharge-history h4 {
    margin: 0 0 15px 0;
    color: #333;
    font-size: 16px;
  }
  
  .amount-text {
    font-weight: bold;
    color: #67C23A;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-input__inner) {
  height: 40px;
  font-size: 16px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
}
</style>
