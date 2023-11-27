package com.ShreeGanesh.youtube;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintWriter;

@SpringBootApplication
public class YoutubeApplication {

    public static void main(String[] args) {

        String download_path = "D:\\spring";
        String exe_path="D:\\spring\\youtube\\youtube\\src\\main\\resources\\";
        String url = "https://www.youtube.com/watch?v=7kDx_qg8e6A";
        String[] command =
                {
                        "cmd",
                };
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            new Thread(new SyncPipe(System.err, p.getErrorStream())).start();
            new Thread(new SyncPipe(System.out, p.getInputStream())).start();
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            stdin.println("cd \"" + download_path + "\"");
//            stdin.println(download_path + "\\yt-dlp " + url);
            stdin.println(exe_path +"\\yt-dlp " + url);
            stdin.close();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpringApplication.run(YoutubeApplication.class, args);
    }

}
