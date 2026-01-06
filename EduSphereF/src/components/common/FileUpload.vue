<template>
  <div class="file-upload">
    <el-upload
      class="upload-container"
      :action="uploadUrl"
      :headers="headers"
      :show-file-list="false"
      :on-success="handleSuccess"
      :on-error="handleError"
      :before-upload="beforeUpload"
      :accept="accept"
      :disabled="uploading"
    >
      <div class="upload-trigger">
        <div v-if="!currentUrl && !uploading" class="upload-placeholder">
          <el-icon :size="50"><Upload /></el-icon>
          <div class="upload-text">{{ placeholder }}</div>
          <div class="upload-hint">{{ hint }}</div>
        </div>

        <div v-if="uploading" class="upload-loading">
          <el-icon class="is-loading" :size="50"><Loading /></el-icon>
          <div class="upload-text">上传中...</div>
        </div>

        <div v-if="currentUrl && !uploading" class="upload-preview">
          <!-- 图片预览 -->
          <img v-if="fileType === 'image'" :src="getFullUrl(currentUrl)" alt="preview" />

          <!-- 视频预览 -->
          <div v-else-if="fileType === 'video'" class="video-preview">
            <el-icon :size="50"><VideoPlay /></el-icon>
            <div class="file-name">{{ getFileName(currentUrl) }}</div>
          </div>

          <!-- 文档预览 -->
          <div v-else-if="fileType === 'document'" class="document-preview">
            <el-icon :size="50"><Document /></el-icon>
            <div class="file-name">{{ getFileName(currentUrl) }}</div>
          </div>

          <div class="upload-actions">
            <el-button type="primary" size="small" :icon="Edit">更换</el-button>
            <el-button type="danger" size="small" :icon="Delete" @click.stop="handleRemove">删除</el-button>
          </div>
        </div>
      </div>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, Loading, Edit, Delete, VideoPlay, Document } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  fileType: {
    type: String,
    required: true,
    validator: (value) => ['image', 'video', 'document'].includes(value)
  },
  placeholder: {
    type: String,
    default: '点击上传文件'
  },
  maxSize: {
    type: Number,
    default: 100 // MB
  }
})

const emit = defineEmits(['update:modelValue', 'success', 'error'])

const currentUrl = ref(props.modelValue)
const uploading = ref(false)

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  currentUrl.value = newVal
})

// 上传地址映射
const uploadUrlMap = {
  image: '/api/upload/image',
  video: '/api/upload/video',
  document: '/api/upload/document'
}

const uploadUrl = computed(() => uploadUrlMap[props.fileType])

// 文件类型accept映射
const acceptMap = {
  image: 'image/jpeg,image/png,image/gif,image/webp',
  video: 'video/mp4,video/avi,video/mov,video/wmv',
  document: 'application/pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.txt'
}

const accept = computed(() => acceptMap[props.fileType])

// 提示文本
const hint = computed(() => {
  const hints = {
    image: `支持 JPG、PNG、GIF 格式，大小不超过 ${props.maxSize}MB`,
    video: `支持 MP4、AVI、MOV 格式，大小不超过 ${props.maxSize}MB`,
    document: `支持 PDF、DOC、PPT、XLS 格式，大小不超过 ${props.maxSize}MB`
  }
  return hints[props.fileType]
})

// 请求头
const headers = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: `Bearer ${token}`
  }
})

// 上传前校验
const beforeUpload = (file) => {
  const isLt = file.size / 1024 / 1024 < props.maxSize
  if (!isLt) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB`)
    return false
  }
  uploading.value = true
  return true
}

// 上传成功
const handleSuccess = (response) => {
  uploading.value = false
  if (response.code === 0) {
    currentUrl.value = response.data.url
    emit('update:modelValue', response.data.url)
    emit('success', response.data.url)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
    emit('error', response.msg)
  }
}

// 上传失败
const handleError = (error) => {
  uploading.value = false
  ElMessage.error('上传失败，请重试')
  emit('error', error)
}

// 删除文件
const handleRemove = () => {
  currentUrl.value = ''
  emit('update:modelValue', '')
  ElMessage.success('已删除')
}

// 获取完整URL
const getFullUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 如果是本地上传的文件路径，添加API前缀以通过代理访问
  if (url.startsWith('/uploads/')) {
    return '/api' + url
  }
  return url
}

// 获取文件名
const getFileName = (url) => {
  if (!url) return ''
  const parts = url.split('/')
  return parts[parts.length - 1]
}
</script>

<style scoped>
.file-upload {
  width: 100%;
}

.upload-container {
  width: 100%;
}

.upload-trigger {
  width: 100%;
  min-height: 180px;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.upload-trigger:hover {
  border-color: #409EFF;
}

.upload-placeholder,
.upload-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 180px;
  color: #8c939d;
}

.upload-text {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}

.upload-hint {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}

.upload-preview {
  position: relative;
  width: 100%;
  min-height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.upload-preview img {
  max-width: 100%;
  max-height: 300px;
  display: block;
}

.video-preview,
.document-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #606266;
}

.file-name {
  margin-top: 10px;
  font-size: 14px;
  text-align: center;
  word-break: break-all;
}

.upload-actions {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
}
</style>
