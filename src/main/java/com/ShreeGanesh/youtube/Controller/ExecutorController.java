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

    /*public ExecutorController(Executor executor) {
        this.executor = executor;
    }*/

    @GetMapping("/download")
    @Operation(summary = "Download operation", description = "Executes download command")
    public void download() {
        executor.executeCommand(Executor.CommandType.DOWNLOAD_ONLY);
    }

    @GetMapping("/show-formats")
    @Operation(summary = "Show Formats operation", description = "Executes show formats command")
    public void showFormats() {
        executor.executeCommand(Executor.CommandType.SHOW_FORMATS);
    }
}
