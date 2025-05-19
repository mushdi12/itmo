<template>
  <div class="register-view">
    <h2>Регистрация</h2>
    <form @submit.prevent="handleRegister" class="register-form">
      <div class="form-group">
        <label for="username">Имя пользователя:</label>
        <input 
          type="text" 
          id="username" 
          v-model="username" 
          required
          autocomplete="username"
        >
      </div>
      <div class="form-group">
        <label for="password">Пароль:</label>
        <input 
          type="password" 
          id="password" 
          v-model="password" 
          required
          autocomplete="new-password"
        >
      </div>
      <div class="form-group">
        <label for="confirmPassword">Подтверждение пароля:</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="confirmPassword" 
          required
          autocomplete="new-password"
        >
      </div>
      <div class="error" v-if="error">{{ error }}</div>
      <button type="submit" :disabled="loading || !isValid">
        {{ loading ? 'Регистрация...' : 'Зарегистрироваться' }}
      </button>
      <div class="login-link">
        <router-link to="/">Уже есть аккаунт? Войти</router-link>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const username = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    const error = ref('')
    const loading = ref(false)

    const isValid = computed(() => {
      return username.value.length >= 3 && 
             password.value.length >= 6 && 
             password.value === confirmPassword.value
    })

    const handleRegister = async () => {
      if (!isValid.value) {
        error.value = 'Пожалуйста, проверьте правильность заполнения полей'
        return
      }

      try {
        loading.value = true
        error.value = ''
        
        await axios.post('/api/auth/register', {
          username: username.value,
          password: password.value
        })
        
        router.push('/')
      } catch (err) {
        error.value = err.response?.data || 'Ошибка при регистрации'
      } finally {
        loading.value = false
      }
    }

    return {
      username,
      password,
      confirmPassword,
      error,
      loading,
      isValid,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-view {
  min-height: 100vh;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  background: black;
}

.background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('@/assets/images/TV.png');
  background-size: cover;
  background-position: center;
  z-index: 0;
}

.noise {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('@/assets/images/Noise.gif');
  background-size: cover;
  opacity: 0.3;
  pointer-events: none;
  mix-blend-mode: screen;
  z-index: 10;
}

.register-form {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: rgba(11, 8, 8, 0.8);
  border: 2px solid #6154a4d0;
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #6154a4d0;
  font-weight: bold;
  font-size: 16px;
}

.form-group input {
  padding: 12px;
  border: 1px solid #6154a4d0;
  border-radius: 5px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 16px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #9969bb;
  box-shadow: 0 0 5px rgba(153, 106, 187, 0.5);
}

button {
  padding: 12px;
  background-color: #6154a4d0;
  border: none;
  border-radius: 5px;
  color: white;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 10px;
}

button:hover {
  background-color: #9969bb;
}

.error {
  color: #ff6b6b;
  font-size: 14px;
  margin-top: 4px;
}

.login-link {
  text-align: center;
  margin-top: 10px;
}

.login-link a {
  color: #9969bb;
  text-decoration: none;
  font-weight: bold;
  transition: color 0.3s;
}

.login-link a:hover {
  color: #b490d1;
}
</style> 