package com.ShreeGanesh.youtube.Service;

import org.springframework.stereotype.Service;

import java.io.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class Executor {

    private String exePath =
            BinarySetupService.BIN_DIR;

    private String ffmpegExePath =
            BinarySetupService.BIN_DIR;

    // ==============================
    // DOWNLOAD SPECIFIC FORMAT
    // ==============================
    public File executeSpecificFormat(String url, String format) {

        try {

            // TEMP DIRECTORY
            String tempDir =
                    System.getProperty("java.io.tmpdir");

            // OUTPUT FILE
            // =========================
// GET REAL VIDEO TITLE
// =========================
            String titleCommand =
                    exePath +
                            "\\yt-dlp.exe --get-title \"" +
                            url +
                            "\"";

            Process titleProcess =
                    Runtime.getRuntime()
                            .exec(titleCommand);

            BufferedReader titleReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    titleProcess.getInputStream()
                            )
                    );

            String videoTitle =
                    titleReader.readLine();

            titleProcess.waitFor();

            if (
                    videoTitle == null
                            || videoTitle.isBlank()
            ) {

                videoTitle =
                        "vidsave_" +
                                System.currentTimeMillis();
            }

// REMOVE INVALID FILE CHARS
            videoTitle =
                    videoTitle.replaceAll(
                            "[\\\\/:*?\"<>|]",
                            ""
                    );

// OUTPUT FILE
            File outputFile =
                    new File(
                            tempDir,
                            videoTitle + ".mp4"
                    );
            // COMMAND
            String command =
                    exePath + "\\yt-dlp.exe " +
                            "--ffmpeg-location \"" + ffmpegExePath + "\" " +
                            "-f \"" + format + "+bestaudio[ext=m4a]\" " +
                            "--merge-output-format mp4 " +
                            "--newline " +
                            "-o \"" + outputFile.getAbsolutePath() + "\" " +
                            "\"" + url + "\"";

            System.out.println("================================");
            System.out.println("EXECUTING COMMAND:");
            System.out.println(command);
            System.out.println("================================");

            ProcessBuilder builder =
                    new ProcessBuilder(
                            "cmd",
                            "/c",
                            command
                    );

            builder.redirectErrorStream(true);

            Process process =
                    builder.start();

            // LOGS
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                System.out.println(line);
            }

            int exitCode =
                    process.waitFor();

            System.out.println(
                    "YT-DLP EXIT CODE: " + exitCode
            );

            // CHECK FILE
            if (
                    !outputFile.exists()
                            || outputFile.length() == 0
            ) {

                System.out.println(
                        "FILE NOT CREATED"
                );

                return null;
            }

            System.out.println(
                    "FINAL FILE: " +
                            outputFile.getAbsolutePath()
            );

            System.out.println(
                    "FILE SIZE: " +
                            outputFile.length()
            );

            return outputFile;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    // ==============================
    // MAIN EXECUTOR
    // ==============================
    public String executeCommand(
            CommandType commandType,
            String url
    ) {

        StringBuilder output =
                new StringBuilder();

        String command = "";

        switch (commandType) {

            case SHOW_FORMATS:

                command =
                        exePath +
                                "\\yt-dlp.exe --no-warnings -F \"" +
                                url +
                                "\"";

                break;

            case DOWNLOAD_ONLY:

                command =
                        exePath +
                                "\\yt-dlp.exe " +
                                "--ffmpeg-location \"" +
                                ffmpegExePath +
                                "\" " +
                                "-f bestvideo+bestaudio " +
                                "--merge-output-format mp4 \"" +
                                url +
                                "\"";

                break;

            default:
                return "Invalid command type";
        }

        try {

            Process process =
                    Runtime.getRuntime()
                            .exec(command);

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                String lower =
                        line.toLowerCase();

                // FILTER ONLY MP4 + M4A
                if (
                        commandType ==
                                CommandType.SHOW_FORMATS
                ) {

                    if (
                            (
                                    lower.contains("mp4")
                                            || lower.contains("m4a")
                            )
                                    &&
                                    !lower.contains("webm")
                    ) {

                        output.append(line)
                                .append("\n");
                    }

                } else {

                    output.append(line)
                            .append("\n");
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

        String command =
                exePath +
                        "\\yt-dlp.exe --dump-json --no-warnings \"" +
                        url +
                        "\"";

        StringBuilder output =
                new StringBuilder();

        try {

            Process process =
                    Runtime.getRuntime()
                            .exec(command);

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
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

    public void downloadWithProgress(
            String url,
            String format,
            SseEmitter emitter
    ) {

        try {

            String tempDir =
                    System.getProperty("java.io.tmpdir");

            String fileName =
                    "vidsave_" +
                            System.currentTimeMillis() +
                            ".mp4";

            File outputFile =
                    new File(tempDir, fileName);

            String command =
                    exePath + "\\yt-dlp.exe " +
                            "--newline " +
                            "--ffmpeg-location \"" +
                            ffmpegExePath +
                            "\" " +
                            "-f \"" +
                            format +
                            "+bestaudio[ext=m4a]\" " +
                            "--merge-output-format mp4 " +
                            "-o \"" +
                            outputFile.getAbsolutePath() +
                            "\" " +
                            "\"" + url + "\"";

            ProcessBuilder builder =
                    new ProcessBuilder(
                            "cmd",
                            "/c",
                            command
                    );

            builder.redirectErrorStream(true);

            Process process =
                    builder.start();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                System.out.println(line);

                // SEND LIVE PROGRESS
                emitter.send(
                        SseEmitter.event()
                                .name("progress")
                                .data(line)
                );
            }

            // WAIT FOR DOWNLOAD COMPLETE
            int exitCode =
                    process.waitFor();

            System.out.println(
                    "EXIT CODE: " + exitCode
            );

            // FINAL FILE CHECK
            if (
                    exitCode == 0 &&
                            outputFile.exists() &&
                            outputFile.length() > 0
            ) {

                emitter.send(
                        SseEmitter.event()
                                .name("complete")
                                .data(
                                        outputFile.getAbsolutePath()
                                )
                );

            } else {

                emitter.send(
                        SseEmitter.event()
                                .name("error")
                                .data("File not created")
                );
            }

            emitter.complete();
            return;

        } catch (Exception e) {

            e.printStackTrace();

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