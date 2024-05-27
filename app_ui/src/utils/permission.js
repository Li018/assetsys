import pinia from '@/stores/store'
import NProgress from 'nprogress'
import useSystemStore from '@/stores/system'
import request from '@/utils/request'
import axios from 'axios'
const systemStore = useSystemStore(pinia)

// localStorage.setItem('userInfo', JSON.stringify(req.data.userInfo))
// localStorage.setItem('authorities',JSON.stringify(req.data.principal.authorities))

export default {
  created(el, bindling) {
    //bindling.value为指令的绑定值
    let pers = []
    let perVal = bindling.value
    if (bindling.value) {
      pers = systemStore.permissionPerms.map((permission) => permission)
    } else {
      pers = []
    }
    //假设某用户对某模块只有添加和删除的权限
    //这个权限信息(即pers)应该是不同用户登录时从后台拿到的对应的信息

    //hasPer为true为有权限
    //hasPer为false为无权限
    let hasPer = pers.some((item) => {
      return item == perVal
    })
    //没有权限就先隐藏此元素吧
    if (!hasPer) {
      el.style.display = 'none'
    }
  }
}
