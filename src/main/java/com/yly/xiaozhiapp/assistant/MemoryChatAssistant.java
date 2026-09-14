package com.yly.xiaozhiapp.assistant;

import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

/**
 * @ClassName: Assistant
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/26
 * @Version: 1.0
 */
@AiService(
        wiringMode = EXPLICIT,
        chatModel = "qwenChatModel",
        chatMemory = "chatMemory")
public interface MerroryChatAssistant {

   String chat(String userMessage);

}
