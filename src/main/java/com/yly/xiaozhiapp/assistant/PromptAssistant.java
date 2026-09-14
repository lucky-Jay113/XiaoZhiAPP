package com.yly.xiaozhiapp.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

/**
 * @ClassName: PromptAssistant
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/27
 * @Version: 1.0
 */
@AiService(
        wiringMode = EXPLICIT,
        chatModel = "qwenChatModel",
        chatMemory = "chatMemory",
        chatMemoryProvider = "chatMemoryProvider")
public interface PromptAssistant {

    @SystemMessage("你是我的好朋友，请用东北话回答问题。今天是{{current_date}}")//系统消息提示词
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    @SystemMessage(fromResource = "my-prompt-template.txt")
    String chat2(@MemoryId int memoryId, @UserMessage String userMessage);

}
