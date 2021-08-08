#!/usr/bin/env groovy

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

    def msg = "${buildStatus}: `${env.JOB_NAME}` #${env.BUILD_NUMBER}:\n${env.BUILD_URL}allure/"

    slackSend(color: color, message: msg)
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

def runTests(xml_config='1') {

    docker.image('maven').inside('-v /var/run/docker.sock:/var/run/docker.sock -v /home/admin/.m2:/root/.m2') {

        git branch: "${APITESTS_REPO_BRANCH}", url: "${APITESTS_REPO_URL}", credentialsId: "jenkins-github"

        sh "mvn test -Dxml.file=${xml_config}"
    }
}

return this