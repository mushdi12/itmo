<template>
  <div class="start-page">
    <div class="background"></div>
    <div class="auth-container">
      <div class="header">
        <h2>Лабораторная работа #4</h2>
        <p>Выполнил: ФИО, группа P3212</p>
        <p>Вариант: 12012</p>
      </div>
      
      <form @submit.prevent="handleSubmit" class="auth-form">
        <div class="form-group">
          <label for="username">Логин:</label>
          <input 
            type="text" 
            id="username" 
            v-model="username" 
            required
            placeholder="Введите логин"
          >
        </div>
        
        <div class="form-group">
          <label for="password">Пароль:</label>
          <input 
            type="password" 
            id="password" 
            v-model="password" 
            required
            placeholder="Введите пароль"
          >
        </div>

        <div class="buttons">
          <button type="submit" class="login-btn">Войти</button>
          <button type="button" @click="register" class="register-btn">Регистрация</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'StartPage',
  setup() {
    const router = useRouter()
    const username = ref('')
    const password = ref('')

    const handleSubmit = async () => {
      try {
        const response = await axios.post('/api/auth/login', {
          username: username.value,
          password: password.value
        })
        if (response.status === 200) {
          router.push('/main')
        }
      } catch (error) {
        alert('Ошибка авторизации')
      }
    }

    const register = async () => {
      try {
        const response = await axios.post('/api/auth/register', {
          username: username.value,
          password: password.value
        })
        if (response.status === 200) {
          alert('Регистрация успешна')
        }
      } catch (error) {
        alert('Ошибка регистрации')
      }
    }

    return {
      username,
      password,
      handleSubmit,
      register
    }
  }
}
</script>

<style scoped>
.start-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  margin: 0;
}

.auth-container {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.header {
  margin-bottom: 2rem;
  text-align: center;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  text-align: left;
  font-weight: bold;
}

input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

button {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn {
  background-color: #4CAF50;
  color: white;
}

.register-btn {
  background-color: #2196F3;
  color: white;
}

button:hover {
  opacity: 0.9;
}
</style> 