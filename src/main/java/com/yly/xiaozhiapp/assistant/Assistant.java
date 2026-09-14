package com.yly.xiaozhiapp.assistant;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

/**
 * @ClassName: Assistant
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/26
 * @Version: 1.0
 */
@AiService(wiringMode = EXPLICIT,chatModel = "qwenChatModel")
public interface Assistant {

   String chat(String userMessage);

}
