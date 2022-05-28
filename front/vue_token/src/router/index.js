//导入vue
import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

export default new VueRouter({
  mode: 'history',
  routes: [
    //登录页
    { path: '/', component: () => import('../views/login.vue') },
    { path: '/home', component: () => import('../views/home.vue') }
  ]
})
