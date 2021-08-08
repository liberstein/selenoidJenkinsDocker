pipeline {
    agent any
    tools {
        maven 'maven'
    }

    triggers {
        githubPush()
    }

    parameters {
        string(name: 'GIT_URL', defaultValue: 'https://github.com/liberstein/selenoidJenkinsDocker.git', description: 'The target git url')
        string(name: 'GIT_BRANCH', defaultValue: 'master', description: 'The target git branch')
        string(name: 'EMAIL_RECIPIENT', defaultValue: 'libersteon@gmail.com', description: 'Default recipient')
        choice(name: 'BROWSER_NAME', choices: ['chrome', 'firefox'], description: 'Pick the target browser in Selenoid')
        choice(name: 'BROWSER_VERSION', choices: ['92.0', '86.0', '85.0'], description: 'Pick the target browser version in Selenoid')
    }

    stages {
        stage('Pull from GitHub') {
            steps {
                slackSend(message: "Jenkins in progress ...")
                git ([
                        url: "${params.GIT_URL}",
                        branch: "${params.GIT_BRANCH}"
                ])
            }
        }
        stage('Run maven clean test') {
            steps {
                sh 'mvn clean test -Dbrowser_name=$BROWSER_NAME -Dbrowser_version=$BROWSER_VERSION'
            }
        }
        stage('Backup and Reports') {
            steps {
                archiveArtifacts artifacts: '**/target/', fingerprint: true
            }
            post {
                always {
                    script {
                        step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: "${params.EMAIL_RECIPIENT}", sendToIndividuals: true])

                        // Формирование отчета
                        allure([
                                includeProperties: false,
                                jdk: '',
                                properties: [],
                                reportBuildPolicy: 'ALWAYS',
                                results: [[path: 'target/allure-results']]
                        ])
                        println('allure report created')

                        // Узнаем ветку репозитория
                        def branch = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD\n').trim().tokenize().last()
                        println("branch= " + branch)

                        // Достаем информацию по тестам из junit репорта
                        def summary = junit testResults: '**/target/surefire-reports/*.xml'
                        println("summary generated")

                        // Текст оповещения
                        def message = "${currentBuild.currentResult}: Job ${env.JOB_NAME}, build ${env.BUILD_NUMBER}, branch ${branch}\nTest Summary - ${summary.totalCount}, Failures: ${summary.failCount}, Skipped: ${summary.skipCount}, Passed: ${summary.passCount}\nMore info at: ${env.BUILD_URL}\nElapsed: ${currentBuild.durationString}"
                        println("message= " + message)
                        slackSend color: 'good', message: message

                        emailext body: '''${SCRIPT, template="groovy-html.template"}''',
                                mimeType: 'text/html',
                                subject: "[Jenkins] ${currentBuild.fullDisplayName}",
                                to: "${params.EMAIL_RECIPIENT}",
                                replyTo: "${params.EMAIL_RECIPIENT}",
                                recipientProviders: [[$class: 'CulpritsRecipientProvider']]
                    }
                }
            }
        }
    }
}