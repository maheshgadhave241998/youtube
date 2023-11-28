package com.ShreeGanesh.youtube;

import com.ShreeGanesh.youtube.Service.Executor;
import com.ShreeGanesh.youtube.Service.SyncPipe;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.PrintWriter;

@SpringBootApplication
//@EnableSwagger2
public class YoutubeApplication {

    public static void main(String[] args) {


        SpringApplication.run(YoutubeApplication.class, args);
    }
   /* @Bean
    public CommandLineRunner schedulingRunner() {
        return args -> {
            // Your background task or long-running process
            while (true) {
                System.out.println("Application is still running...");
                Thread.sleep(1000); // Sleep for 1 second (adjust as needed)
            }
        };
    }*/

}
