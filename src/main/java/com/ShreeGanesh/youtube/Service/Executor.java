package com.ShreeGanesh.youtube.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class Executor {

    private static final Logger log = LoggerFactory.getLogger(Executor.class);

    private final String ytDlp = "yt-dlp";
    private final String ffmpeg = "ffmpeg";

    private final String cookiesPath = "/app/cookies.txt";

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // ==============================
    // DOWNLOAD SPECIFIC FORMAT
    // ==============================
    public File executeSpecificFormat(String url, String format) {

        try {

            String tempDir = System.getProperty("java.io.tmpdir");

            // GET TITLE
            ProcessBuilder titlePb = new ProcessBuilder(
                    ytDlp,
                    "--cookies", cookiesPath,
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
                videoTitle = "vid_" + System.currentTimeMillis();
            }

            videoTitle = videoTitle.replaceAll("[\\\\/:*?\"<>|]", "");

            File outputFile = new File(tempDir, videoTitle + ".mp4");

            log.info("Starting download: {}", url);

            ProcessBuilder builder = new ProcessBuilder(
                    ytDlp,
                    "--cookies", cookiesPath,
                    "--ffmpeg-location", ffmpeg,
                    "--newline",
                    "-f", format + "+bestaudio[ext=m4a]",
                    "--merge-output-format", "mp4",
                    "-o", outputFile.getAbsolutePath(),
                    url
            );

            Process process = builder.start();

            // 🔥 ASYNC STREAM (IMPORTANT FIX)
            executorService.submit(() -> {
                try (BufferedReader reader =
                             new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("[yt-dlp] {}", line);
                    }

                } catch (Exception e) {
                    log.error("stdout error", e);
                }
            });

            executorService.submit(() -> {
                try (BufferedReader reader =
                             new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("[yt-dlp-err] {}", line);
                    }

                } catch (Exception e) {
                    log.error("stderr error", e);
                }
            });

            int exitCode = process.waitFor();

            log.info("YT-DLP EXIT CODE: {}", exitCode);

            if (exitCode != 0 || !outputFile.exists() || outputFile.length() == 0) {
                log.error("Download failed for URL: {}", url);
                return null;
            }

            return outputFile;

        } catch (Exception e) {
            log.error("executeSpecificFormat error", e);
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
                            "--cookies", cookiesPath,
                            "--no-warnings",
                            "-F",
                            url
                    );
                    break;

                case DOWNLOAD_ONLY:
                    builder = new ProcessBuilder(
                            ytDlp,
                            "--cookies", cookiesPath,
                            "--ffmpeg-location", ffmpeg,
                            "-f", "bestvideo+bestaudio",
                            "--merge-output-format", "mp4",
                            url
                    );
                    break;

                default:
                    return "Invalid command type";
            }

            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                log.info(line);
            }

            process.waitFor();

        } catch (Exception e) {
            log.error("executeCommand error", e);
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
                    "--cookies", cookiesPath,
                    "--dump-json",
                    "--no-warnings",
                    url
            );

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
            log.error("getVideoInfo error", e);
            return e.getMessage();
        }

        return output.toString();
    }

    // ==============================
    // SSE DOWNLOAD PROGRESS (FIXED)
    // ==============================
    public void downloadWithProgress(String url, String format, SseEmitter emitter) {

        try {

            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "vid_" + System.currentTimeMillis() + ".mp4";

            File outputFile = new File(tempDir, fileName);

            log.info("SSE download started: {}", url);

            ProcessBuilder builder = new ProcessBuilder(
                    ytDlp,
                    "--cookies", cookiesPath,
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

                log.info("[SSE yt-dlp] {}", line);

                try {
                    emitter.send(
                            SseEmitter.event()
                                    .name("progress")
                                    .data(line)
                    );
                } catch (Exception e) {
                    log.error("SSE broken (client disconnected)", e);
                    process.destroy();
                    break;
                }
            }

            int exitCode = process.waitFor();

            log.info("SSE EXIT CODE: {}", exitCode);

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
                                .data("Download failed")
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

    public enum CommandType {
        DOWNLOAD_ONLY,
        SHOW_FORMATS,
        SHOWS_FORMATS
    }
}