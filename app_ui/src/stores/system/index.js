import { defineStore } from 'pinia'

// useStore 可以是 useUser、useCart 之类的任何东西
// 第一个参数是应用程序中 store 的唯一 id
const useSystemStore = defineStore('System', {
  // other options...
  // 推荐使用 完整类型推断的箭头函数
  state: () => {
    return {
      // 所有这些属性都将自动推断其类型
      fold: false,
      token: localStorage.getItem('token'),
      userInfo: {},
      hasRouter: false,
      routers: [],
      permissionPerms: [],
      backgroundColor: localStorage.getItem('bgc') ? localStorage.getItem('bgc') : '#2f4056',
      textColor: localStorage.getItem('tc') ? localStorage.getItem('tc') : '#FFFFFF',
      activeTextcolor: localStorage.getItem('at') ? localStorage.getItem('at') : '#409EFF'
    }
  },
  actions: {
    setToken(token) {
      localStorage.setItem('token', token)
      this.token = localStorage.getItem('token')
    },
    setBgc(value) {
      localStorage.setItem('bgc', value)
      this.backgroundColor = localStorage.getItem('bgc')
    },
    setTextColor(value) {
      localStorage.setItem('tc', value)
      this.textColor = localStorage.getItem('tc')
    },
    setAtiveTextcolor(value) {
      localStorage.setItem('at', value)
      this.activeTextcolor = localStorage.getItem('at')
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
    },
    setRouters(routers) {
      this.routers = routers
    },
    setAuthorities(permissionPerms) {
      this.permissionPerms = permissionPerms
    },
    reset() {
      localStorage.clear()
      ;(this.token = undefined),
        (this.userInfo = {}),
        (this.routers = []),
        (this.permissionPerms = [])
      this.hasRouter = false
    }
  },
  getters: {}
})
export default useSystemStore
