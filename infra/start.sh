#!/usr/bin/env bash
docker rm -vf $(docker ps -a -q)
#docker rm $(docker stop $(docker ps -a -q --filter="name=selenoid-ui" --format="{{.ID}}"))
docker-compose down
docker-compose up -d
sleep 5
# узнаём ip адреса контейнеров
SELENOID_IP=`docker inspect selenoid -f {{.NetworkSettings.IPAddress}}`
JENKINS_IP=`docker inspect jenkins -f {{.NetworkSettings.IPAddress}}`
NGROK_IP=`docker inspect ngrok -f {{.NetworkSettings.IPAddress}}`


# Добавим запись в /etc/hosts, чтоб localhost мог резолвиdocker ть контейнеры по имени
sed -i '/ngrok/d' /etc/hosts
sed -i '/selenoid/d' /etc/hosts
sed -i '/jenkins/d' /etc/hosts
[[ "$(grep -c $SELENOID_IP /etc/hosts)" == 0 ]] && echo "$SELENOID_IP      selenoid" >> /etc/hosts
[[ "$(grep -c $JENKINS_IP /etc/hosts)" == 0 ]] && echo "$JENKINS_IP      jenkins" >> /etc/hosts
[[ "$(grep -c $NGROK_IP /etc/hosts)" == 0 ]] && echo "$NGROK_IP      ngrok" >> /etc/hosts

# не забываем подтянуть необходимые вспомогательные контейнеры браузеров на локаль
# во избежание ошибки вида:
# create container: Error response from daemon: No such image: selenoid/vnc_chrome:86.0 (WARNING: The server did not provide any stacktrace information)
docker pull selenoid/video-recorder:latest-release
docker pull selenoid/vnc_chrome:86.0

echo "Creating selenoid-ui    ..."
docker run -d --name selenoid-ui -p 8080:8080 aerokube/selenoid-ui --selenoid-uri http://${SELENOID_IP}:4444
WEBHOOK=`curl -s ngrok:4551/api/tunnels | jq -r '.tunnels[0].public_url'`

printf "Please add WebHOOK into Jenkins and GitHub:\n $WEBHOOK  <--- Login to Jenkins with credentials: test/test"
echo ""
#научим дженкинс резолвить хост selenoid
docker exec jenkins /bin/bash -c "echo \"$SELENOID_IP      selenoid\" >> /etc/hosts"
#echo "jenkins /etc/hosts:"
#docker exec jenkins /bin/bash -c "cat /etc/hosts"
