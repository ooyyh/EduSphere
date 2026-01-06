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
      draftCourses: 0,
      pendingCourses: 0,
      publishedCourses: 0,
      rejectedCourses: 0
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
      text: '课程状态分布',
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
      data: ['草稿', '待审核', '已发布', '已拒绝']
    },
    series: [
      {
        name: '课程状态',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
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
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: props.data.draftCourses, name: '草稿', itemStyle: { color: '#909399' } },
          { value: props.data.pendingCourses, name: '待审核', itemStyle: { color: '#E6A23C' } },
          { value: props.data.publishedCourses, name: '已发布', itemStyle: { color: '#67C23A' } },
          { value: props.data.rejectedCourses, name: '已拒绝', itemStyle: { color: '#F56C6C' } }
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
        { value: props.data.draftCourses, name: '草稿', itemStyle: { color: '#909399' } },
        { value: props.data.pendingCourses, name: '待审核', itemStyle: { color: '#E6A23C' } },
        { value: props.data.publishedCourses, name: '已发布', itemStyle: { color: '#67C23A' } },
        { value: props.data.rejectedCourses, name: '已拒绝', itemStyle: { color: '#F56C6C' } }
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
