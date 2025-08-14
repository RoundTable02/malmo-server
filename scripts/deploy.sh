#!/bin/bash

# Nginx 설정 파일 경로
NGINX_UPSTREAM_CONF="/etc/nginx/conf.d/upstream.conf"

# 현재 활성화된 서비스(blue 또는 green) 찾기
CURRENT_COLOR=$(grep -oP '(?<=proxy_pass http://127.0.0.1:808)\d' $NGINX_UPSTREAM_CONF | sed 's/1/blue/' | sed 's/2/green/')

echo "Current active service: $CURRENT_COLOR"

# 유휴 서비스 및 포트 설정
if [ "$CURRENT_COLOR" == "blue" ]; then
  IDLE_COLOR="green"
  IDLE_PORT=8082
else
  IDLE_COLOR="blue"
  IDLE_PORT=8081
fi

echo "Deploying to idle service: $IDLE_COLOR on port $IDLE_PORT"

# 1. 유휴 서비스의 최신 이미지를 pull
docker-compose pull malmo-$IDLE_COLOR

# 2. 유휴 서비스 컨테이너 시작 (의존성 서비스는 재시작하지 않음)
docker-compose up -d --no-deps malmo-$IDLE_COLOR

# 3. Health check
echo "Waiting for $IDLE_COLOR service to be up..."
sleep 30 # 컨테이너가 완전히 시작될 때까지 잠시 대기

for i in {1..10}; do
  # Spring Boot Actuator의 health endpoint를 사용
  HEALTH_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:$IDLE_PORT/actuator/health)
  if [ "$HEALTH_STATUS" -eq 200 ]; then
    echo "Health check successful for $IDLE_COLOR."
    
    # 4. Nginx 트래픽 전환
    echo "Switching Nginx traffic to $IDLE_COLOR..."
    echo "set \$service_url http://127.0.0.1:$IDLE_PORT;" | sudo tee $NGINX_UPSTREAM_CONF
    sudo systemctl reload nginx
    echo "Traffic switched successfully."

    # 5. 이전 서비스 중지
    echo "Stopping old service: $CURRENT_COLOR..."
    docker-compose stop malmo-$CURRENT_COLOR
    echo "Old service stopped."
    
    exit 0
  fi
  echo "Health check failed (status: $HEALTH_STATUS). Retrying in 10 seconds... ($i/10)"
  sleep 10
done

echo "Deployment failed: $IDLE_COLOR service did not become healthy in time."
exit 1
