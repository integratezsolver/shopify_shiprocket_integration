pipeline {
    agent any

    tools {
        jdk 'jdk21'        // Make sure JDK 21 is installed in Jenkins
        gradle 'gradle8'   // Add Gradle installation name from Jenkins
    }

    environment {
        DB_URL = "jdbc:mysql://localhost:3306/integratez?useSSL=false"
        DB_USER = "root"
        DB_PASS = "root"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/integratezsolver/shopify_shiprocket_integration'
            }
        }

         stage('Build JAR') {
                    steps {
                        sh './gradlew clean build -x test'
                    }
                }

         stage('Run App') {
                   steps {
                      sh 'nohup java -jar build/libs/*.jar > app.log 2>&1 &'
                   }
               }

    }

    post {
        always {
            echo 'Build process completed.'
        }
    }
}
