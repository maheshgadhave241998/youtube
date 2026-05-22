/*
package com.ShreeGanesh.youtube.Service;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;

@Service
public class Executor {

    private String downloadPath = "D:\\spring";
    private String exePath = "D:\\spring\\youtube\\youtube\\src\\main\\resources\\";
    private String url = "https://www.youtube.com/watch?v=oafxkMv4xnc&list=RDoafxkMv4xnc&start_radio=1";

    private String ffmegexepath="D:\\spring\\youtube\\youtube\\src\\main\\resources\\";
    private String[] baseCommand = { "cmd" };
    private Process p;

    public void executeCommand(CommandType commandType) {
        String[] command;
        switch (commandType) {
            case DOWNLOAD_ONLY:
                command = new String[]{exePath + "\\yt-dlp " + "--ffmpeg-location "+ffmegexepath+" -f bestvideo+bestaudio --merge-output-format mp4 "+ url};
                break;
            case SHOW_FORMATS:
                command = new String[]{exePath + "\\yt-dlp " + "-F "+ url};
                break;
            case SHOWs_FORMATS:
                command = new String[]{exePath + "\\yt-dlp " + "-f "+"137 "+ url};
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
        SHOW_FORMATS,
        SHOWs_FORMATS
    }
}*/

package com.ShreeGanesh.youtube.Service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@Service
public class Executor {

    private String downloadPath = "D:\\spring";

    private String exePath =
            "D:\\spring\\youtube\\youtube\\src\\main\\resources";

    private String ffmpegExePath =
            "D:\\spring\\youtube\\youtube\\src\\main\\resources";

    private String[] baseCommand = {"cmd"};

    private Process p;

    public void executeSpecificFormat(String url, String format) {

        String[] command = new String[]{

                exePath + "\\yt-dlp.exe " +
                        "--ffmpeg-location \"" + ffmpegExePath + "\" " +
                        "-f " + format + " " +
                        "--merge-output-format mp4 " +
                        "\"" + url + "\""
        };

        try {

            p = Runtime.getRuntime().exec(baseCommand);

            startErrorStreamThread();

            startInputStreamThread();

            executeCommandInProcess(command);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String executeCommand(CommandType commandType, String url) {

        StringBuilder output = new StringBuilder();

        String command = "";

        switch (commandType) {

            case SHOW_FORMATS:

                command =
                        exePath + "\\yt-dlp --no-warnings -F \"" + url + "\"";

                break;

            case DOWNLOAD_ONLY:

                command =
                        exePath + "\\yt-dlp --ffmpeg-location \"" +
                                ffmpegExePath +
                                "\" -f bestvideo+bestaudio --merge-output-format mp4 \"" +
                                url + "\"";

                break;
        }

        try {

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(process.getInputStream())
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                output.append(line).append("\n");
            }

            process.waitFor();

        } catch (Exception e) {

            return e.getMessage();
        }

        return output.toString();
    }

    private void startErrorStreamThread() {
        new Thread(new SyncPipe(System.err, p.getErrorStream())).start();
    }

    private void startInputStreamThread() {
        new Thread(new SyncPipe(System.out, p.getInputStream())).start();
    }

    private void executeCommandInProcess(String[] command) {

        try {

            PrintWriter stdin =
                    new PrintWriter(p.getOutputStream());

            stdin.println("cd /d \"" + downloadPath + "\"");

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
        SHOW_FORMATS,
        SHOWS_FORMATS
    }
}