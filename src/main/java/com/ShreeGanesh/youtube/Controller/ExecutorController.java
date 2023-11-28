package com.ShreeGanesh.youtube.Controller;

import com.ShreeGanesh.youtube.Service.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/executor")
public class ExecutorController {



     @Autowired
     Executor executor;

    public ExecutorController(Executor executor) {
        this.executor = executor;
    }

    @GetMapping("/download")
    public void download() {
        executor.executeCommand(Executor.CommandType.DOWNLOAD_ONLY);
    }

    @GetMapping("/show-formats")
    public void showFormats() {
        executor.executeCommand(Executor.CommandType.SHOW_FORMATS);
    }
}
