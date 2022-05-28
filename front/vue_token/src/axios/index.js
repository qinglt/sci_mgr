import axios from 'axios'
import { Message } from 'element-ui'
//设置默认请求头
axios.defaults.headers['Content-Type'] = 'application/json; charset=utf-8'
//创建一个axios对象
const instance = axios.create({
  baseURL: '/api',
  timeout: 50000,
  //表示跨域请求时是否需要使用凭证
  withCredentials: false, // default
  //表示服务器响应的数据类型，可以是 'arraybuffer', 'blob', 'document', 'json', 'text', 'stream'
  responseType: 'json', // default
  //响应编码
  //Note: Ignored for `responseType` of 'stream' or client-side requests
  responseEncoding: 'utf8', // default
  //xsrf token 的值的cookie的名称
  xsrfCookieName: 'XSRF-TOKEN', // default
  //the name of the http header that carries the xsrf token value
  xsrfHeaderName: 'X-XSRF-TOKEN', // default
  // `onUploadProgress` 允许为上传处理进度事件
  // onUploadProgress: function (progressEvent) {
  //   // Do whatever you want with the native progress event
  // },
  // // `onDownloadProgress` 允许为下载处理进度事件
  // onDownloadProgress: function (progressEvent) {
  //   // 对原生进度事件的处理
  // }
})

//请求拦截
//所有的网络请求都会先走这个方法
instance.interceptors.request.use(
  config => {
    console.group('全局↑--请求--↑拦截', config.url)
    console.log(config)
    console.groupEnd()
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

//响应拦截
//所有的网络请求返回数据之后都会先执行此方法
//可以根据服务器的返回状态码做相应的处理
instance.interceptors.response.use(
  response => {
    console.group('全局↓--响应--↓拦截', response.config.url)
    console.log(response)
    console.groupEnd()
    if (response.data.code !== 200) {
      Message.error(response.data.obj)
    }
    return response
  },
  error => {
    console.log('TCL: error', error)
    const msg = error.Message !== undefined ? error.Message : ''
    Message({
      message: '网络错误' + msg,
      type: 'error',
      duration: 3 * 1000
    })
    return Promise.reject(error)
  }
)

export default {
  get(url, params) {
    if (window.sessionStorage.getItem('token')) {
      return instance.get(url, {
        params: params,
        headers: { Authorization: window.sessionStorage.getItem('token') }
      })
    } else {
      return instance.get(url, params)
    }
  },

  post(url, data) {
    if (window.sessionStorage.getItem('token')) {
      return instance.post(url, data, {
        headers: { Authorization: window.sessionStorage.getItem('token') }
      })
    } else {
      return instance.post(url, data, {})
    }
  },

  // 只在登录时提交表单使用
  postForm(url, data) {
    let params = new URLSearchParams()
    for (let index in data) {
      params.append(index, data[index])
    }
    //提交表单，修改默认请求头信息
    return instance.post(url, params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
  }
}
