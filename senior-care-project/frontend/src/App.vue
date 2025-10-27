<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

// --- 상태 변수 ---
const eventLog = ref([]); // Firestore에서 가져온 이벤트 기록
const videoPlayer = ref(null); // <video> 태그 요소를 참조
const eventTriggered = ref(false); // 이벤트가 이미 발생했는지 (중복 방지)
const simulationStatus = ref('대기 중...'); // 현재 상태 표시
const API_URL = '/api'; // Nginx가 백엔드로 넘겨줄 API 경로

// (시뮬레이션) 영상의 5초 시점에 이벤트를 발생시킴
const TRIGGER_TIME_SEC = 5.0;

// --- 함수 ---

// 1. (백엔드) Firestore에서 이벤트 기록 불러오기
const fetchEvents = async () => {
    try {
        const response = await axios.get(`${API_URL}/events`);
        eventLog.value = response.data;
    } catch (error) {
        console.error('이벤트 기록 조회 실패:', error);
    }
};

// 2. (백엔드) "낙상 감지" API 호출 (Gemini 시뮬레이션)
const triggerFallDetection = async () => {
    // 중복 호출 방지
    if (eventTriggered.value) return;
    eventTriggered.value = true; // 플래그 설정

    // 낙상 감지 및 카톡 알림 전송 시작 메시지로 업데이트
    simulationStatus.value = `🚨 ${TRIGGER_TIME_SEC}초경 감지! Firestore 저장 및 카톡 알림 전송 중...`;
    console.log('이벤트 감지! 백엔드 API 호출...');

    try {
        await axios.post(`${API_URL}/trigger/fall-detection`);
        // 성공 메시지에 카톡 알림 전송 완료를 명시적으로 추가
        simulationStatus.value = `✅ Firestore 저장 및 카톡 알림 전송 완료!`;
        // 저장 성공 시, 즉시 목록 새로고침
        fetchEvents();
    } catch (error)
    {
        console.error('API 호출 실패:', error);
        simulationStatus.value = `❌ API 호출 실패`;
    }
};

// 3. (영상) 영상 재생 시간을 실시간으로 감지하는 함수
const handleTimeUpdate = () => {
    const currentTime = videoPlayer.value.currentTime;

    // (핵심!) 설정한 시간(5초)을 넘었고, 아직 이벤트가 발생하지 않았다면
    if (currentTime >= TRIGGER_TIME_SEC && !eventTriggered.value) {
        triggerFallDetection();
    }
};

// 4. (UI) 시뮬레이션 초기화
const resetSimulation = () => {
    if (videoPlayer.value) {
        videoPlayer.value.currentTime = 0; // 영상 0초로
        videoPlayer.value.play(); // 다시 재생
    }
    eventTriggered.value = false; // 이벤트 플래그 초기화
    simulationStatus.value = '시뮬레이션 재시작...';
};

// 5. (날짜) 날짜 형식 변환
// (최종 해결!) Instant 객체를 Firestore가 저장한 Map 형식으로 처리
const formatTimestamp = (eventTimestamp) => {
    if (!eventTimestamp) return '';
    // 만약 ISO 문자열이면 (일반적인 경우)
    if (typeof eventTimestamp === 'string') {
        return new Date(eventTimestamp).toLocaleString('ko-KR', { hour12: false });
    }
    // Firestore/Java Instant 객체 형식 ({epochSecond: N, nano: N}) 인 경우
    if (eventTimestamp.epochSecond) {
        // 초(seconds)를 밀리초(milliseconds)로 변환하여 Date 객체 생성
        const milliseconds = eventTimestamp.epochSecond * 1000;
        return new Date(milliseconds).toLocaleString('ko-KR', { hour12: false });
    }
    return '날짜 오류';
};

// --- 라이프사이클 훅 ---

// 1초마다 목록을 자동으로 새로고침 (실시간 알림처럼 보이게 함)
let pollInterval;
onMounted(() => {
    fetchEvents(); // 페이지 로드 시 1회 실행
    // 1초(1000ms)마다 fetchEvents 함수를 반복 실행
    pollInterval = setInterval(fetchEvents, 1000);
});

// 페이지 벗어날 때 자동 새로고침 중지 (메모리 누수 방지)
onUnmounted(() => {
    clearInterval(pollInterval);
});
</script>

<template>
    <div class="bg-gray-100 min-h-screen flex items-center justify-center p-4">
        <div class="w-full max-w-4xl bg-white rounded-lg shadow-xl p-8 flex gap-8">

            <!-- 1. 왼쪽: 영상 모니터링 영역 -->
            <div class="flex-1">
                <h2 class="text-2xl font-bold text-gray-800 mb-4">🏠 실시간 홈캠 (시뮬레이션)</h2>
                <video
                    ref="videoPlayer"
                    src="/sample-video.mp4"
                    autoplay
                    muted
                    loop
                    @timeupdate="handleTimeUpdate"
                    class="w-full rounded-lg bg-black shadow-inner"
                >
                    영상을 로드할 수 없습니다. (public/sample-video.mp4 확인)
                </video>
                <button
                    @click="resetSimulation"
                    class="w-full mt-4 bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg transition duration-300">
                    🔄 시뮬레이션 다시보기
                </button>
                <p class="mt-4 text-center font-semibold text-blue-700 h-6">
                    {{ simulationStatus }}
                </p>
            </div>

            <!-- 2. 오른쪽: 이벤트 기록 영역 -->
            <div class="flex-1 max-w-md">
                <h2 class="text-2xl font-bold text-gray-700 mb-4">🚨 이벤트 기록 (Firestore)</h2>
                <!-- (오류 수정!) vtr-if -> v-if -->
                <div v-if="eventLog.length === 0" class="text-center text-gray-500 p-4 bg-gray-50 rounded-lg">
                    (자동 새로고침 중... 이벤트 대기 중)
                </div>
                <ul v-else class="max-h-96 overflow-y-auto space-y-3">
                    <li
                        v-for="(event, index) in eventLog"
                        :key="event.id"
                        class="p-4 bg-gray-50 rounded-lg shadow-sm"
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
