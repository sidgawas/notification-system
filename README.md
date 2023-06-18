# Setup
## Starting Push Notification Service on local machine

### 1. Start RabbitMQ container
`docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management`

### 2. Build project
`./gradlew clean build`

### 3. Run Push notification gateway
`java -jar .\push-notification-gateway\build\libs\push-notification-gateway-0.0.1-SNAPSHOT.jar`

### 4. Import following cURL in Postman to get Server Sent Events
`curl --location 'http://localhost:8080/api/v1/push_notifications/1'`