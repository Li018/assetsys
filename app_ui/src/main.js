import '@/styles/reset.css'
import '@/styles/global.css'
import DataV from '@kjgl77/datav-vue3';
// 导入elementui

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/styles/theme.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import Directive from '@/utils/index'
import locale from 'element-plus/es/locale/lang/zh-cn'
import pinia from '@/stores/store'
import App from './App.vue'
import router from './router'
import { createApp } from 'vue'
const app = createApp(App)

app.use(router)
app.use(pinia)
// 使用饿了么ui
app.use(ElementPlus, { locale })
// 导入图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
// 注册自定义事件
app.use(Directive)
app.use(DataV);

app.mount('#app')
