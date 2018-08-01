package org.benetech.mathshare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "PMD.UseUtilityClass"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        getLogger().info("MathShare is running");
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(Application.class);
    }
}
