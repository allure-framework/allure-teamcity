pipeline {
    agent { label 'java' }
    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('Archive') {
            steps {
                archiveArtifacts 'allure-teamcity-server/build/distributions/allure-teamcity.zip'
            }
        }
    }
    post {
        always {
            deleteDir()
        }
        failure {
            slackSend message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} failed (<${env.BUILD_URL}|Open>)",
                    color: 'danger', teamDomain: 'qameta', channel: 'allure', tokenCredentialId: 'allure-channel'
        }
    }
}
