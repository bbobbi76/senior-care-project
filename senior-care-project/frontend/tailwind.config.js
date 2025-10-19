//Tailwind CSS에게 어떤 파일들을 감시할지 알려주는 설정입니다.

/** @type {import('tailwindcss').Config} */
export default {
    content: [
      "./index.html",
      "./src/**/*.{vue,js,ts,jsx,tsx}", // .vue 파일 안의 클래스들을 감지
    ],
    theme: {
      extend: {},
    },
    plugins: [],
  }