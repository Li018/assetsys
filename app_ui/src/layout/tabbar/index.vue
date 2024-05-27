<script setup>
import { ref } from 'vue'
import {DataLine, Expand, Fold, FullScreen, InfoFilled, Loading, Setting} from '@element-plus/icons-vue'
import useSystemStore from '@/stores/system'
import router from '@/router'
import pinia from '@/stores/store'
import request from '@/utils/request'
import {ElMessage} from "element-plus";

const isFullScreen = ref(false)
const systemStore = useSystemStore(pinia)
// const backgroudColor = localStorage.getItem('bgc')
//   ? ref(localStorage.getItem('bgc'))
//   : ref('#2f4056')
const $route = router
const drawer = ref(false)
const predefineColors = ref([
  '#2f4056',
  '#FFFFFF',
  '#409EFF',
  '#ff4500',
  '#ff8c00',
  '#ffd700',
  '#90ee90',
  '#00ced1',
  '#1e90ff',
  '#c71585'
])
// 修改图标
const changeIcon = () => {
  systemStore.fold = !systemStore.fold
}
//修改全屏
const changeFullScreen = () => {
  isFullScreen.value = !isFullScreen.value
  if (isFullScreen.value) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}
//刷新页面
const refsh = () => {
  window.location.reload()
}
// 退出登录
const handleLogOut = async () => {
  await request.post('/auth/logout')
  await $route.replace('/login')
  systemStore.reset()
}
// 修改个人信息
const handleInformation = async () => {
  await $route.replace('/information')
}
//展示抽屉
const handleDrawer = () => {
  drawer.value = true
}
const handleBgc = () => {
  if (systemStore.backgroundColor) {
    systemStore.setBgc(systemStore.backgroundColor)
  } else {
    systemStore.setBgc(localStorage.getItem('bgc') ? localStorage.getItem('bgc') : '#2f4056')
  }
}
const handleTextColor = () => {
  if (systemStore.textColor) {
    systemStore.setTextColor(systemStore.textColor)
  } else {
    systemStore.setTextColor(localStorage.getItem('tc') ? localStorage.getItem('tc') : '#FFFFFF')
  }
}
const handleActiveTextcolor = () => {
  if (systemStore.activeTextcolor) {
    systemStore.setAtiveTextcolor(systemStore.activeTextcolor)
  } else {
    systemStore.setAtiveTextcolor(
      localStorage.getItem('at') ? localStorage.getItem('at') : '#409EFF'
    )
  }
}
const resetColor = () => {
  systemStore.setBgc('#2f4056')
  systemStore.setTextColor('#FFFFFF')
  systemStore.setTextColor('#FFFFFF')
    drawer.value=false
    ElMessage({
        showClose: true,
        message: '重置配色成功',
        type: 'success'
    })
}
</script>
<script>
export default {
  name: 'Tabbar'
}
</script>
<template>
  <el-row>
    <el-col :xs="24" :sm="12" :md="10" :lg="16" :xl="20" style="display: flex; align-items: center">
      <el-icon size="20" style="margin: 0 10px">
        <component :is="systemStore.fold ? Expand : Fold" @click="changeIcon"></component>
      </el-icon>
      <el-breadcrumb separator="/" style="font-family: -apple-system">
        <el-breadcrumb-item
          v-for="(item, index) in $route.currentRoute.value.matched"
          v-show="item.name"
          :key="index.toString()"
          :to="item.path"
        >
          <el-icon>
            <component :is="item.meta.icon"></component>
          </el-icon>
          {{ item.meta.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </el-col>
    <el-col :xs="0" :sm="12" :md="14" :lg="8" :xl="4" style="display: flex; align-items: center">
        <el-popconfirm
                width="220"
                confirm-button-text="确认"
                cancel-button-text="取消"
                :icon="InfoFilled"
                icon-color="#626AEF"
                title="确认跳转到可视化页面？"
                @confirm="$route.push('/dataline')"
        >
            <template #reference>
                <el-button type="info"  :icon="DataLine" circle />
            </template>
        </el-popconfirm>

      <el-button type="warning" @click="handleDrawer" :icon="Setting" circle />
      <el-button type="primary" @click="refsh" :icon="Loading" circle />
      <el-button type="success" @click="changeFullScreen" :icon="FullScreen" circle />

      <el-avatar :src="systemStore.userInfo.avatar" :size="33" style="margin: 0 8px"> </el-avatar>
      <el-dropdown style="margin: 0 10px; cursor: pointer">
        <span class="el-dropdown-link">
          {{ systemStore.userInfo.username }}
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleInformation">个人信息</el-dropdown-item>
          </el-dropdown-menu>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleLogOut">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-col>
  </el-row>

  <el-drawer v-model="drawer" title="系统外观配置" :with-header="true">
    <el-form>
      <el-form-item label="菜单背景">
        <el-color-picker
          v-model="systemStore.backgroundColor"
          size="small"
          @change="handleBgc"
          :predefine="predefineColors"
        />
      </el-form-item>
      <el-form-item label="字体颜色">
        <el-color-picker
          v-model="systemStore.textColor"
          size="small"
          @change="handleTextColor"
          :predefine="predefineColors"
        />
      </el-form-item>
      <el-form-item label="选中颜色">
        <el-color-picker
          v-model="systemStore.activeTextcolor"
          size="small"
          @change="handleActiveTextcolor"
          :predefine="predefineColors"
        />
      </el-form-item>
      <el-button type="warning" plain round @click="resetColor">恢复默认配色</el-button>
    </el-form>
  </el-drawer>
</template>

<style scoped lang="scss">
.el-row {
  height: 100%;
  box-shadow: 0px 1px 1px rgba(0, 0, 0, 0.1); /* 阴影效果 */
}
</style>
