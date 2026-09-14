package com.yly.xiaozhiapp;

import com.yly.xiaozhiapp.assistant.Assistant;
import com.yly.xiaozhiapp.assistant.MemoryChatAssistant;
import com.yly.xiaozhiapp.assistant.SeparateChatAssistant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName: ChatMemoryTest
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/27
 * @Version: 1.0
 */
@SpringBootTest
public class ChatMemoryTest {


    @Autowired
    private QwenChatModel qwenChatModel;

    MessageWindowChatMemory chatMemory =  MessageWindowChatMemory.withMaxMessages(10);
    @Test
    public void testChatMemory() {
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(qwenChatModel)
                .chatMemory(chatMemory)
                .build();

        String answer = assistant.chat("我是yly");
        System.out.println(answer);
        answer = assistant.chat("我是谁");
        System.out.println(answer);
    }


    @Autowired
    private MemoryChatAssistant memoryChatAssistant;
    @Test
    public void testChatMemory2() {

        String answer = memoryChatAssistant.chat("我是lucky");
        System.out.println(answer);
        answer = memoryChatAssistant.chat("我是谁");
        System.out.println(answer);

    }

    @Autowired
    private SeparateChatAssistant separateChatAssistant;
    @Test
    public void testChatMemory3() {

        String answer = separateChatAssistant.chat(1,"我是lucky");
        System.out.println(answer);
        answer = separateChatAssistant.chat(1,"我是谁");
        System.out.println(answer);

        answer = separateChatAssistant.chat(2,"我是谁");
        System.out.println(answer);

    }

    @Test
    public void testChatMemory4() {

        String answer = separateChatAssistant.chat(3,"今天几号");
        System.out.println(answer);

    }

    @Test
    public void testChatMemory5() {

        String answer = separateChatAssistant.chat2(3,"你是我的好朋友，请用东北话回答问题，回答问题的时候适当添加表情符号。\n" +
                "今天是 {{current_date}}。");
        System.out.println(answer);

    }

    //chat3
    @Test
    public void testChatMemory6() {

        String answer = separateChatAssistant.chat3(4,"我是lucky");
        System.out.println(answer);
        answer = separateChatAssistant.chat3(4,"我是谁");
        System.out.println(answer);

        answer = separateChatAssistant.chat3(5,"我是谁");
        System.out.println(answer);

    }


}
