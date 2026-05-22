/*
package com.ShreeGanesh.youtube.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YtDlpExample {

    public static void main(String[] args) {
        // Replace 'yt-dlp', '-g', 'video_url' with the appropriate command and its arguments
//        List<String> command = new ArrayList<>();
//        command.add("D:\\spring\\youtube\\youtube\\src\\main\\resources\\yt-dlp.exe");
//        command.add("-F");
//        command.add("https://www.youtube.com/watch?v=7kDx_qg8e6A");

        String url = "https://www.youtube.com/watch?v=UF7YH84kZPA";
//        yt-dlp -F https://www.youtube.com/watch?v=UF7YH84kZP | findstr "m3u8"

        try {
            // Build the command
            String[] command = {"D:\\spring\\youtube\\youtube\\src\\main\\resources\\yt-dlp.exe", "-F", url,"findstr /C:m3u8" };

            // Start the process
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Regular expression pattern for extracting ID, EXT, RESOLUTION, FILESIZE, and MORE INFO
            Pattern pattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+(\\S+)\\s+([\\S\\s]+)");

            // Print the table header
            System.out.printf("%-10s%-10s%-15s%-15s%n", "ID", "EXT", "RESOLUTION", "FILESIZE");

            // Parse each line of the output
            while ((line = reader.readLine()) != null) {
                // Match the pattern in the line
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String id = matcher.group(1);
                    String ext = matcher.group(2);
                    String resolution = matcher.group(3);
                    String fileSize = matcher.group(4);

                    // Check if EXT is "mp4" and MORE INFO is blank
                    if (ext.equals("mp4") && (fileSize!=("https"))) {
                        // Print ID, EXT, RESOLUTION, and FILESIZE
                        System.out.printf("%-10s%-10s%-15s%-15s%n", id, ext, resolution, fileSize);
                    }
                }
            }

            // Wait for the process to finish
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}*/
