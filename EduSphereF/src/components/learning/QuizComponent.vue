<template>
  <div class="quiz-component">
    <div v-if="!quizStarted" class="quiz-start">
      <div class="quiz-header">
        <h3>{{ quizData?.title || '课程测验' }}</h3>
        <p v-if="quizData?.description">{{ quizData.description }}</p>
      </div>
      
      <div class="quiz-info">
        <div class="info-item">
          <el-icon><QuestionFilled /></el-icon>
          <span>{{ quizData?.questions?.length || 0 }} 道题目</span>
        </div>
        <div class="info-item">
          <el-icon><Clock /></el-icon>
          <span>预计用时 {{ quizData?.timeLimit || 30 }} 分钟</span>
        </div>
        <div class="info-item">
          <el-icon><Trophy /></el-icon>
          <span>及格分数 {{ quizData?.passingScore || 60 }} 分</span>
        </div>
      </div>
      
      <div class="quiz-actions">
        <el-button type="primary" size="large" @click="startQuiz">
          <el-icon><VideoPlay /></el-icon>
          开始测验
        </el-button>
      </div>
    </div>
    
    <div v-else-if="!quizCompleted" class="quiz-content">
      <!-- 测验头部 -->
      <div class="quiz-header">
        <div class="quiz-title">
          <h3>{{ quizData?.title || '课程测验' }}</h3>
          <div class="quiz-progress">
            <span>第 {{ currentQuestionIndex + 1 }} 题，共 {{ quizData?.questions?.length || 0 }} 题</span>
            <el-progress 
              :percentage="Math.round(((currentQuestionIndex + 1) / (quizData?.questions?.length || 1)) * 100)"
              :show-text="false"
              style="width: 200px;"
            />
          </div>
        </div>
        <div class="quiz-timer" v-if="quizData?.timeLimit">
          <el-icon><Clock /></el-icon>
          <span>{{ formatTime(remainingTime) }}</span>
        </div>
      </div>
      
      <!-- 题目内容 -->
      <div class="question-content">
        <div class="question-header">
          <h4>{{ currentQuestion?.title }}</h4>
          <div class="question-meta">
            <el-tag :type="getQuestionTypeColor(currentQuestion?.type)" size="small">
              {{ getQuestionTypeText(currentQuestion?.type) }}
            </el-tag>
            <span class="question-score">{{ currentQuestion?.score || 1 }} 分</span>
          </div>
        </div>
        
        <div class="question-body">
          <!-- 单选题 -->
          <div v-if="currentQuestion?.type === 'single'" class="single-choice">
            <el-radio-group v-model="currentAnswer" @change="onAnswerChange">
              <el-radio 
                v-for="(option, index) in currentQuestion.options" 
                :key="index"
                :label="index"
                class="option-item"
              >
                <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
                <span class="option-text">{{ option }}</span>
              </el-radio>
            </el-radio-group>
          </div>
          
          <!-- 多选题 -->
          <div v-else-if="currentQuestion?.type === 'multiple'" class="multiple-choice">
            <el-checkbox-group v-model="currentAnswer" @change="onAnswerChange">
              <el-checkbox 
                v-for="(option, index) in currentQuestion.options" 
                :key="index"
                :label="index"
                class="option-item"
              >
                <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
                <span class="option-text">{{ option }}</span>
              </el-checkbox>
            </el-checkbox-group>
          </div>
          
          <!-- 判断题 -->
          <div v-else-if="currentQuestion?.type === 'true-false'" class="true-false">
            <el-radio-group v-model="currentAnswer" @change="onAnswerChange">
              <el-radio label="true" class="option-item">
                <span class="option-label">A.</span>
                <span class="option-text">正确</span>
              </el-radio>
              <el-radio label="false" class="option-item">
                <span class="option-label">B.</span>
                <span class="option-text">错误</span>
              </el-radio>
            </el-radio-group>
          </div>
          
          <!-- 填空题 -->
          <div v-else-if="currentQuestion?.type === 'fill'" class="fill-blank">
            <div class="fill-content" v-html="formatFillQuestion(currentQuestion?.title)"></div>
            <div class="fill-inputs">
              <el-input
                v-for="(blank, index) in currentQuestion?.blanks"
                :key="index"
                v-model="currentAnswer[index]"
                :placeholder="`请输入第${index + 1}个空`"
                @input="onAnswerChange"
                class="fill-input"
              />
            </div>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="quiz-actions">
        <el-button @click="previousQuestion" :disabled="currentQuestionIndex === 0">
          <el-icon><ArrowLeft /></el-icon>
          上一题
        </el-button>
        <el-button 
          v-if="currentQuestionIndex < (quizData?.questions?.length || 0) - 1"
          type="primary" 
          @click="nextQuestion"
        >
          下一题
          <el-icon><ArrowRight /></el-icon>
        </el-button>
        <el-button 
          v-else
          type="success" 
          @click="submitQuiz"
        >
          <el-icon><Check /></el-icon>
          提交测验
        </el-button>
      </div>
    </div>
    
    <!-- 测验结果 -->
    <div v-else class="quiz-result">
      <div class="result-header">
        <el-icon class="result-icon" :class="resultClass">
          <component :is="resultIcon" />
        </el-icon>
        <h3>{{ resultTitle }}</h3>
        <p>{{ resultMessage }}</p>
      </div>
      
      <div class="result-stats">
        <div class="stat-item">
          <div class="stat-value">{{ quizResult.score }}</div>
          <div class="stat-label">总分</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ quizResult.correctCount }}</div>
          <div class="stat-label">正确题数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ quizResult.totalCount }}</div>
          <div class="stat-label">总题数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ Math.round((quizResult.correctCount / quizResult.totalCount) * 100) }}%</div>
          <div class="stat-label">正确率</div>
        </div>
      </div>
      
      <div class="result-actions">
        <el-button @click="restartQuiz">重新测验</el-button>
        <el-button type="primary" @click="finishQuiz">完成测验</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  QuestionFilled, Clock, Trophy, VideoPlay, ArrowLeft, ArrowRight, 
  Check, CircleCheck, CircleClose 
} from '@element-plus/icons-vue'

const props = defineProps({
  quizData: {
    type: Object,
    default: () => ({})
  },
  lessonId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['quiz-completed'])

// 响应式数据
const quizStarted = ref(false)
const quizCompleted = ref(false)
const currentQuestionIndex = ref(0)
const currentAnswer = ref(null)
const answers = ref({})
const remainingTime = ref(0)
const timer = ref(null)
const quizResult = ref({})

// 计算属性
const currentQuestion = computed(() => {
  return props.quizData?.questions?.[currentQuestionIndex.value]
})

const resultClass = computed(() => {
  const score = quizResult.value.score || 0
  const passingScore = props.quizData?.passingScore || 60
  return score >= passingScore ? 'success' : 'failed'
})

const resultIcon = computed(() => {
  return resultClass.value === 'success' ? CircleCheck : CircleClose
})

const resultTitle = computed(() => {
  const score = quizResult.value.score || 0
  const passingScore = props.quizData?.passingScore || 60
  return score >= passingScore ? '恭喜通过测验！' : '测验未通过'
})

const resultMessage = computed(() => {
  const score = quizResult.value.score || 0
  const passingScore = props.quizData?.passingScore || 60
  if (score >= passingScore) {
    return `您获得了 ${score} 分，超过了及格分数 ${passingScore} 分！`
  } else {
    return `您获得了 ${score} 分，未达到及格分数 ${passingScore} 分，请重新学习后再次测验。`
  }
})

// 开始测验
const startQuiz = () => {
  quizStarted.value = true
  currentQuestionIndex.value = 0
  answers.value = {}
  
  // 初始化答案
  if (currentQuestion.value?.type === 'fill') {
    currentAnswer.value = new Array(currentQuestion.value.blanks?.length || 0).fill('')
  } else {
    currentAnswer.value = currentQuestion.value?.type === 'multiple' ? [] : null
  }
  
  // 开始计时
  if (props.quizData?.timeLimit) {
    remainingTime.value = props.quizData.timeLimit * 60 // 转换为秒
    startTimer()
  }
}

// 开始计时器
const startTimer = () => {
  timer.value = setInterval(() => {
    remainingTime.value--
    if (remainingTime.value <= 0) {
      submitQuiz()
    }
  }, 1000)
}

// 停止计时器
const stopTimer = () => {
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }
}

// 格式化时间
const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 答案变化处理
const onAnswerChange = () => {
  // 保存当前答案
  answers.value[currentQuestionIndex.value] = currentAnswer.value
}

// 上一题
const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
    loadQuestionAnswer()
  }
}

// 下一题
const nextQuestion = () => {
  if (currentQuestionIndex.value < (props.quizData?.questions?.length || 0) - 1) {
    currentQuestionIndex.value++
    loadQuestionAnswer()
  }
}

// 加载题目答案
const loadQuestionAnswer = () => {
  const savedAnswer = answers.value[currentQuestionIndex.value]
  if (savedAnswer !== undefined) {
    currentAnswer.value = savedAnswer
  } else {
    // 初始化新答案
    if (currentQuestion.value?.type === 'fill') {
      currentAnswer.value = new Array(currentQuestion.value.blanks?.length || 0).fill('')
    } else {
      currentAnswer.value = currentQuestion.value?.type === 'multiple' ? [] : null
    }
  }
}

// 提交测验
const submitQuiz = () => {
  stopTimer()
  
  // 保存最后一题答案
  answers.value[currentQuestionIndex.value] = currentAnswer.value
  
  // 计算分数
  const result = calculateScore()
  quizResult.value = result
  quizCompleted.value = true
  
  // 发送结果
  emit('quiz-completed', result)
}

// 计算分数
const calculateScore = () => {
  const questions = props.quizData?.questions || []
  let correctCount = 0
  let totalScore = 0
  
  questions.forEach((question, index) => {
    const userAnswer = answers.value[index]
    const correctAnswer = question.answer
    const questionScore = question.score || 1
    
    let isCorrect = false
    
    if (question.type === 'single' || question.type === 'true-false') {
      isCorrect = userAnswer === correctAnswer
    } else if (question.type === 'multiple') {
      isCorrect = JSON.stringify(userAnswer?.sort()) === JSON.stringify(correctAnswer?.sort())
    } else if (question.type === 'fill') {
      isCorrect = JSON.stringify(userAnswer) === JSON.stringify(correctAnswer)
    }
    
    if (isCorrect) {
      correctCount++
      totalScore += questionScore
    }
  })
  
  return {
    score: totalScore,
    correctCount,
    totalCount: questions.length,
    answers: answers.value
  }
}

// 重新测验
const restartQuiz = () => {
  quizStarted.value = false
  quizCompleted.value = false
  currentQuestionIndex.value = 0
  currentAnswer.value = null
  answers.value = {}
  remainingTime.value = 0
  stopTimer()
}

// 完成测验
const finishQuiz = () => {
  emit('quiz-completed', quizResult.value)
}

// 获取题目类型颜色
const getQuestionTypeColor = (type) => {
  const colors = {
    single: 'primary',
    multiple: 'success',
    'true-false': 'warning',
    fill: 'info'
  }
  return colors[type] || 'default'
}

// 获取题目类型文本
const getQuestionTypeText = (type) => {
  const texts = {
    single: '单选题',
    multiple: '多选题',
    'true-false': '判断题',
    fill: '填空题'
  }
  return texts[type] || '未知'
}

// 格式化填空题
const formatFillQuestion = (title) => {
  if (!title) return ''
  
  let formattedTitle = title
  const blanks = props.quizData?.questions?.[currentQuestionIndex.value]?.blanks || []
  
  blanks.forEach((blank, index) => {
    const placeholder = `___${index + 1}___`
    formattedTitle = formattedTitle.replace(placeholder, `<span class="blank-placeholder">第${index + 1}空</span>`)
  })
  
  return formattedTitle
}

// 组件卸载时清理计时器
onUnmounted(() => {
  stopTimer()
})
</script>

<style scoped>
.quiz-component {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

/* 测验开始页面 */
.quiz-start {
  text-align: center;
  padding: 40px 20px;
}

.quiz-header h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 1.5rem;
}

.quiz-header p {
  margin: 0 0 30px 0;
  color: #666;
  font-size: 1rem;
}

.quiz-info {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 40px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 0.9rem;
}

.info-item .el-icon {
  color: #409EFF;
}

/* 测验内容 */
.quiz-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.quiz-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}

.quiz-title h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 1.2rem;
}

.quiz-progress {
  display: flex;
  align-items: center;
  gap: 15px;
}

.quiz-progress span {
  font-size: 0.9rem;
  color: #666;
}

.quiz-timer {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #E6A23C;
  font-weight: 600;
}

/* 题目内容 */
.question-content {
  padding: 30px;
}

.question-header {
  margin-bottom: 25px;
}

.question-header h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 1.1rem;
  line-height: 1.6;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.question-score {
  color: #409EFF;
  font-weight: 600;
}

.question-body {
  margin-bottom: 30px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.option-item:hover {
  background: #f0f9ff;
}

.option-label {
  font-weight: 600;
  color: #409EFF;
  margin-right: 10px;
  min-width: 20px;
}

.option-text {
  flex: 1;
  line-height: 1.5;
}

/* 填空题 */
.fill-content {
  margin-bottom: 20px;
  line-height: 1.8;
  font-size: 1rem;
}

.blank-placeholder {
  background: #f0f9ff;
  color: #409EFF;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.fill-inputs {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.fill-input {
  max-width: 300px;
}

/* 操作按钮 */
.quiz-actions {
  display: flex;
  justify-content: space-between;
  padding: 20px 30px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
}

/* 测验结果 */
.quiz-result {
  text-align: center;
  padding: 40px 20px;
}

.result-header {
  margin-bottom: 40px;
}

.result-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.result-icon.success {
  color: #67C23A;
}

.result-icon.failed {
  color: #F56C6C;
}

.result-header h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 1.5rem;
}

.result-header p {
  margin: 0;
  color: #666;
  font-size: 1rem;
}

.result-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 40px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 2rem;
  font-weight: 600;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quiz-info {
    flex-direction: column;
    gap: 20px;
  }
  
  .quiz-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .quiz-progress {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .result-stats {
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .quiz-actions {
    flex-direction: column;
    gap: 15px;
  }
}
</style>
