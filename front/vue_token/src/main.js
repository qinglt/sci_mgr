import Vue from 'vue'
import App from './App.vue'

import ViewUI from 'view-design'
import 'view-design/dist/styles/iview.css'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import router from './router'
import axios from './axios/index'

Vue.config.productionTip = false
Vue.prototype.$request = axios

Vue.use(ViewUI)
Vue.use(ElementUI)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
