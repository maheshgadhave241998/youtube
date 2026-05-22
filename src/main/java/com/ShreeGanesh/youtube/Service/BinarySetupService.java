package com.ShreeGanesh.youtube.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class BinarySetupService {

    // ======================================================
    // YOUR RESOURCE FOLDER
    // ======================================================
    public static final String BIN_DIR =
            "/src/main/resources";

    // ======================================================
    // FINAL EXE PATHS
    // ======================================================
    public static final String YT_DLP_EXE =
            BIN_DIR + "\\yt-dlp.exe";

    public static final String FFMPEG_EXE =
            BIN_DIR + "\\ffmpeg.exe";

    public static final String FFPROBE_EXE =
            BIN_DIR + "\\ffprobe.exe";

    // ======================================================
    // DOWNLOAD URLS
    // ======================================================
    private static final String YT_DLP_URL =
            "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe";

    private static final String FFMPEG_ZIP_URL =
            "https://www.gyan.dev/ffmpeg/builds/ffmpeg-release-essentials.zip";

    // ======================================================
    // AUTO RUN WHEN SPRING STARTS
    // ======================================================
    @PostConstruct
    public void init() {

        try {

            System.out.println("\n======================================");
            System.out.println("STARTING BINARY SETUP");
            System.out.println("======================================\n");

            // CREATE FOLDER IF NOT EXISTS
            Files.createDirectories(
                    Paths.get(BIN_DIR)
            );

            // ======================================================
            // DOWNLOAD yt-dlp.exe
            // ======================================================
            if (!Files.exists(Paths.get(YT_DLP_EXE))) {

                System.out.println("Downloading yt-dlp.exe...");

                downloadFile(
                        YT_DLP_URL,
                        YT_DLP_EXE
                );

                System.out.println("yt-dlp.exe downloaded");
            }
            else {

                System.out.println("yt-dlp.exe already exists");
            }

            // ======================================================
            // CHECK FFMPEG
            // ======================================================
            boolean ffmpegMissing =
                    !Files.exists(Paths.get(FFMPEG_EXE));

            boolean ffprobeMissing =
                    !Files.exists(Paths.get(FFPROBE_EXE));

            if (ffmpegMissing || ffprobeMissing) {

                String zipPath =
                        BIN_DIR + "\\ffmpeg.zip";

                System.out.println("Downloading ffmpeg zip...");

                downloadFile(
                        FFMPEG_ZIP_URL,
                        zipPath
                );

                System.out.println("Extracting ffmpeg files...");

                extractExeFiles(
                        zipPath,
                        BIN_DIR
                );

                // DELETE ZIP AFTER EXTRACTION
                Files.deleteIfExists(
                        Paths.get(zipPath)
                );

                System.out.println("ffmpeg setup completed");
            }
            else {

                System.out.println("ffmpeg.exe already exists");
                System.out.println("ffprobe.exe already exists");
            }

            System.out.println("\n======================================");
            System.out.println("ALL BINARIES READY");
            System.out.println("======================================\n");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ======================================================
    // DOWNLOAD FILE
    // ======================================================
    private void downloadFile(
            String url,
            String outputPath
    ) throws Exception {

        try (
                InputStream inputStream =
                        new BufferedInputStream(
                                new URL(url).openStream()
                        )
        ) {

            Files.copy(
                    inputStream,
                    Paths.get(outputPath),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    // ======================================================
    // EXTRACT ONLY ffmpeg.exe + ffprobe.exe
    // ======================================================
    private void extractExeFiles(
            String zipFilePath,
            String outputDir
    ) throws Exception {

        try (
                ZipInputStream zis =
                        new ZipInputStream(
                                new FileInputStream(zipFilePath)
                        )
        ) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                String entryName =
                        entry.getName();

                // ONLY THESE FILES
                if (
                        entryName.endsWith("ffmpeg.exe")
                                ||
                                entryName.endsWith("ffprobe.exe")
                ) {

                    String fileName =
                            entryName.substring(
                                    entryName.lastIndexOf("/") + 1
                            );

                    Path outputPath =
                            Paths.get(
                                    outputDir,
                                    fileName
                            );

                    Files.copy(
                            zis,
                            outputPath,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    System.out.println(
                            "Extracted: " + fileName
                    );
                }

                zis.closeEntry();
            }
        }
    }
}