<template>
  <div class="account-container">
    <div class="account-card">
      <div class="logo-area">
        <h1>EduSphere</h1>
        <p>知识无界，学习无限</p>
      </div>
      <div class="tabs-area">
        <el-tabs v-model="activeTab" class="account-tabs">
          <el-tab-pane name="login" label="登录">
            <router-view v-if="activeTab === 'login'" />
          </el-tab-pane>
          <el-tab-pane name="register" label="注册">
            <router-view v-if="activeTab === 'register'" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeTab = ref('login')

// 根据当前路由设置活动标签
watch(
  () => route.path,
  (path) => {
    if (path.includes('register')) {
      activeTab.value = 'register'
    } else {
      activeTab.value = 'login'
    }
  },
  { immediate: true }
)

// 当标签切换时更新路由
watch(activeTab, (tab) => {
  if (tab === 'register' && !route.path.includes('register')) {
    router.push('/account/register')
  } else if (tab === 'login' && !route.path.includes('login')) {
    router.push('/account/login')
  }
})
</script>

<style scoped>
.account-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.account-card {
  width: 420px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  padding: 30px;
}

.logo-area {
  text-align: center;
  margin-bottom: 30px;
}

.logo-area h1 {
  font-size: 28px;
  color: #409eff;
  margin-bottom: 8px;
}

.logo-area p {
  color: #909399;
  font-size: 14px;
}

.tabs-area {
  margin-top: 20px;
}

.account-tabs {
  width: 100%;
}

:deep(.el-tabs__nav) {
  width: 100%;
  display: flex;
}

:deep(.el-tabs__item) {
  flex: 1;
  text-align: center;
}
</style>