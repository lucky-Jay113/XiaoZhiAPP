package com.yly.xiaozhiapp.controller;

import com.yly.xiaozhiapp.assistant.XiaozhiAgent;
import com.yly.xiaozhiapp.bean.ChatForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @ClassName: XiaozhiController
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/28
 * @Version: 1.0
 */
@Tag(name = "蚂蚁小智")
@RestController
@RequestMapping("/xiaozhi")
public class XiaozhiController {

    @Autowired
    private XiaozhiAgent xiaozhiAgent;

    @Operation(summary = "对话")
    @PostMapping(value = "/chat",produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        return xiaozhiAgent.chat(chatForm.getMemoryId(), chatForm.getMessage());
    }

}
