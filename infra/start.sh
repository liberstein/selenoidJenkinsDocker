#!/usr/bin/env bash
docker rm $(docker stop $(docker ps -a -q --filter="name=selenoid-ui" --format="{{.ID}}"))
docker-compose down
docker-compose up -d
sleep 5
SELENOID_IP=`docker inspect selenoid -f {{.NetworkSettings.IPAddress}}`
echo "Creating selenoid-ui    ..."
docker run -d --name selenoid-ui -p 8080:8080 aerokube/selenoid-ui --selenoid-uri http://${SELENOID_IP}:4444
WEBHOOK=`curl -s localhost:4551/api/tunnels | jq -r '.tunnels[0].public_url'`
echo ""
echo "Please add WebHOOK into Jenkins and GitHub:"
echo $WEBHOOK
echo ""
#научим дженкинс резолвить хост selenoid
docker exec jenkins /bin/bash -c "echo \"$SELENOID_IP      selenoid\" >> /etc/hosts"
echo "jenkins hosts:"
docker exec jenkins /bin/bash -c "cat /etc/hosts"
