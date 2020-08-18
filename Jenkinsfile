pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                echo 'Checkout...'
                checkout scm
                stash 'sources'
            }
        }
        stage('Build') {
            steps {
                echo 'Build...'
                unstash 'sources'
                sh 'kill -9 $(lsof -t -i:8080) || echo "Process was not running."'
                dir('server') {
                    sh 'mvn clean package -DskipTests'
                }
                stash 'sources'
            }
        }
        stage('Run') {
            steps {
                sh 'kill -9 $(lsof -t -i:8080) || echo "Process was not running."'
                sh 'echo "java -jar server/target/dev-0.0.1-SNAPSHOT.jar > /logs/spring.txt" | at now + 1 minutes'
            }
        }
    }
    post {
        failure {
            emailext subject: 'Build failed in Jenkins: $PROJECT_NAME - #$BUILD_NUMBER',
                body: 'Check console output at $BUILD_URL to view the results. \n\n ${CHANGES} \n\n -------------------------------------------------- \n${BUILD_LOG, maxLines=100, escapeHtml=false}',
                replyTo: '$DEFAULT_REPLYTO',
                to: 'llavin@purdue.edu'
        }
    }
}   
