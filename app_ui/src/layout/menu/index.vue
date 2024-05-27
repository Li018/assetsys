<script setup>
import { useRouter } from 'vue-router'
//获取父组件传递过来的全部路由数组
defineProps(['menuList'])

//获取路由器对象
let $router = useRouter()
//点击菜单的回调
</script>
<script>
export default {
  name: 'SysMenu'
}
</script>

<template>
  <template v-for="(item, index) in menuList" :key="item.path">
    <!--没有子路由-->
    <template v-if="item.children.length === 0 && item.component !== ''">
      <el-menu-item :index="item.path" v-if="!item.meta.hidden">
        <el-icon>
          <component :is="item.meta.icon"></component>
        </el-icon>
        <template #title>
          <span>{{ item.meta.title }}</span>
        </template>
      </el-menu-item>
    </template>
    <!-- 有子路由且个数大于一个1 -->
    <el-sub-menu :index="item.path" v-if="item.children && item.children.length > 0">
      <template #title>
        <el-icon>
          <component :is="item.meta.icon"></component>
        </el-icon>
        <span>{{ item.meta.title }}</span>
      </template>

      <SysMenu :menuList="item.children"></SysMenu>
    </el-sub-menu>
  </template>
</template>

<style scoped></style>
