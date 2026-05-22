/*
package com.ShreeGanesh.youtube.Controller;

import com.ShreeGanesh.youtube.Service.Executor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/executor")
public class ExecutorController {

    @Autowired
    Executor executor;

    */
/*public ExecutorController(Executor executor) {
        this.executor = executor;
    }*//*


    @GetMapping("/download")
    @Operation(summary = "Download operation", description = "Executes download command")
    public void download() {
        executor.executeCommand(Executor.CommandType.DOWNLOAD_ONLY);
    }

    @GetMapping("/downloads")
    @Operation(summary = "Download operation", description = "Executes download command")
    public void downloads() {
        executor.executeCommand(Executor.CommandType.DOWNLOAD_ONLY);
    }

    @GetMapping("/show-formats")
    @Operation(summary = "Show Formats operation", description = "Executes show formats command")
    public void showFormats() {
        executor.executeCommand(Executor.CommandType.SHOW_FORMATS);
    }

    @GetMapping("/shows-formats")
    @Operation(summary = "Show Formats operation", description = "Executes show formats command")
    public void showsFormats() {
        executor.executeCommand(Executor.CommandType.SHOWs_FORMATS);
    }
}
*/


package com.ShreeGanesh.youtube.Controller;

import com.ShreeGanesh.youtube.Service.Executor;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/executor")
public class ExecutorController {

    @Autowired
    private Executor executor;

    @GetMapping("/download")
    @Operation(
            summary = "Download video",
            description = "Downloads youtube video"
    )
    public String download(
            @RequestParam String url
    ) {

        executor.executeCommand(
                Executor.CommandType.DOWNLOAD_ONLY,
                url
        );

        return "Download Started";
    }

    @GetMapping("/show-formats")
    public String showFormats(
            @RequestParam String url
    ) {

        return executor.executeCommand(
                Executor.CommandType.SHOW_FORMATS,
                url
        );
    }
    @GetMapping("/download-format")
    public void downloadFormat(
            @RequestParam String url,
            @RequestParam String format,
            HttpServletResponse response
    ) {

        try {

            File file =
                    executor.executeSpecificFormat(url, format);

            if (file == null || !file.exists()) {

                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Download failed"
                );

                return;
            }

            response.setContentType(
                    Files.probeContentType(file.toPath())
            );

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" + file.getName() + "\""
            );

            response.setContentLengthLong(file.length());

            Files.copy(
                    file.toPath(),
                    response.getOutputStream()
            );

            response.getOutputStream().flush();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    @GetMapping("/video-info")
    public String getVideoInfo(@RequestParam String url) {

        return executor.getVideoInfo(url);
    }
    @GetMapping("/download-progress")
    public SseEmitter downloadWithProgress(
            @RequestParam String url,
            @RequestParam String format
    ) {

        SseEmitter emitter =
                new SseEmitter(0L);

        new Thread(() -> {

            executor.downloadWithProgress(
                    url,
                    format,
                    emitter
            );

        }).start();

        return emitter;
    }
    @GetMapping("/download-file")
    public void downloadFile(
            @RequestParam String path,
            HttpServletResponse response
    ) {

        try {

            File file =
                    new File(path);

            response.setContentType(
                    "video/mp4"
            );

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" +
                            file.getName() +
                            "\""
            );

            response.setContentLengthLong(
                    file.length()
            );

            Files.copy(
                    file.toPath(),
                    response.getOutputStream()
            );

            response.getOutputStream().flush();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}