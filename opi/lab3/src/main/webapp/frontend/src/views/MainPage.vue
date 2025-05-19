<template>
  <div class="main-page">
    <div class="background"></div>
    <div class="content">
      <div class="graph-container">
        <canvas ref="graphCanvas" width="400" height="400" @click="handleCanvasClick"></canvas>
      </div>

      <div class="form-container">
        <form @submit.prevent="checkPoint" class="point-form">
          <div class="input-group">
            <label for="xInput">X:</label>
            <input 
              type="number" 
              id="xInput" 
              v-model.number="x" 
              step="0.5"
              min="-4"
              max="4"
              required
            >
          </div>

          <div class="input-group">
            <label for="yInput">Y:</label>
            <input 
              type="number" 
              id="yInput" 
              v-model.number="y"
              step="0.1"
              min="-5"
              max="5"
              required
            >
          </div>

          <div class="input-group">
            <label for="rInput">R:</label>
            <input 
              type="number" 
              id="rInput" 
              v-model.number="r"
              step="0.5"
              min="1"
              max="3"
              required
              @change="redrawGraph"
            >
          </div>

          <div class="buttons">
            <button type="submit" class="check-btn">Проверить</button>
            <button type="button" @click="clearResults" class="clear-btn">Очистить</button>
          </div>
        </form>
      </div>

      <div class="results-table">
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
            <tr v-for="point in points" :key="point.id">
              <td>{{ point.x.toFixed(2) }}</td>
              <td>{{ point.y.toFixed(2) }}</td>
              <td>{{ point.r.toFixed(2) }}</td>
              <td>{{ point.result ? 'Попадание' : 'Промах' }}</td>
              <td>{{ point.executionTime }} мс</td>
              <td>{{ formatDateTime(point.checkTime) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'

export default {
  name: 'MainPage',
  setup() {
    const graphCanvas = ref(null)
    const x = ref(0)
    const y = ref(0)
    const r = ref(1)
    const points = ref([])

    const drawGraph = () => {
      const canvas = graphCanvas.value
      const ctx = canvas.getContext('2d')
      const width = canvas.width
      const height = canvas.height
      const centerX = width / 2
      const centerY = height / 2
      const scale = width / 3 / r.value

      // Очистка канваса
      ctx.clearRect(0, 0, width, height)

      // Рисуем оси
      ctx.beginPath()
      ctx.moveTo(0, centerY)
      ctx.lineTo(width, centerY)
      ctx.moveTo(centerX, 0)
      ctx.lineTo(centerX, height)
      ctx.strokeStyle = 'black'
      ctx.stroke()

      // Рисуем области
      ctx.fillStyle = 'rgba(0, 0, 255, 0.3)'
      
      // Прямоугольник
      ctx.fillRect(centerX, centerY, scale * r.value / 2, scale * r.value)
      
      // Треугольник
      ctx.beginPath()
      ctx.moveTo(centerX, centerY)
      ctx.lineTo(centerX - scale * r.value / 2, centerY)
      ctx.lineTo(centerX, centerY + scale * r.value / 2)
      ctx.closePath()
      ctx.fill()
      
      // Четверть круга
      ctx.beginPath()
      ctx.arc(centerX, centerY, scale * r.value / 2, 0, Math.PI / 2, false)
      ctx.lineTo(centerX, centerY)
      ctx.closePath()
      ctx.fill()

      // Рисуем точки
      points.value.forEach(point => {
        const pointX = centerX + point.x * scale
        const pointY = centerY - point.y * scale
        ctx.beginPath()
        ctx.arc(pointX, pointY, 3, 0, 2 * Math.PI)
        ctx.fillStyle = point.result ? 'green' : 'red'
        ctx.fill()
      })
    }

    const handleCanvasClick = (event) => {
      const canvas = graphCanvas.value
      const rect = canvas.getBoundingClientRect()
      const scale = canvas.width / 3 / r.value
      const centerX = canvas.width / 2
      const centerY = canvas.height / 2

      const clickX = event.clientX - rect.left
      const clickY = event.clientY - rect.top

      x.value = ((clickX - centerX) / scale).toFixed(2)
      y.value = ((centerY - clickY) / scale).toFixed(2)

      checkPoint()
    }

    const checkPoint = async () => {
      try {
        const response = await axios.post('/api/points/check', {
          x: x.value,
          y: y.value,
          r: r.value
        })
        points.value.unshift(response.data)
        drawGraph()
      } catch (error) {
        alert('Ошибка при проверке точки')
      }
    }

    const clearResults = async () => {
      try {
        await axios.delete('/api/points')
        points.value = []
        drawGraph()
      } catch (error) {
        alert('Ошибка при очистке результатов')
      }
    }

    const loadPoints = async () => {
      try {
        const response = await axios.get('/api/points')
        points.value = response.data
        drawGraph()
      } catch (error) {
        alert('Ошибка при загрузке точек')
      }
    }

    const formatDateTime = (dateTime) => {
      return new Date(dateTime).toLocaleString()
    }

    onMounted(() => {
      loadPoints()
    })

    watch(r, () => {
      drawGraph()
    })

    return {
      graphCanvas,
      x,
      y,
      r,
      points,
      checkPoint,
      clearResults,
      handleCanvasClick,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.main-page {
  padding: 2rem;
}

.content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  max-width: 1200px;
  margin: 0 auto;
}

.graph-container {
  margin-bottom: 2rem;
}

.form-container {
  max-width: 400px;
  margin: 0 auto 2rem;
}

.point-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.input-group label {
  width: 30px;
  text-align: right;
}

.input-group input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.buttons {
  display: flex;
  gap: 1rem;
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

.check-btn {
  background-color: #4CAF50;
  color: white;
}

.clear-btn {
  background-color: #f44336;
  color: white;
}

button:hover {
  opacity: 0.9;
}

.results-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 2rem;
}

th, td {
  padding: 0.75rem;
  text-align: center;
  border: 1px solid #ddd;
}

th {
  background-color: #f5f5f5;
  font-weight: bold;
}

tr:nth-child(even) {
  background-color: #f9f9f9;
}

tr:hover {
  background-color: #f5f5f5;
}
</style> 