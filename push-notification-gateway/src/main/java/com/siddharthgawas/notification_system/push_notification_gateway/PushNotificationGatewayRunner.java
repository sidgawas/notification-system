package com.siddharthgawas.notification_system.push_notification_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The type Push notification gateway runner.
 */
@SpringBootApplication
public class PushNotificationGatewayRunner {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PushNotificationGatewayRunner.class, args);
    }
}
