import permission from './permission'
import pinia from '@/stores/store'
import NProgress from 'nprogress'
import useSystemStore from '@/stores/system'
const systemStore = useSystemStore(pinia)
//批量注册指令(现在就一个permission)
const directives = {
  permission
}
//注册的一般写法，循环遍历directives，通过vue.directive注册
export default {
  install(Vue) {
    Object.keys(directives).forEach((key) => {
      Vue.directive(key, directives[key])
    })
  }
}
