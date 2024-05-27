<script setup>
import useSystemStore from '@/stores/system'
import router from '@/router'
const $route = router
const systemStore = useSystemStore()
// 返回首页
const goHome = () => {
  $route.push('/')
}
</script>
<script>
export default {
  name: 'Logo'
}
</script>
<template>
  <div class="logo" :class="{ fold: systemStore.fold ? true : false }" @click="goHome">
    <img src="/public/logo.png" style="width: 20px; height: 20px; margin-left: 23px" />

    <p style="margin-left: 10px; cursor: pointer; color: white">assetsys</p>
  </div>
</template>

<style scoped lang="scss">
.logo {
  height: 50px;
  width: 200px;
  display: flex;
  align-items: center;

  &.fold {
    p {
      display: none;
    }
  }

  //.el-row{
  //  height: 100%;
  //
  //  .logo_left{
  //      display: flex;
  //      align-items: center;
  //      justify-content: center;
  //  }
  //  .logo_right{
  //    display: flex;
  //    align-items: center;
  //    color: white;
  //    font-family: "Arial Black";
  //  }

  //.el-col{
  //  display: flex;
  //  align-items: center;
  //  font-family: "Arial Black";
  //  color: white;
  //}
  //
  //}
}
</style>
