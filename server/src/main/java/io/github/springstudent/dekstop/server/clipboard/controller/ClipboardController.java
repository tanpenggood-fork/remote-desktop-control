package io.github.springstudent.dekstop.server.clipboard.controller;

import io.github.springstudent.dekstop.server.clipboard.pojo.Clipboard;
import io.github.springstudent.dekstop.server.clipboard.service.ClipboardService;
import io.github.springstudent.dekstop.server.file.controller.FileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ZhouNing
 * @date 2024/12/31 9:33
 **/
@RestController
@RequestMapping("/clipboard")
public class ClipboardController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private ClipboardService clipboardService;

    @PostMapping("/clear")
    public void clear(@RequestParam String deviceCode) throws Exception {
        try {
            clipboardService.clear(deviceCode);
        } catch (Exception e) {
            log.error("clear error,deviceCode={}", deviceCode, e);
            throw e;
        }
    }

    @PostMapping("/add")
    public String add(@RequestBody Clipboard clipboard) throws Exception {
        try {
            return clipboardService.add(clipboard);
        } catch (Exception e) {
            log.error("add error,clipboard={}", clipboard, e);
            throw e;
        }
    }

    @GetMapping("/get")
    public List<Clipboard> get(@RequestParam String deviceCode) throws Exception {
        try {
            return clipboardService.get(deviceCode);
        } catch (Exception e) {
            log.error("get error,deviceCode={}",deviceCode,e);
            throw e;
        }
    }

}
