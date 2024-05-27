const modules = import.meta.glob('/src/views/**/**.vue')
// 常量路由
const constrouters = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/dataline',
    name: 'dataline',
    component: () => import('@/views/dataline/index.vue')
  },
  {
    //404
    path: '/404',
    component: () => import('@/views/404/index.vue'),
    name: '404'
  }
]

// 获取菜单信息
const getRouters = (data) => {
  const res = data
  const router = [
    {
      path: '/',
      name: '',
      component: () => import('@/layout/index.vue'),
      redirect: '/home',
      children: [
        {
          path: '/home',
          name: 'home',
          component: import('@/views/home/index.vue'),
          meta: {
            icon: 'HomeFilled',
            title: '系统首页',
            hidden: false
          }
        },
        {
          path: '/information',
          name: 'information',
          component: import('@/views/information/index.vue'),
          meta: {
            icon: 'User',
            title: '个人信息',
            hidden: true
          }
        }
      ]
    }
  ]

  res.forEach((routeData) => {
    router[0].children.push(addRouter(routeData))
  })
  router[1] = {
    //登录成功展示数据的路由
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    name: 'Any',
    meta: {
      title: '404',
      hidden: true,
      icon: 'CircleCloseFilled'
    }
  }

  return router
}
// 菜单转成路由
const addRouter = (routeData) => {
  const route = {
    path: routeData.path,
    name: routeData.perms,
    meta: routeData.meta
  }
  route.component = modules[`/src/views${routeData.component}.vue`]
  if (routeData.children && routeData.children.length > 0) {
    route.redirect = routeData.children[0].path
    route.children = routeData.children.map((childRoute) => addRouter(childRoute))
  }
  return route
}

export default constrouters
export { getRouters }
