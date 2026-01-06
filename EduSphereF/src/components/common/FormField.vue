<template>
  <el-form-item :label="label" :prop="prop" :rules="rules">
    <el-input
      v-if="type === 'input'"
      v-model="modelValue"
      :placeholder="placeholder"
      :type="inputType"
      :maxlength="maxlength"
      :show-word-limit="showWordLimit"
      :disabled="disabled"
      @input="$emit('update:modelValue', $event)"
    />
    
    <el-input
      v-else-if="type === 'textarea'"
      v-model="modelValue"
      :placeholder="placeholder"
      :rows="rows"
      :maxlength="maxlength"
      :show-word-limit="showWordLimit"
      :disabled="disabled"
      type="textarea"
      @input="$emit('update:modelValue', $event)"
    />
    
    <el-select
      v-else-if="type === 'select'"
      v-model="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      @change="$emit('update:modelValue', $event)"
    >
      <el-option
        v-for="option in options"
        :key="option.value"
        :label="option.label"
        :value="option.value"
      />
    </el-select>
    
    <el-input-number
      v-else-if="type === 'number'"
      v-model="modelValue"
      :placeholder="placeholder"
      :min="min"
      :max="max"
      :step="step"
      :disabled="disabled"
      @change="$emit('update:modelValue', $event)"
    />
    
    <el-switch
      v-else-if="type === 'switch'"
      v-model="modelValue"
      :disabled="disabled"
      @change="$emit('update:modelValue', $event)"
    />
  </el-form-item>
</template>

<script setup>
defineProps({
  // 字段类型
  type: {
    type: String,
    default: 'input',
    validator: (value) => ['input', 'textarea', 'select', 'number', 'switch'].includes(value)
  },
  
  // 标签
  label: {
    type: String,
    required: true
  },
  
  // 字段名
  prop: {
    type: String,
    required: true
  },
  
  // 验证规则
  rules: {
    type: Array,
    default: () => []
  },
  
  // 占位符
  placeholder: {
    type: String,
    default: ''
  },
  
  // 输入框类型
  inputType: {
    type: String,
    default: 'text'
  },
  
  // 最大长度
  maxlength: {
    type: Number,
    default: null
  },
  
  // 是否显示字数限制
  showWordLimit: {
    type: Boolean,
    default: false
  },
  
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  
  // 文本域行数
  rows: {
    type: Number,
    default: 3
  },
  
  // 选择器选项
  options: {
    type: Array,
    default: () => []
  },
  
  // 数字输入框最小值
  min: {
    type: Number,
    default: 0
  },
  
  // 数字输入框最大值
  max: {
    type: Number,
    default: 999999
  },
  
  // 数字输入框步长
  step: {
    type: Number,
    default: 1
  }
})

defineEmits(['update:modelValue'])
</script>
