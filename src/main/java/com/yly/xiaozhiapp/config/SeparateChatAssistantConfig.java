package com.yly.xiaozhiapp.config;

import com.yly.xiaozhiapp.store.MongoChatMemoryStore;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: MemoryChatAssistantConfig
 * @Author: YLY
 * @Description: 创建一个MemoryChatAssistantConfig类，用于创建一个ChatMemoryProvider对象，并注册到Spring容器中。
 * @Date: 2026/2/27
 * @Version: 1.0
 */
@Configuration
public class SeparateChatAssistantConfig {

    /**必须注入MongoChatMemoryStore对象到Spring容器管理，用于存储聊天记录
     * 从而能注入MongoTemplate
     */
    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        return messageId -> MessageWindowChatMemory.builder()
                .id(messageId)
                .maxMessages(10)
                .chatMemoryStore(mongoChatMemoryStore)
                .build();
    }
}
