pipeline {
    agent any

    environment {
        IMAGE_NAME     = "literature-platform"
        CONTAINER_NAME = "literature-platform-container"
        APP_PORT       = "7575"
        NETWORK_NAME   = "app-network"
    }

    stages {
        stage('1. Checkout') {
            steps {
                checkout scm
            }
        }

        stage('2. Build Docker Image') {
            steps {
                echo "Docker image qurilmoqda..."
                sh """
                    docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -t ${IMAGE_NAME}:latest .
                """
            }
        }

        stage('3. Run Application') {
            steps {
                echo "Ilova ishga tushirilmoqda..."
                sh """
                    # Loglar papkasi
                    mkdir -p ${WORKSPACE}/logs
                    chmod -R 777 ${WORKSPACE}/logs

                    # Eski konteynerni tozalash
                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true

                    # Network yaratish (agar yo'q bo'lsa)
                    docker network inspect ${NETWORK_NAME} >/dev/null 2>&1 || docker network create ${NETWORK_NAME}

                    # Faqat oddiy run — hech qanday -e yo'q!
                    docker run -d \
                      --name "${CONTAINER_NAME}" \
                      -p ${APP_PORT}:8080 \
                      --network ${NETWORK_NAME} \
                      --restart unless-stopped \
                      -v "${WORKSPACE}/logs:/app/logs" \
                      ${IMAGE_NAME}:latest
                """
                echo "Dastur ishga tushdi → http://localhost:${APP_PORT} ga kiring"
            }
        }
    }

    post {
        always {
            echo "Pipeline yakunlandi"
        }
    }
}