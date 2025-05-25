package io.github.springstudent.dekstop.server.web.controller;

import io.github.springstudent.dekstop.server.netty.NettyChannelManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanpenggood
 * @date 2025/05/25 8:46
 **/
@RestController
@RequestMapping("/web")
public class WebController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, Map>> info() {
        Map<String, Map> result = new HashMap<>();
        result.put("deviceCodeChannel", NettyChannelManager.getDeviceCodeChannelMap());
        result.put("channelBrother", NettyChannelManager.getChannelBrotherMap());
        return ResponseEntity.ok(result);
    }

}
