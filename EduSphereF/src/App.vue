<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 初始化用户状态
onMounted(() => {
  authStore.initUser()
})
</script>
<template>
  <RouterView />
</template>

<style scoped>
</style>
