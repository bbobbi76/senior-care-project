//Vue.js를 시작하고 `App.vue`를 `index.html`에 연결하는 시작점입니다.

import { createApp } from 'vue'
import './style.css' // Tailwind CSS 스타일을 전역으로 불러옵니다.
import App from './App.vue'

createApp(App).mount('#app')