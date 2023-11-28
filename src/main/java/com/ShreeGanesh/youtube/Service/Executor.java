package com.ShreeGanesh.youtube.Service;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;

@Service
public class Executor {

    private String downloadPath = "D:\\spring";
    private String exePath = "D:\\spring\\youtube\\youtube\\src\\main\\resources\\";
    private String url = "https://www.youtube.com/watch?v=7kDx_qg8e6A";
    private String[] baseCommand = { "cmd" };
    private Process p;

    public void executeCommand(CommandType commandType) {
        String[] command;
        switch (commandType) {
            case DOWNLOAD_ONLY:
                command = new String[]{exePath + "\\yt-dlp " + url};
                break;
            case SHOW_FORMATS:
                command = new String[]{exePath + "\\yt-dlp " + "-F "+ url};
                break;
            default:
                throw new IllegalArgumentException("Invalid command type: " + commandType);
        }

        try {
            p = Runtime.getRuntime().exec(baseCommand);
            startErrorStreamThread();
            startInputStreamThread();
            executeCommandInProcess(command);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void startErrorStreamThread() {
        new Thread(new SyncPipe(System.err, p.getErrorStream())).start();
    }

    private void startInputStreamThread() {
        new Thread(new SyncPipe(System.out, p.getInputStream())).start();
    }

    private void executeCommandInProcess(String[] command) {
        try {
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            stdin.println("cd \"" + downloadPath + "\"");
            for (String cmd : command) {
                stdin.println(cmd);
            }
            stdin.close();
            p.waitFor();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public enum CommandType {
        DOWNLOAD_ONLY,
        SHOW_FORMATS
    }
}