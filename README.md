#### [Lesson from Java QA Engineer (by OTUS)][link]:
#### Запуск окружения:
```bash
cd infra && sudo ./start.sh && cd ..
```
###### В результате запуска поднимутся докер контейнеры:
- ngrok на порту 4551
- настроенный jenkins на порту 8083 -> Мануалы [1][Jenkins-docker-compose] и [2][jenkinsTutorial]
- selenoid(HUB) на порту 4444
- selenoid-ui на порту 8080

###### Проверено в окружении:
- Ubuntu 18.04.1
- IDEA 2021.2 RC
- MAVEN 3.6.0
- OpenJDK 1.8.0_292 (JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre)
- Allure 2.13.2
- Bash version 4.4.20(1)-release (x86_64-pc-linux-gnu)
- Docker version 20.10.7, build f0df350 
- Docker-compose version 1.27.4, build 40524192 (на младших версиях синтаксис может не отработать)

###### Удаленный запуск тестов:
- Установить вебхук в GitHub и в Jenkins по адресу http://0.0.0.0:8083/ (логинимся как test/test)
- Идём в джобу = WebHok Pipeline и запускаем тест, под хромом 86.0

###### Локальный запуск тестов:
Настройки:
```bash
source ~/.profile
cat /etc/hosts   # убеждаемся что хосты selenoid и jenkins резолвятся и доступны
```
Валидация Jenkinsfile:
```bash
curl --user test:test -X POST -F "jenkinsfile=<Jenkinsfile" http://jenkins:8080/pipeline-model-converter/validate
curl --user test:test -X POST -F "jenkinsfile=<Jenkinsfile" http://0.0.0.0:8083/pipeline-model-converter/validate
```
запуск после настроек:
```bash
mvn clean test # важно соответствие модели в файле infra/selenoid/config/browsers.json + присутствие контейнера браузера!
mvn clean test -Dbrowser_name=chrome -Dbrowser_version=86.0
```

###### Allure report (IDEA -> maven/Plugins/allure -> allure:serve):
```bash
source ~/.profile
allure generate target/allure-results/ --clean -o allure-report && allure open
```

###### Цель:
- Запустите и настройте локально Jenkins (сервис или контейнер)
- Создайте job (можно использовать job, созданную в ходе выполнения прошлого домашнего задания) 
- Шаги в job:
1) Выгрузить из вашего репозитория код тестов
2) Собрать проект
3) Выполнить все тесты
4) Прислать письмо вам на почту со значениями:
- номер сборки, статус сборки, ветка репозитория, количество тестов (всего/успешных/проваленных/пропущенных), общее время выполнения job'ы
5) Job'а должна запускаться после каждого git push'а в ваш репозиторий (использовать webhooks) и каждую ночь в 01:00. 
6) Помимо отчетности по e-mail, отчет должен приходить в канал в slack 
7) Отчеты должны добавляться в систему отчетов Allure
8) По окончанию выполнения job, должен выполняться back-up самой job'ы и настроек (можно использовать SCM Sync configuration plugin)
---
###### Критерии оценки:
- +3 удалось запустить Jenkins (настроить) 
- +2 создать job для запуска автотестов из репозитория 
- +2 job запускается в автоматическом режиме при Git push в ветку master 
- +1 настроенная система отчетов 
- +1 выполняется back-up самой job'ы и настроек 
- +1 хранится история только 5 последних прогонов

###### Минимальный бал: 10

---
###### cat ~/.profile:
```bash
export M2_HOME=/usr/share/maven
JAVA_HOME=$(dirname $( readlink -f $(which java) ))
JAVA_HOME=$(realpath "$JAVA_HOME"/../)
export JAVA_HOME
export ALLURE_HOME=/home/egorbunov/Desktop/allure-2.14.0
PATH="$HOME/bin:$HOME/.local/bin:$PATH:$JAVA_HOME/bin:$M2_HOME/bin:$ALLURE_HOME/bin"
export PATH

```

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

[link]: <https://otus.ru/learning/102096/>
[Jenkins-docker-compose]: <https://adamtheautomator.com/jenkins-docker/>
[ngrok-docker-compose]: <https://github.com/shkoliar/docker-ngrok>
[jenkinsTutorial]: <https://github.com/liberstein/JenkinsImageTutorial>
