<template>
  <div class="main-view">
    <div class="background"></div>
    <div class="content">
      <button @click="handleLogout" class="logoutButton">Выйти</button>
      <form @submit.prevent="handleSubmit" class="table">
        <div class="graph-section">
          <canvas ref="canvas" width="400" height="400" @click="handleCanvasClick"></canvas>
          <div class="input-controls">
            <div class="input-group">
              <label for="x">X:</label>
              <select id="x" v-model="x" required>
                <option v-for="value in [-4, -3.5, -3, -2.5, -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4]" 
                        :key="value" 
                        :value="value">
                  {{ value }}
                </option>
              </select>
            </div>

            <div class="input-group">
              <label for="y">Y:</label>
              <input 
                type="text" 
                id="y" 
                v-model.number="y"
                required
                @input="validateY"
                placeholder="-5 ... 5"
              >
              <div class="error" v-if="yError">{{ yError }}</div>
            </div>

            <div class="input-group">
              <label for="r">R:</label>
              <select id="r" v-model="r" required @change="redrawGraph">
                <option v-for="value in [1, 1.5, 2, 2.5, 3]" 
                        :key="value" 
                        :value="value">
                  {{ value }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <button type="submit" :disabled="loading || !!yError" class="checkButton">
          {{ loading ? 'Проверка...' : 'Проверить' }}
        </button>
        <button type="button" @click="clearResults" class="clearButton">Очистить</button>
      </form>

      <div class="result">
        <table>
          <thead>
            <tr>
              <th>X</th>
              <th>Y</th>
              <th>R</th>
              <th>Результат</th>
              <th>Время выполнения</th>
              <th>Время проверки</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="point in paginatedPoints" :key="point.id">
              <td>{{ point.x }}</td>
              <td>{{ point.y }}</td>
              <td>{{ point.r }}</td>
              <td>{{ point.result ? 'Попадание' : 'Промах' }}</td>
              <td>{{ point.executionTime }} мкс</td>
              <td>{{ formatDate(point) }}</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1" class="pagination-btn">←</button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="pagination-btn">→</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'MainView',
  setup() {
    const router = useRouter()
    const canvas = ref(null)
    const x = ref(0)
    const y = ref(0)
    const r = ref(1)
    const yError = ref('')
    const loading = ref(false)
    const points = ref([])
    const currentPage = ref(1)
    const itemsPerPage = 15

    const paginatedPoints = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage
      const end = start + itemsPerPage
      return points.value.slice(start, end)
    })

    const totalPages = computed(() => Math.ceil(points.value.length / itemsPerPage))

    const nextPage = () => {
      if (currentPage.value < totalPages.value) {
        currentPage.value++
      }
    }

    const prevPage = () => {
      if (currentPage.value > 1) {
        currentPage.value--
      }
    }

    const validateY = () => {
      if (y.value < -5 || y.value > 3) {
        yError.value = 'Y должен быть в диапазоне [-5, 3]'
        return false
      }
      yError.value = ''
      return true
    }

    const drawGraph = () => {
      if (!canvas.value) return
      
      const ctx = canvas.value.getContext('2d')
      const width = canvas.value.width
      const height = canvas.value.height
      const centerX = width / 2
      const centerY = height / 2
      const scale = (width / 3) / r.value
      
      // Очистка канваса
      ctx.clearRect(0, 0, width, height)
      
      // Установка стилей
      ctx.fillStyle = 'rgb(153, 106, 187)'
      ctx.strokeStyle = 'rgb(153, 106, 187)'
      ctx.lineWidth = 2
      ctx.font = '14px Arial'
      
      // Рисование фигур
      ctx.beginPath()
      // Прямоугольник
      ctx.fillRect(centerX, centerY - scale * r.value / 2, scale * r.value, scale * r.value / 2)
      
      // Треугольник
      ctx.beginPath()
      ctx.moveTo(centerX, centerY)
      ctx.lineTo(centerX - scale * r.value / 2, centerY)
      ctx.lineTo(centerX, centerY + scale * r.value)
      ctx.closePath()
      ctx.fill()
      
      // Четверть круга
      ctx.beginPath()
      ctx.moveTo(centerX, centerY)
      ctx.arc(centerX, centerY, scale * r.value, 0, Math.PI/2)
      ctx.closePath()
      ctx.fill()
      
      // Оси
      ctx.strokeStyle = 'black'
      ctx.beginPath()
      ctx.moveTo(0, centerY)
      ctx.lineTo(width, centerY)
      ctx.moveTo(centerX, 0)
      ctx.lineTo(centerX, height)
      ctx.stroke()
      
      // Стрелки на осях
      ctx.beginPath()
      ctx.moveTo(width - 10, centerY - 5)
      ctx.lineTo(width, centerY)
      ctx.lineTo(width - 10, centerY + 5)
      ctx.moveTo(centerX - 5, 10)
      ctx.lineTo(centerX, 0)
      ctx.lineTo(centerX + 5, 10)
      ctx.stroke()
      
      // Метки на осях
      ctx.fillStyle = 'black'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      
      // Метки R на оси X
      ctx.fillText(r.value.toString(), centerX + scale * r.value, centerY - 10)
      ctx.fillText((r.value/2).toString(), centerX + scale * r.value / 2, centerY - 10)
      ctx.fillText((-r.value).toString(), centerX - scale * r.value, centerY - 10)
      ctx.fillText((-r.value/2).toString(), centerX - scale * r.value / 2, centerY - 10)
      
      // Метки R на оси Y
      ctx.fillText(r.value.toString(), centerX + 20, centerY - scale * r.value)
      ctx.fillText((r.value/2).toString(), centerX + 20, centerY - scale * r.value / 2)
      ctx.fillText((-r.value).toString(), centerX + 20, centerY + scale * r.value)
      ctx.fillText((-r.value/2).toString(), centerX + 20, centerY + scale * r.value / 2)
      
      // Рисование точек
      points.value.forEach(point => {
        const pointX = centerX + point.x * scale
        const pointY = centerY - point.y * scale
        
        ctx.beginPath()
        ctx.arc(pointX, pointY, 4, 0, 2 * Math.PI)
        ctx.fillStyle = point.result ? 'green' : 'red'
        ctx.fill()
      })
    }

    const handleCanvasClick = (event) => {
      const rect = canvas.value.getBoundingClientRect()
      const scaleX = canvas.value.width / rect.width
      const scaleY = canvas.value.height / rect.height
      
      const mouseX = (event.clientX - rect.left) * scaleX
      const mouseY = (event.clientY - rect.top) * scaleY
      
      const centerX = canvas.value.width / 2
      const centerY = canvas.value.height / 2
      const scale = (canvas.value.width / 3) / r.value
      
      const xCoord = (mouseX - centerX) / scale
      const yCoord = (centerY - mouseY) / scale
      
      x.value = xCoord.toFixed(2)
      y.value = yCoord.toFixed(2)
      
      handleSubmit()
    }

    const handleSubmit = async () => {
      if (!validateY()) return
      
      loading.value = true
      try {
        const response = await axios.post('/api/points', {
          x: parseFloat(x.value),
          y: parseFloat(y.value),
          r: parseFloat(r.value)
        })
        points.value.unshift(response.data)
        drawGraph()
      } catch (error) {
        if (error.response?.status === 401) {
          router.push('/login')
        } else {
          console.error('Ошибка при отправке точки:', error)
        }
      } finally {
        loading.value = false
      }
    }

    const loadPoints = async () => {
      try {
        const response = await axios.get('/api/points')
        points.value = response.data
        drawGraph()
      } catch (error) {
        if (error.response?.status === 401) {
          router.push('/login')
        } else {
          console.error('Ошибка при загрузке точек:', error)
        }
      }
    }

    const clearResults = async () => {
      try {
        await axios.delete('/api/points')
        points.value = []
        drawGraph()
      } catch (err) {
        console.error('Ошибка при очистке результатов:', err)
      }
    }

    const handleLogout = async () => {
      try {
        await axios.post('/api/auth/logout')
        router.push('/')
      } catch (err) {
        console.error('Ошибка при выходе:', err)
      }
    }

    const formatDate = (point) => {
      if (!point || !point.checkTime) return '';
      try {
        const [year, month, day, hour, minute, second] = point.checkTime;
        const date = new Date(year, month - 1, day, hour, minute, second);
        return new Intl.DateTimeFormat('ru-RU', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hour12: false
        }).format(date);
      } catch (e) {
        console.error('Error formatting date:', e);
        return 'Ошибка даты';
      }
    }

    const checkPointWithNewR = (point) => {
      const x = point.x;
      const y = point.y;
      const currentR = r.value;
      
      // Прямоугольник в верхней правой части (0.5 x R)
      const inRectangle = x >= 0 && x <= currentR && y >= 0 && y <= currentR/2;
      
      // Треугольник в левой нижней части
      // Вершины: (0,0), (-0.5r,0), (0,-r)
      // Уравнение гипотенузы: y = 2x + r
      const inTriangle = x <= 0 && x >= -0.5 * currentR && y <= 0 && y >= -currentR && y <= 2 * x + currentR;
      
      // Четверть круга в правой нижней части
      const inCircle = x >= 0 && y <= 0 && (x * x + y * y) <= currentR * currentR;
      
      return inRectangle || inTriangle || inCircle;
    }

    watch(r, () => {
      // Перепроверяем все точки с новым радиусом
      points.value = points.value.map(point => ({
        ...point,
        result: checkPointWithNewR(point)
      }));
      drawGraph();
    }, { immediate: true })

    onMounted(() => {
      canvas.value.width = 400
      canvas.value.height = 400
      loadPoints()
    })

    return {
      canvas,
      x,
      y,
      r,
      yError,
      loading,
      points,
      handleSubmit,
      handleCanvasClick,
      clearResults,
      handleLogout,
      formatDate,
      validateY,
      paginatedPoints,
      currentPage,
      totalPages,
      nextPage,
      prevPage
    }
  }
}
</script>

<style scoped>
.main-view {
  min-height: 100vh;
  position: relative;
  color: white;
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

.content {
  position: relative;
  z-index: 1;
  height: 100vh;
}

.graph-section {
  position: absolute;
  top: 23%;
  left: 1%;
  width: 48%;
  height: 66%;
  background: white;
  border-radius: 5px;
  z-index: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
}

canvas {
  width: 100%;
  height: calc(100% - 60px);
  margin-bottom: 10px;
}

.input-controls {
  position: relative;
  display: flex;
  justify-content: center;
  gap: 20px;
  background-color: white;
  padding: 10px;
  border-top: 1px solid #6154a4d0;
  margin-top: auto;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.input-group label {
  color: #6154a4d0;
  font-weight: bold;
  margin: 0;
  min-width: 20px;
}

.input-group select,
.input-group input {
  padding: 5px;
  border: 1px solid #6154a4d0;
  border-radius: 4px;
  background-color: white;
  color: #6154a4d0;
  width: 80px;
}

.result {
  position: absolute;
  top: 23%;
  left: 81%;
  width: 25%;
  max-height: 400px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
}

.result thead {
  position: sticky;
  top: 0;
  background: rgba(153, 106, 187, 0.9);
  color: white;
  white-space: nowrap;
}

.result th,
.result td {
  padding: 3px;
  text-align: center;
  border: 1px solid rgba(153, 106, 187, 0.3);
  white-space: nowrap;
}

.result tbody tr:hover {
  background: rgba(153, 106, 187, 0.1);
}

.checkButton {
  position: absolute;
  left: 65%;
  top: 66%;
  background-color: #6154a4d0;
  border: none;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.clearButton {
  position: absolute;
  left: 65%;
  top: 83%;
  background-color: #6154a4d0;
  border: none;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.error {
  position: absolute;
  bottom: -20px;
  left: 0;
  color: red;
  font-size: 12px;
}

.logoutButton {
  position: absolute;
  top: 18%;
  right: 30%;
  background-color: #6154a4d0;
  border: none;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logoutButton:hover {
  background-color: #9969bb;
}

/* Десктопный режим (>1035px) */
@media screen and (min-width: 1036px) {
  .graph-section {
    width: 48%;
  }
  
  .result {
    width: 28%;
  }
}

/* Планшетный режим (798-1035px) */
@media screen and (min-width: 798px) and (max-width: 1035px) {
  .graph-section {
    width: 60%;
    left: 5%;
  }
  
  .result {
    width: 30%;
    right: 5%;
  }
  
  .checkButton {
    left: 70%;
  }
  
  .clearButton {
    left: 70%;
  }
  
  .logoutButton {
    right: 20%;
  }
}

/* Мобильный режим (<798px) */
@media screen and (max-width: 797px) {
  .content {
    display: flex;
    flex-direction: column;
    padding: 10px;
  }
  
  .graph-section {
    position: relative;
    width: 100%;
    left: 0;
    top: 0;
    margin-bottom: 20px;
    height: auto;
  }
  
  canvas {
    height: 300px;
  }
  
  .input-controls {
    flex-direction: column;
    gap: 15px;
  }
  
  .result {
    position: relative;
    width: 100%;
    right: 0;
    top: 0;
    margin-top: 20px;
  }
  
  .checkButton,
  .clearButton {
    position: relative;
    left: 0;
    width: 100%;
    margin: 10px 0;
  }
  
  .logoutButton {
    position: relative;
    top: 10px;
    right: 0;
    width: 100%;
    margin-bottom: 20px;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 5px;
  background: rgba(153, 106, 187, 0.9);
  margin-top: auto;
}

.pagination-btn {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  padding: 0 8px;
  transition: opacity 0.3s;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: white;
  margin: 0 10px;
}
</style> 