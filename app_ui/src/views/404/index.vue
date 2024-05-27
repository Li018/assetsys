<script setup>
import router from '@/router'
//返回首页
const goToHomePage = () => {
  router.replace('/')
}
</script>

<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="not-found-title">404</h1>
      <p class="not-found-message">页面未找到</p>
      <p>
        对不起，您正在寻找的页面不存在。尝试检查URL的错误，然后按浏览器上的刷新按钮或尝试在我们的应用程序中找到其他内容。
      </p>
      <div class="not-found-buttons">
        <el-button type="primary" @click="goToHomePage">返回主页</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.not-found-content {
  text-align: center;
}

.not-found-title {
  font-size: 120px;
  color: #333;
}

.not-found-message {
  font-size: 24px;
  color: #666;
  margin-bottom: 20px;
}

.not-found-buttons {
  margin-top: 20px;
}
</style>
