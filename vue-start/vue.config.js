const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  devServer: {
    proxy: {
      '/person': {
        target: 'http://localhost:50010',
        ws: true,
        changeOrigin: true
      },
      '/prefix': {
        target: 'http://localhost:50010',
        // 路径重写，去掉前缀 /prefix
        pathRewriter: {'^/prefix': ''},
        ws: true,
        changeOrigin: true
      }
    }
  }
})
