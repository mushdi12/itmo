const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  outputDir: '../resources/static',
  devServer: {
    port: 8082,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        logLevel: 'debug',
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  }
}) 