from flask import Flask, jsonify, request # type: ignore
from flask_cors import CORS # type: ignore
import datetime
import time

# --- 데이터베이스 대신 인메모리 리스트 사용 (간단 데모) ---
event_log = []
# -----------------------------------------------------------

app = Flask(__name__)
# Nginx를 통해 오는 모든 /api/ 요청을 허용합니다.
CORS(app)


@app.route('/api/trigger/fall-detection', methods=['POST'])
def trigger_fall_detection():
    """AI 감지 시뮬레이션 후 인메모리 DB에 이벤트를 저장하는 API"""
    
    try:
        # 1. 이벤트 데이터 생성 (시간은 Python datetime 객체로)
        event_data = {
            'id': f"event_{int(time.time())}", # 고유 ID 생성
            'eventType': 'FALL_DETECTED (AI_SIM)',
            'timestamp': datetime.datetime.now(datetime.timezone.utc)
        }

        # 2. 인메모리 리스트의 맨 앞에 추가
        event_log.insert(0, event_data)
        
        print(f"🎉 API CALL SUCCESS: Saved new event: {event_data['eventType']}")
        
        return jsonify({"status": "SUCCESS", "message": "Event saved successfully"}), 200
    
    except Exception as e:
        print(f"❌ API CALL FAILURE (500 Error): {e}")
        return jsonify({"status": "ERROR", "message": str(e)}), 500


@app.route('/api/events', methods=['GET'])
def get_events():
    """인메모리 DB에 저장된 이벤트를 최신순으로 조회하는 API"""
    
    try:
        # Vue에서 처리하기 쉽도록 Timestamp를 문자열로 변환
        serializable_list = []
        for event in event_log:
            # datetime 객체를 ISO 문자열로 변환하여 JSON으로 보냅니다.
            serializable_list.append({
                'id': event['id'],
                'eventType': event['eventType'],
                'timestamp': event['timestamp'].isoformat() 
            })
        
        return jsonify(serializable_list), 200
        
    except Exception as e:
        print(f"❌ API CALL FAILURE (500 Error during query): {e}")
        return jsonify({"status": "ERROR", "message": str(e)}), 500


if __name__ == '__main__':
    print("\n--- Flask Server Starting ---\n")
    # Docker 환경에서 8080 포트로 실행
    app.run(host='0.0.0.0', port=8080)