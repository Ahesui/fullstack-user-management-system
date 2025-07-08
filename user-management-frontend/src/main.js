import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import { createPinia } from 'pinia' // 1. 导入

const app = createApp(App)
const pinia = createPinia() // 2. 创建Pinia实例

app.use(pinia) // 3. 注册Pinia插件
app.use(router)
app.mount('#app')

// createApp(App).use(router).mount('#app')
