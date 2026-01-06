<template>
  <el-button
    :type="type"
    :size="size"
    :loading="loading"
    :disabled="disabled"
    :icon="icon"
    @click="handleClick"
  >
    <el-icon v-if="icon && !loading">
      <component :is="icon" />
    </el-icon>
    {{ text }}
  </el-button>
</template>

<script setup>
import { ElButton, ElIcon } from 'element-plus'

const props = defineProps({
  // 按钮文本
  text: {
    type: String,
    required: true
  },
  
  // 按钮类型
  type: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'success', 'warning', 'danger', 'info', 'text'].includes(value)
  },
  
  // 按钮大小
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  },
  
  // 是否加载中
  loading: {
    type: Boolean,
    default: false
  },
  
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  
  // 图标
  icon: {
    type: [String, Object],
    default: null
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  if (!props.loading && !props.disabled) {
    emit('click')
  }
}
</script>
