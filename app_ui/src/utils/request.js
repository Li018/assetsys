import axios from 'axios'
import settings from '@/utils/settings'
import useSystemStore from '@/stores/system'
import pinia from '@/stores/store'
import NProgress from 'nprogress'
import { ElMessage } from 'element-plus'
import router from '@/router'
import 'nprogress/nprogress.css'
const $route = router

const systemStore = useSystemStore(pinia)
// const $route = router
// 进度条配置项这样写
NProgress.configure({
  easing: 'ease', // 动画方式，和css动画属性一样（默认：ease）
  speed: 500, // 递增进度条的速度，单位ms（默认： 200）
  showSpinner: true, // 是否显示加载ico
  trickle: true, //是否自动递增
  trickleSpeed: 200, // 自动递增间隔
  minimum: 0.3 // 初始化时的最小百分比，0-1（默认：0.08）
})
const request = axios.create({
  baseURL: settings.url_prefix,
  timeout: 5000
})
//请求拦截器
request.interceptors.request.use((config) => {
  NProgress.start()
  config.headers.Authorization = systemStore.token
  // 返回配置对象，headers属性请求头，经常给服务器端携带公共参数
  // 返回配置对象
  return config
})
// 响应拦截器
request.interceptors.response.use(
  (response) => {
    NProgress.done()
    if (response.data instanceof Blob) {
      return response
    }

    if (response.data.code === 200) {
      return response.data
    } else {
      response.data.msg
        ? showErrorMessage(response.data.msg)
        : showErrorMessage('系统异常，请联系管理员！')
      return Promise.reject(response.data.msg)
    }
  },
  (error) => {
    NProgress.done()
    const status = error.response.status
    if (status === 400) {
      showErrorMessage(error.response.data.msg)
    } else if (status === 500) {
      showErrorMessage('服务器内部异常')
    } else if (status === 401) {
      systemStore.reset()
      $route.replace('/login')
    } else if (status === 403) {
      showErrorMessage('您暂时没有权限！')
    } else if (status === 404) {
      showErrorMessage('访问路径不存在！')
    } else if (status === 405) {
      showErrorMessage('请求方法错误！')
    } else {
      showErrorMessage('系统异常！')
    }

    return Promise.reject(error.response.data.msg)
  }
)
const showErrorMessage = (msg) => {
  ElMessage({
    showClose: true,
    message: msg,
    type: 'error'
  })
}
export default request
