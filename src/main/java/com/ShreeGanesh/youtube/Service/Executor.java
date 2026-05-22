package com.ShreeGanesh.youtube.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;

@Service
public class Executor {

    // Linux system-installed binaries
    private final String ytDlp = "yt-dlp";
    private final String ffmpeg = "ffmpeg";

    // ==============================
    // DOWNLOAD SPECIFIC FORMAT
    // ==============================
    public File executeSpecificFormat(String url, String format) {

        try {

            String tempDir = System.getProperty("java.io.tmpdir");

            // =========================
            // GET VIDEO TITLE
            // =========================
            ProcessBuilder titlePb = new ProcessBuilder(
                    ytDlp,
                    "--get-title",
                    url
            );

            Process titleProcess = titlePb.start();

            BufferedReader titleReader = new BufferedReader(
                    new InputStreamReader(titleProcess.getInputStream())
            );

            String videoTitle = titleReader.readLine();
            titleProcess.waitFor();

            if (videoTitle == null || videoTitle.isBlank()) {
                videoTitle = "vidsave_" + System.currentTimeMillis();
            }

            videoTitle = videoTitle.replaceAll("[\\\\/:*?\"<>|]", "");

            File outputFile = new File(tempDir, videoTitle + ".mp4");

            // =========================
            // DOWNLOAD COMMAND
            // =========================
            ProcessBuilder builder = new ProcessBuilder(
                    ytDlp,
                    "--ffmpeg-location", ffmpeg,
                    "-f", format + "+bestaudio[ext=m4a]",
                    "--merge-output-format", "mp4",
                    "--newline",
                    "-o", outputFile.getAbsolutePath(),
                    url
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("YT-DLP EXIT CODE: " + exitCode);

            if (!outputFile.exists() || outputFile.length() == 0) {
                System.out.println("FILE NOT CREATED");
                return null;
            }

            return outputFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==============================
    // SHOW FORMATS / DOWNLOAD INFO
    // ==============================
    public String executeCommand(CommandType commandType, String url) {

        StringBuilder output = new StringBuilder();

        try {

            ProcessBuilder builder;

            switch (commandType) {

                case SHOW_FORMATS:
                    builder = new ProcessBuilder(
                            ytDlp,
                            "--no-warnings",
                            "-F",
                            url
                    );
                    break;

                case DOWNLOAD_ONLY:
                    builder = new ProcessBuilder(
                            ytDlp,
                            "--ffmpeg-location", ffmpeg,
                            "-f", "bestvideo+bestaudio",
                            "--merge-output-format", "mp4",
                            url
                    );
                    break;

                default:
                    return "Invalid command type";
            }

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {

                String lower = line.toLowerCase();

                if (commandType == CommandType.SHOW_FORMATS) {
                    if ((lower.contains("mp4") || lower.contains("m4a"))
                            && !lower.contains("webm")) {
                        output.append(line).append("\n");
                    }
                } else {
                    output.append(line).append("\n");
                }
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return output.toString();
    }

    // ==============================
    // VIDEO INFO
    // ==============================
    public String getVideoInfo(String url) {

        StringBuilder output = new StringBuilder();

        try {

            ProcessBuilder builder = new ProcessBuilder(
                    ytDlp,
                    "--dump-json",
                    "--no-warnings",
                    url
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            process.waitFor();

        } catch (Exception e) {
            return e.getMessage();
        }

        return output.toString();
    }

    // ==============================
    // DOWNLOAD WITH PROGRESS (SSE)
    // ==============================
    public void downloadWithProgress(String url, String format, SseEmitter emitter) {

        try {

            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "vidsave_" + System.currentTimeMillis() + ".mp4";
            File outputFile = new File(tempDir, fileName);

            ProcessBuilder builder = new ProcessBuilder(
                    ytDlp,
                    "--newline",
                    "--ffmpeg-location", ffmpeg,
                    "-f", format + "+bestaudio[ext=m4a]",
                    "--merge-output-format", "mp4",
                    "-o", outputFile.getAbsolutePath(),
                    url
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {

                emitter.send(
                        SseEmitter.event()
                                .name("progress")
                                .data(line)
                );
            }

            int exitCode = process.waitFor();

            if (exitCode == 0 && outputFile.exists() && outputFile.length() > 0) {

                emitter.send(
                        SseEmitter.event()
                                .name("complete")
                                .data(outputFile.getAbsolutePath())
                );

            } else {

                emitter.send(
                        SseEmitter.event()
                                .name("error")
                                .data("File not created")
                );
            }

            emitter.complete();

        } catch (Exception e) {

            try {
                emitter.send(
                        SseEmitter.event()
                                .name("error")
                                .data(e.getMessage())
                );
            } catch (Exception ignored) {}

            emitter.completeWithError(e);
        }
    }

    // ==============================
    // COMMAND TYPES
    // ==============================
    public enum CommandType {
        DOWNLOAD_ONLY,
        SHOW_FORMATS,
        SHOWS_FORMATS
    }
}