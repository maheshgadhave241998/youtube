package com.ShreeGanesh.youtube.Service;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;

@Service
public class Executor {

    String download_path = "D:\\spring";
    String exe_path = "D:\\spring\\youtube\\youtube\\src\\main\\resources\\";
    String url = "https://www.youtube.com/watch?v=7kDx_qg8e6A";
    String[] command =
            {
                    "cmd",
            };
    Process p;


    {
        try {
            p = Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        new Thread(new SyncPipe(System.err, p.getErrorStream())).start();
        new Thread(new SyncPipe(System.out, p.getInputStream())).start();
        PrintWriter stdin = new PrintWriter(p.getOutputStream());
        stdin.println("cd \"" + download_path + "\"");
//            stdin.println(download_path + "\\yt-dlp " + url);
        stdin.println(exe_path + "\\yt-dlp " + "-F " + url);
        stdin.close();
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
