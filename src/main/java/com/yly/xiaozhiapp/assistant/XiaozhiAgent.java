package com.yly.xiaozhiapp.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

/**
 * @ClassName: XiaozhiAgent
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/28
 * @Version: 1.0
 */
@AiService(
        wiringMode = EXPLICIT,
       // chatModel = "qwenChatModel",
        streamingChatModel = "qwenStreamingChatModel",
        chatMemoryProvider = "chatMemoryProviderXiaozhi",
        tools = "appointmentTools" ,//tools配置
        contentRetriever = "contentRetrieverXiaozhiPincone" //配置向量存储
)
public interface XiaozhiAgent {

    @SystemMessage(fromResource = "xiaozhi-prompt-template.txt")
    Flux<String> chat(@MemoryId Long memoryId, @UserMessage String userMessage);
}
