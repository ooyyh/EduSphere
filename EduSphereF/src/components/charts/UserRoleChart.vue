<template>
  <div class="chart-container">
    <div ref="chartRef" class="chart"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      adminCount: 0,
      teacherCount: 0,
      studentCount: 0
    })
  }
})

const chartRef = ref(null)
let chartInstance = null

const initChart = () => {
  if (!chartRef.value) return
  
  // 如果已存在实例，先销毁
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(chartRef.value)
  
  const option = {
    title: {
      text: '用户角色分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle',
      data: ['管理员', '教师', '学生']
    },
    series: [
      {
        name: '用户角色',
        type: 'pie',
        radius: '60%',
        center: ['60%', '50%'],
        data: [
          { value: props.data.adminCount, name: '管理员', itemStyle: { color: '#F56C6C' } },
          { value: props.data.teacherCount, name: '教师', itemStyle: { color: '#E6A23C' } },
          { value: props.data.studentCount, name: '学生', itemStyle: { color: '#67C23A' } }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
}

const updateChart = () => {
  if (!chartInstance) return
  
  const option = {
    series: [{
      data: [
        { value: props.data.adminCount, name: '管理员', itemStyle: { color: '#F56C6C' } },
        { value: props.data.teacherCount, name: '教师', itemStyle: { color: '#E6A23C' } },
        { value: props.data.studentCount, name: '学生', itemStyle: { color: '#67C23A' } }
      ]
    }]
  }
  
  chartInstance.setOption(option)
}

const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 重新初始化图表
const reinitChart = () => {
  nextTick(() => {
    setTimeout(() => {
      initChart()
    }, 100)
  })
}

// 暴露方法给父组件
defineExpose({
  reinitChart
})

watch(() => props.data, () => {
  updateChart()
}, { deep: true })

onMounted(() => {
  // 使用nextTick确保DOM已渲染
  nextTick(() => {
    setTimeout(() => {
      initChart()
    }, 100)
  })
  window.addEventListener('resize', resizeChart)
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
  window.removeEventListener('resize', resizeChart)
})
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 400px;
}

.chart {
  width: 100%;
  height: 100%;
}
</style>
