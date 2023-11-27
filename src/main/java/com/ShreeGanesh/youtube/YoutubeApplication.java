package com.ShreeGanesh.youtube;

import com.ShreeGanesh.youtube.Service.Executor;
import com.ShreeGanesh.youtube.Service.SyncPipe;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintWriter;

@SpringBootApplication
public class YoutubeApplication {

    public static void main(String[] args) {


        SpringApplication.run(YoutubeApplication.class, args);
    }

}
