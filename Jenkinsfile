pipeline {
    agent {
        docker {
            image 'gradle:7.6-jdk17-alpine'
            label 'docker'
            args '-v $JENKINS_HOME/gradle-home:/home/gradle'
        }
    }

    stages {
        stage('Build') {
            steps {
                sh 'gradle clean build'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
        }
        success {
            echo 'Build completed successfully.'
        }
        failure {
            echo 'Build failed.'
        }
    }
}