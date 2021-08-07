pipeline {
    agent any
    tools {
        maven 'maven'
    }
    
    triggers {
        githubPush()
    }

    parameters {
        string(name: 'GIT_URL', defaultValue: 'https://github.com/liberstein/apiHelperExample-1.git', description: 'The target git url')
        string(name: 'GIT_BRANCH', defaultValue: 'jenkins', description: 'The target git branch')
        choice(name: 'BROWSER_NAME', choices: ['chrome', 'firefox'], description: 'Pick the target browser in Selenoid')
        choice(name: 'BROWSER_VERSION', choices: ['92.0', '86.0', '85.0'], description: 'Pick the target browser version in Selenoid')
    }

    stages {
        stage('Pull from GitHub') {
            steps {             
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
                    if (currentBuild.currentResult == 'SUCCESS') {
                    step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: "liberstein@gmail.com", sendToIndividuals: true])
                    } else {
                    step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: "liberstein@gmail.com", sendToIndividuals: true])
                    }


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
                    def message = "${currentBuild.currentResult}: Job ${env.JOB_NAME}, build ${env.BUILD_NUMBER}, branch ${branch}\nTest Summary - ${summary.totalCount}, Failures: ${summary.failCount}, Skipped: ${summary.skipCount}, Passed: ${summary.passCount}\nMore info at: ${env.BUILD_URL}"
                    println("message= " + message)
                   
                    slackSend color: 'good', message: message
                    //emailNotification()
                  
                  }
                }
            }
        }
    }
}

def emailNotification() {
    def to = emailextrecipients([[$class: 'CulpritsRecipientProvider'],
                                 [$class: 'DevelopersRecipientProvider'],
                                 [$class: 'RequesterRecipientProvider']])
    String currentResult = currentBuild.result
    String previousResult = currentBuild.getPreviousBuild().result

    def causes = currentBuild.rawBuild.getCauses()
    // E.g. 'started by user', 'triggered by scm change'
    def cause = null
    if (!causes.isEmpty()) {
        cause = causes[0].getShortDescription()
    }

    // Ensure we don't keep a list of causes, or we get
    // "java.io.NotSerializableException: hudson.model.Cause$UserIdCause"
    // see http://stackoverflow.com/a/37897833/509706
    causes = null

    String subject = "$env.JOB_NAME $env.BUILD_NUMBER: $currentResult"

    String body = """
<p>Build $env.BUILD_NUMBER ran on $env.NODE_NAME and terminated with $currentResult.
</p>

<p>Build trigger: $cause</p>

<p>See: <a href="$env.BUILD_URL">$env.BUILD_URL</a></p>

"""

    String log = currentBuild.rawBuild.getLog(40).join('\n')
    if (currentBuild != 'SUCCESS') {
        body = body + """
<h2>Last lines of output</h2>
<pre>$log</pre>
"""
    }

    if (to != null && !to.isEmpty()) {
        // Email on any failures, and on first success.
        if (currentResult != 'SUCCESS' || currentResult != previousResult) {
            mail to: to, subject: subject, body: body, mimeType: "text/html"
        }
        echo 'Sent email notification'
    }
}

def notifySlack(String buildStatus = 'STARTED') {
    // Build status of null means success.
    buildStatus = buildStatus ?: 'SUCCESS'
    def color
    if (buildStatus == 'STARTED') {
        color = '#D4DADF'
    } else if (buildStatus == 'SUCCESS') {
        color = '#BDFFC3'
    } else if (buildStatus == 'UNSTABLE') {
        color = '#FFFE89'
    } else {
        color = '#FF9FA1'
    }
    def msg = "${buildStatus}: `${env.JOB_NAME}` #${env.BUILD_NUMBER}:\n${env.BUILD_URL}"
    slackSend(color: color, message: msg)
}
node {
    try {
        notifySlack()
        // sh 'runbuild' //if you need to fail this pipeline
    } catch (e) {
        currentBuild.result = 'FAILURE'
        throw e
    } finally {
        notifySlack(currentBuild.result)
    }
}
