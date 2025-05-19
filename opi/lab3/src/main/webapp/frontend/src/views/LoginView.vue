<template>
  <div class="login-view">
    <div class="header">
      <h1>Автор: Метель Леонард Валерьевич</h1>
      <p>Группа: P3212</p>
      <p>Вариант: 12012</p>
    </div>
    <div class="clock">
      <div v-for="hour in 12" :key="hour" class="hour-mark" :style="{ transform: `rotate(${(hour - 1) * 30}deg)` }">
        {{ hour === 1 ? 12 : hour - 1 }}
      </div>
      <div class="hand hour" :style="{ transform: `rotate(${hourRotation}deg)` }"></div>
      <div class="hand minute" :style="{ transform: `rotate(${minuteRotation}deg)` }"></div>
      <div class="hand second" :style="{ transform: `rotate(${secondRotation}deg)` }"></div>
      <div class="center"></div>
    </div>
    <form @submit.prevent="handleLogin" class="login-form">
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
          autocomplete="current-password"
        >
      </div>
      <div class="error" v-if="error">{{ error }}</div>
      <button type="submit" :disabled="loading" class="styled-link">
        {{ loading ? 'Вход...' : 'Войти' }}
      </button>
      <div class="register-link">
        <router-link to="/register" class="styled-link">Зарегистрироваться</router-link>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'LoginView',
  setup() {
    const router = useRouter()
    const username = ref('')
    const password = ref('')
    const error = ref('')
    const loading = ref(false)
    const hourRotation = ref(0)
    const minuteRotation = ref(0)
    const secondRotation = ref(0)

    const updateClock = () => {
      const now = new Date()
      const hours = now.getHours() % 12
      const minutes = now.getMinutes()
      const seconds = now.getSeconds()

      hourRotation.value = (hours * 30) + (minutes * 0.5)
      minuteRotation.value = (minutes * 6) + (seconds * 0.1)
      secondRotation.value = seconds * 6
    }

    let clockInterval

    onMounted(() => {
      updateClock()
      clockInterval = setInterval(updateClock, 1000)
    })

    onUnmounted(() => {
      if (clockInterval) clearInterval(clockInterval)
    })

    const handleLogin = async () => {
      try {
        loading.value = true
        error.value = ''
        
        const response = await axios.post('/api/auth/login', {
          username: username.value,
          password: password.value
        })
        
        if (response.status === 200) {
          localStorage.setItem('isAuthenticated', 'true')
          localStorage.setItem('username', username.value)
          router.push('/main')
        }
      } catch (err) {
        error.value = err.response?.data || 'Неверное имя пользователя или пароль'
      } finally {
        loading.value = false
      }
    }

    return {
      username,
      password,
      error,
      loading,
      handleLogin,
      hourRotation,
      minuteRotation,
      secondRotation
    }
  }
}
</script>

<style scoped>
.login-view {
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

.login-form {
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

h2 {
  color: white;
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: white;
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

button:disabled {
  background-color: rgba(97, 84, 164, 0.5);
  cursor: not-allowed;
}

.error {
  color: #ff6b6b;
  font-size: 14px;
  margin-top: 4px;
  text-align: center;
}

.register-link {
  text-align: center;
  margin-top: 10px;
}

.register-link a {
  color: white;
  text-decoration: none;
  font-weight: bold;
  transition: color 0.3s;
}

.register-link a:hover {
  color: #b490d1;
}

.header {
  background-color: rgba(11, 8, 8, 0.8);
  padding: 15px;
  text-align: center;
  border: 2px solid #6154a4d0;
  border-radius: 10px;
  margin: 20px;
}

.header h1, .header p {
  font-size: 24px;
  color: white;
  margin: 0;
}

.header p {
  font-size: 18px;
  margin-top: 10px;
}

.clock {
  position: relative;
  width: 300px;
  height: 300px;
  border: 10px solid #6154a4d0;
  border-radius: 50%;
  margin: 0 auto;
  margin-top: 80px;
  background-color: rgba(11, 8, 8, 0.8);
  box-shadow: 0 4px 8px rgba(97, 84, 164, 0.3);
}

.hour-mark {
  position: absolute;
  width: 20px;
  height: 20px;
  text-align: center;
  line-height: 20px;
  font-size: 18px;
  color: white;
  transform: none !important;
}

.hour-mark:nth-child(1) { top: 10px; left: 140px; }      /* 12 */
.hour-mark:nth-child(2) { top: 30px; right: 75px; }      /* 1 */
.hour-mark:nth-child(3) { top: 75px; right: 30px; }      /* 2 */
.hour-mark:nth-child(4) { top: 140px; right: 10px; }     /* 3 */
.hour-mark:nth-child(5) { bottom: 75px; right: 30px; }   /* 4 */
.hour-mark:nth-child(6) { bottom: 30px; right: 75px; }   /* 5 */
.hour-mark:nth-child(7) { bottom: 10px; left: 140px; }   /* 6 */
.hour-mark:nth-child(8) { bottom: 30px; left: 75px; }    /* 7 */
.hour-mark:nth-child(9) { bottom: 75px; left: 30px; }    /* 8 */
.hour-mark:nth-child(10) { top: 140px; left: 10px; }     /* 9 */
.hour-mark:nth-child(11) { top: 75px; left: 30px; }      /* 10 */
.hour-mark:nth-child(12) { top: 30px; left: 75px; }      /* 11 */

.hand {
  position: absolute;
  background: #6154a4d0;
  border-radius: 10px;
  transform-origin: bottom;
  bottom: 50%;
  left: 50%;
  transform: translateX(-50%);
}

.hour {
  width: 7px;
  height: 80px;
}

.minute {
  width: 4px;
  height: 125px;
}

.second {
  width: 2px;
  height: 125px;
  background: #9969bb;
}

.center {
  position: absolute;
  width: 12px;
  height: 12px;
  background: #6154a4d0;
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.styled-link {
  display: inline-block;
  padding: 10px 50px;
  background-color: #6154a4d0;
  color: white;
  text-decoration: none;
  border-radius: 5px;
  font-size: 16px;
  transition: background-color 0.3s, transform 0.3s;
  border: none;
  cursor: pointer;
}

.styled-link:hover {
  background-color: #9969bb;
  transform: scale(1.05);
}

.styled-link:active {
  transform: scale(0.95);
}
</style> 