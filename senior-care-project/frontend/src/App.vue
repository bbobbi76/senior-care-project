<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

// --- 상태 변수 ---
const eventLog = ref([]); // 백엔드 API에서 가져온 이벤트 기록
const simulationStatus = ref('시스템 준비 완료.'); // 현재 상태 표시
const API_URL = '/api'; // Nginx가 Flask로 보낼 API 경로

// --- 함수 ---

// 1. (백엔드) 이벤트 기록 불러오기
const fetchEvents = async () => {
    try {
        const response = await axios.get(`${API_URL}/events`);
        eventLog.value = response.data;
    } catch (error) {
        console.error('이벤트 기록 조회 실패:', error, error.response);
    }
};

// 2. (백엔드) "낙상 감지" API 호출 (AI 시뮬레이션)
const triggerFallDetection = async () => {
    simulationStatus.value = `🚨 AI 감지! 기록 저장 중...`;

    try {
        await axios.post(`${API_URL}/trigger/fall-detection`);
        simulationStatus.value = `✅ 낙상 감지 완료! (알림 기록됨)`;
        fetchEvents(); // 목록 새로고침
    } catch (error)
    {
        simulationStatus.value = `❌ API 호출 실패. (500/502 에러). 백엔드 서버 확인.`;
    }
};

// 5. 날짜 형식 변환 (ISO 문자열 처리)
const formatTimestamp = (isoString) => {
    if (!isoString) return '시간 정보 없음';
    
    // Python Flask 서버가 ISO 문자열로 보내므로, Date 객체로 변환합니다.
    try {
        return new Date(isoString).toLocaleString('ko-KR', { hour12: false });
    } catch (e) {
        return '날짜 해석 오류';
    }
};

// --- 라이프사이클 훅 ---
let pollInterval;
onMounted(() => {
    fetchEvents(); 
    // 1초마다 목록 자동 새로고침
    pollInterval = setInterval(fetchEvents, 1000); 
});

onUnmounted(() => {
    clearInterval(pollInterval);
});
</script>

<template>
    <div class="bg-gray-100 min-h-screen flex items-center justify-center p-4">
        <div class="w-full max-w-lg bg-white rounded-lg shadow-xl p-8">

            <!-- 1. AI 감지 테스트 영역 -->
            <div>
                <h2 class="text-2xl font-bold text-gray-800 mb-4 text-center">🏠 AI 홈캠 시뮬레이션 (수동)</h2>
                
                <button
                    @click="triggerFallDetection"
                    class="w-full mt-4 bg-red-600 hover:bg-red-700 text-white font-bold py-3 rounded-lg text-lg transition duration-300 shadow-md">
                    🚨 (테스트) AI 낙상 이벤트 발생시키기
                </button>
                
                <p class="mt-4 text-center font-semibold text-blue-700 h-6">
                    {{ simulationStatus }}
                </p>
            </div>

            <!-- 2. 이벤트 기록 영역 -->
            <div class="mt-10 border-t pt-6">
                <h2 class="text-2xl font-extrabold text-red-600 mb-4">🚨 AI 감지 및 알림 기록</h2>
                
                <div v-if="eventLog.length === 0" class="text-center text-gray-500 p-4 bg-gray-50 rounded-lg">
                    (자동 새로고침 중... 이벤트 대기 중)
                </div>
                <ul v-else class="bg-gray-100 p-4 rounded-lg shadow-inner max-h-96 overflow-y-auto space-y-3 border border-gray-300">
                    <li
                        v-for="(event, index) in eventLog"
                        :key="event.id"
                        class="p-4 rounded-lg shadow-sm"
                        :class="{ 'animate-pulse bg-red-100 border-2 border-red-500': index === 0 }"
                    >
                        <div class="flex justify-between items-center">
                            <span class="font-bold text-lg text-red-600">🚨 {{ event.eventType }}</span>
                            <span v-if="index === 0" class="text-sm font-bold text-red-600">NEW!</span>
                        </div>
                        <span class="text-sm text-gray-700">{{ formatTimestamp(event.timestamp) }}</span>
                    </li>
                </ul>
            </div>

        </div>
    </div>
</template>