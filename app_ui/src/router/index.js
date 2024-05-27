import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import useSystemStore from '@/stores/system'
import pinia from '@/stores/store'
import constrouters, { getRouters } from '@/router/routers'
import { ref } from 'vue'
import request from '../utils/request'

const systemStore = useSystemStore(pinia)

// 进度条配置项这样写
NProgress.configure({
  easing: 'ease', // 动画方式，和css动画属性一样（默认：ease）
  speed: 500, // 递增进度条的速度，单位ms（默认： 200）
  showSpinner: true, // 是否显示加载ico
  trickle: true, //是否自动递增
  trickleSpeed: 200, // 自动递增间隔
  minimum: 0.3 // 初始化时的最小百分比，0-1（默认：0.08）
})
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constrouters
})

const isGetRoute = ref(false)
router.beforeEach(async (to, from, next) => {
  NProgress.start()

  if (to.path === '/login') {
    next()
  } else if (!systemStore.hasRouter) {
    const res = await request.get('/auth/authInfo')
    systemStore.routers = res.data.routers

    systemStore.userInfo = res.data.userInfo
    systemStore.permissionPerms = res.data.permissionPerms

    const asyncRouters = getRouters(systemStore.routers)
    asyncRouters.forEach((req) => {
      router.addRoute(req)
    })
    next({ ...to, replace: true })
    systemStore.hasRouter = true
  } else {
    next()
  }
})
// 路由跳转后钩子函数中 - 执行进度条加载结束
router.afterEach((to, from, next) => {
  NProgress.done()
})
export default router
