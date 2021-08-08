#!/usr/bin/env bash
docker rm $(docker stop $(docker ps -a -q --filter="name=selenoid-ui" --format="{{.ID}}"))
docker-compose down
docker-compose up -d
sleep 5
# узнаём ip адреса контейнеров
SELENOID_IP=`docker inspect selenoid -f {{.NetworkSettings.IPAddress}}`
JENKINS_IP=`docker inspect jenkins -f {{.NetworkSettings.IPAddress}}`
NGROK_IP=`docker inspect ngrok -f {{.NetworkSettings.IPAddress}}`

# Добавим запись в /etc/hosts, чтоб localhost мог резолвить контейнеры по имени
[[ "$(grep -c $SELENOID_IP /etc/hosts)" == 0 ]] && echo "$SELENOID_IP      selenoid" >> /etc/hosts
[[ "$(grep -c $JENKINS_IP /etc/hosts)" == 0 ]] && echo "$JENKINS_IP      jenkins" >> /etc/hosts
[[ "$(grep -c $NGROK_IP /etc/hosts)" == 0 ]] && echo "$NGROK_IP      ngrok" >> /etc/hosts

echo "Creating selenoid-ui    ..."
docker run -d --name selenoid-ui -p 8080:8080 aerokube/selenoid-ui --selenoid-uri http://${SELENOID_IP}:4444
WEBHOOK=`curl -s ngrok:4551/api/tunnels | jq -r '.tunnels[0].public_url'`

printf "Please add WebHOOK into Jenkins and GitHub:\n $WEBHOOK  <--- Login to Jenkins with credentials: test/test"
echo ""
#научим дженкинс резолвить хост selenoid
docker exec jenkins /bin/bash -c "echo \"$SELENOID_IP      selenoid\" >> /etc/hosts"
#echo "jenkins /etc/hosts:"
#docker exec jenkins /bin/bash -c "cat /etc/hosts"
