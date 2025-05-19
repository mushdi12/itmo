<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
body {
    margin: 0;
    padding: 0;
    height: 100vh;
    position: relative;
    background-color: black;
}

.background {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('../public/images/TV.png') center/cover no-repeat;
    z-index: 1;
}

.background::after {
    content: '';
    position: absolute;
    top: 23%;
    left: 7%;
    width: 48%;
    height: 66%;
    background: url('../public/images/Noise.gif');
    z-index: 2;
}

input[type="text"] {
    background-color: #f1f1f1;
    color: #6154a4d0;
}

input[type="text"]::placeholder {
    color: #6154a4d0;
}

button {
    padding: 10px 20px;
    background-color: #532293;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

button:hover {
    background-color: #5145a0;
}

/* Десктопный режим (>1035px) */
@media screen and (min-width: 1036px) {
    .content {
        max-width: 1200px;
        margin: 0 auto;
    }
}

/* Планшетный режим (798-1035px) */
@media screen and (min-width: 798px) and (max-width: 1035px) {
    .content {
        max-width: 90%;
        margin: 0 auto;
    }
    
    .graph-section {
        width: 60%;
    }
    
    .result {
        width: 35%;
    }
}

/* Мобильный режим (<798px) */
@media screen and (max-width: 797px) {
    .content {
        max-width: 100%;
        padding: 10px;
    }
    
    .graph-section {
        position: relative;
        width: 100%;
        left: 0;
        top: 0;
        margin-bottom: 20px;
    }
    
    .result {
        position: relative;
        width: 100%;
        right: 0;
        top: 0;
        overflow-x: auto;
    }
    
    .input-controls {
        flex-direction: column;
        align-items: center;
    }
    
    .checkButton,
    .clearButton {
        position: relative;
        left: 0;
        top: 0;
        width: 100%;
        margin: 10px 0;
    }
    
    .logoutButton {
        position: relative;
        top: 0;
        right: 0;
        width: 100%;
        margin: 10px 0;
    }
}
</style> 