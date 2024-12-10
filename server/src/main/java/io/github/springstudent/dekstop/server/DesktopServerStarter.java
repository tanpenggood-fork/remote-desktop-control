package io.github.springstudent.dekstop.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ZhouNing
 * @date 2024/12/6 8:45
 **/
@SpringBootApplication
@EnableScheduling
public class DesktopServerStarter {

    public static void main(String[] args) {
        SpringApplication.run(DesktopServerStarter.class, args);
    }

}
