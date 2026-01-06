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
      totalLessons: 0,
      completedLessons: 0,
      inProgressLessons: 0
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
      text: '学习进度统计',
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
      data: ['已完成', '进行中', '未开始']
    },
    series: [
      {
        name: '学习进度',
        type: 'pie',
        radius: ['30%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { 
            value: props.data.completedLessons, 
            name: '已完成', 
            itemStyle: { color: '#67C23A' } 
          },
          { 
            value: props.data.inProgressLessons, 
            name: '进行中', 
            itemStyle: { color: '#E6A23C' } 
          },
          { 
            value: props.data.totalLessons - props.data.completedLessons - props.data.inProgressLessons, 
            name: '未开始', 
            itemStyle: { color: '#909399' } 
          }
        ]
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
        { 
          value: props.data.completedLessons, 
          name: '已完成', 
          itemStyle: { color: '#67C23A' } 
        },
        { 
          value: props.data.inProgressLessons, 
          name: '进行中', 
          itemStyle: { color: '#E6A23C' } 
        },
        { 
          value: props.data.totalLessons - props.data.completedLessons - props.data.inProgressLessons, 
          name: '未开始', 
          itemStyle: { color: '#909399' } 
        }
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
