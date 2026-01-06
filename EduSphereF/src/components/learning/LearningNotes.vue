<template>
  <div class="learning-notes">
    <div class="notes-header">
      <h3>学习笔记</h3>
      <el-button type="primary" size="small" @click="showNoteDialog = true">
        <el-icon><Edit /></el-icon>
        添加笔记
      </el-button>
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="notes.length === 0" class="empty-notes">
      <el-empty description="暂无笔记">
        <el-button type="primary" @click="showNoteDialog = true">添加第一条笔记</el-button>
      </el-empty>
    </div>

    <div v-else class="notes-list">
      <div v-for="note in notes" :key="note.id" class="note-item">
        <div class="note-header">
          <div class="note-info">
            <span class="note-time" v-if="note.videoTime">{{ formatTime(note.videoTime) }}</span>
            <span class="note-lesson">{{ note.lessonTitle }}</span>
            <el-tag v-if="note.isPublic" type="success" size="small">公开</el-tag>
            <el-tag v-else type="info" size="small">私密</el-tag>
          </div>
          <div class="note-actions">
            <el-button size="small" text @click="editNote(note)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button size="small" text type="danger" @click="deleteNote(note.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
        <div class="note-content">{{ note.content }}</div>
        <div class="note-footer">
          <span class="note-date">{{ formatDate(note.createdAt) }}</span>
        </div>
      </div>
    </div>

    <!-- 添加/编辑笔记对话框 -->
    <el-dialog
      v-model="showNoteDialog"
      :title="editingNote ? '编辑笔记' : '添加笔记'"
      width="600px"
    >
      <el-form :model="noteForm" label-width="80px">
        <el-form-item label="笔记内容">
          <el-input
            v-model="noteForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入笔记内容..."
          />
        </el-form-item>
        <el-form-item label="视频时间">
          <el-input
            v-model.number="noteForm.videoTime"
            type="number"
            placeholder="视频时间点（秒）"
          >
            <template #append>秒</template>
          </el-input>
        </el-form-item>
        <el-form-item label="是否公开">
          <el-switch
            v-model="noteForm.isPublic"
            :active-value="1"
            :inactive-value="0"
            active-text="公开"
            inactive-text="私密"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNoteDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNote" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const props = defineProps({
  courseId: {
    type: Number,
    required: true
  },
  lessonId: {
    type: Number,
    default: null
  }
})

const loading = ref(false)
const notes = ref([])
const showNoteDialog = ref(false)
const editingNote = ref(null)
const saving = ref(false)

const noteForm = ref({
  content: '',
  videoTime: null,
  isPublic: 0,
  courseId: props.courseId,
  lessonId: props.lessonId
})

// 加载笔记
const loadNotes = async () => {
  try {
    loading.value = true
    let url = `/notes/course/${props.courseId}`
    if (props.lessonId) {
      url = `/notes/lesson/${props.lessonId}`
    }

    const response = await request.get(url)
    if (response.code === 0) {
      notes.value = response.data || []
    } else {
      ElMessage.error(response.msg || '加载笔记失败')
    }
  } catch (error) {
    console.error('加载笔记失败:', error)
    ElMessage.error('加载笔记失败')
  } finally {
    loading.value = false
  }
}

// 保存笔记
const saveNote = async () => {
  if (!noteForm.value.content.trim()) {
    ElMessage.warning('请输入笔记内容')
    return
  }

  try {
    saving.value = true
    noteForm.value.courseId = props.courseId
    noteForm.value.lessonId = props.lessonId

    let response
    if (editingNote.value) {
      response = await request.put('/notes/update', noteForm.value)
    } else {
      response = await request.post('/notes/create', noteForm.value)
    }

    if (response.code === 0) {
      ElMessage.success(editingNote.value ? '笔记更新成功' : '笔记添加成功')
      showNoteDialog.value = false
      resetForm()
      loadNotes()
    } else {
      ElMessage.error(response.msg || '保存笔记失败')
    }
  } catch (error) {
    console.error('保存笔记失败:', error)
    ElMessage.error('保存笔记失败')
  } finally {
    saving.value = false
  }
}

// 编辑笔记
const editNote = (note) => {
  editingNote.value = note
  noteForm.value = {
    id: note.id,
    content: note.content,
    videoTime: note.videoTime,
    isPublic: note.isPublic,
    courseId: props.courseId,
    lessonId: props.lessonId
  }
  showNoteDialog.value = true
}

// 删除笔记
const deleteNote = async (noteId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条笔记吗？', '确认删除', {
      type: 'warning'
    })

    const response = await request.delete(`/notes/delete/${noteId}`)
    if (response.code === 0) {
      ElMessage.success('笔记删除成功')
      loadNotes()
    } else {
      ElMessage.error(response.msg || '删除笔记失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除笔记失败:', error)
      ElMessage.error('删除笔记失败')
    }
  }
}

// 重置表单
const resetForm = () => {
  editingNote.value = null
  noteForm.value = {
    content: '',
    videoTime: null,
    isPublic: 0,
    courseId: props.courseId,
    lessonId: props.lessonId
  }
}

// 格式化时间
const formatTime = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

// 监听lessonId变化
watch(() => props.lessonId, () => {
  loadNotes()
})

onMounted(() => {
  loadNotes()
})
</script>

<style scoped>
.learning-notes {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.notes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.notes-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.2rem;
}

.loading {
  padding: 20px 0;
}

.empty-notes {
  padding: 40px 0;
  text-align: center;
}

.notes-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.note-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  transition: box-shadow 0.3s;
}

.note-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.note-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.note-time {
  color: #409EFF;
  font-weight: 600;
  font-size: 0.9rem;
}

.note-lesson {
  color: #666;
  font-size: 0.9rem;
}

.note-actions {
  display: flex;
  gap: 8px;
}

.note-content {
  color: #333;
  line-height: 1.6;
  margin-bottom: 12px;
  white-space: pre-wrap;
  word-break: break-word;
}

.note-footer {
  display: flex;
  justify-content: flex-end;
}

.note-date {
  color: #999;
  font-size: 0.85rem;
}
</style>
