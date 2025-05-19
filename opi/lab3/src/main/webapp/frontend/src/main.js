import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// Настройка axios
axios.defaults.withCredentials = true
axios.defaults.baseURL = '/api'

// Обработка ошибок авторизации
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

createApp(App).use(router).mount('#app') 