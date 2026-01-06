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
    type: Array,
    default: () => []
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
  
  // 生成最近7天的日期
  const dates = []
  const platformIncome = []
  const instructorIncome = []
  
  for (let i = 6; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    const dateStr = date.toISOString().split('T')[0]
    dates.push(dateStr)
    
    // 模拟数据，实际应该从props.data获取
    platformIncome.push(Math.floor(Math.random() * 1000) + 500)
    instructorIncome.push(Math.floor(Math.random() * 2000) + 1000)
  }
  
  const option = {
    title: {
      text: '收入趋势',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['平台收入', '讲师收入'],
      top: 'bottom'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '平台收入',
        type: 'line',
        smooth: true,
        data: platformIncome,
        itemStyle: {
          color: '#67C23A'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(103, 194, 58, 0.3)'
            }, {
              offset: 1, color: 'rgba(103, 194, 58, 0.1)'
            }]
          }
        }
      },
      {
        name: '讲师收入',
        type: 'line',
        smooth: true,
        data: instructorIncome,
        itemStyle: {
          color: '#E6A23C'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(230, 162, 60, 0.3)'
            }, {
              offset: 1, color: 'rgba(230, 162, 60, 0.1)'
            }]
          }
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
}

const updateChart = () => {
  if (!chartInstance) return
  
  // 这里可以根据props.data更新图表数据
  // 暂时使用模拟数据
  initChart()
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
