module.exports = {
  devServer: {
    disableHostCheck: false,
    host: 'localhost', // 主机地址
    port: 8081, // 端口号
    https: false,
    hotOnly: false,
    // 代理配置，常见的请求就两种，跨域和代理，日常开发中应该是代理用的最多
    proxy: {
      '/api': {
        // ‘/’表示地址的默认请求IP，可以通过不同访问路径的开头区别不同地址。如'/api'就是以api
        target: 'http://localhost:8082', // 后端地址及端口号
        ws: true,
        changeOrigin: true, // 是否跨域
        pathRewrite: {
          // 路径重写
          '^/api': ''
        }
      }
    }
  }
}
