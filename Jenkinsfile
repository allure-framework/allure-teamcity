pipeline {
    agent { label 'java' }
    tools { maven 'default' }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true clean verify'
            }
        }
        stage('Archive') {
            steps{
                archiveArtifacts 'target/allure-teamcity-plugin.zip'
            }
        }
    }
    post {
        failure {
            slackSend message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} failed (<${env.BUILD_URL}|Open>)",
                    color: 'danger', teamDomain: 'qameta', channel: 'allure', tokenCredentialId: 'allure-channel'
        }
    }
}
