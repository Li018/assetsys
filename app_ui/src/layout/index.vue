<script setup>
import Tabbar from '@/layout/tabbar/index.vue'
import SysMenu from '@/layout/menu/index.vue'
import useSystemStore from '@/stores/system'
import Main from '@/layout/main/index.vue'
import Logo from '@/layout/menu/logo/index.vue'
import request from '@/utils/request'

const systemStore = useSystemStore()
</script>
<script>
export default {
  name: 'Layout'
}
</script>
<template>

  <div class="layout">
    <!--    左侧菜单-->
    <div
      class="layout_menu"
      :class="{ fold: systemStore.fold ? true : false }"
      :style="{
        backgroundColor: systemStore.backgroundColor
      }"
    >
      <Logo></Logo>
      <el-menu
        :background-color="systemStore.backgroundColor"
        :text-color="systemStore.textColor"
        :collapse="systemStore.fold"
        :default-active="this.$route.path"
        :collapse-transition="false"
        :active-text-color="systemStore.activeTextcolor"
        router
      >
        <el-menu-item index="/home">
          <el-icon>
            <HomeFilled />
          </el-icon>
          <template #title>
            <span>系统首页</span>
          </template>
        </el-menu-item>
        <SysMenu :menuList="systemStore.routers"></SysMenu>
      </el-menu>
    </div>
    <!--    头部导航栏-->
    <div class="layout_tabbar" :class="{ fold: systemStore.fold ? true : false }">
      <Tabbar></Tabbar>
    </div>
    <!--    main页面-->
    <div class="layout_main" :class="{ fold: systemStore.fold ? true : false }">
      <div>
        <Main></Main>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.el-menu {
  border-right: none !important;
}
.layout {
  width: 100%;
  height: 100vh;

  .layout_menu {
    width: 200px;
    height: 100vh;

    transition: all 0.1s ease-in-out;
    &.fold {
      width: 64px;
    }
  }
  .layout_tabbar {
    position: fixed;
    height: 50px;
    width: calc(100% - 200px);
    top: 0;
    left: 200px;
    background-color: #fff;
    z-index: 1;
    transition: all 0.1s ease-in-out;
    &.fold {
      width: calc(100% - 64px);
      left: 64px;
    }
  }
  .layout_main {
    z-index: 0;
    position: absolute;
    top: 50px;
    left: 200px;
    padding: 20px 20px;
    width: calc(100% - 200px);
    height: calc(100vh - 50px);

    transition: all 0.1s ease-in-out;
    overflow: auto;
    &.fold {
      width: calc(100% - 64px);
      left: 64px;
    }
  }
}

</style>
