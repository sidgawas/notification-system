pipeline {
    agent {
        docker {
            image 'gradle:7.6-jdk17-alpine'
            label 'docker'
            args '-v ${env.WORKSPACE}/gradle-home:/home/gradle'
        }
    }

    stages {
        stage('Build') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build'
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