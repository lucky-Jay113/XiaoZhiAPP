package com.yly.xiaozhiapp;

import com.yly.xiaozhiapp.assistant.PromptAssistant;
import com.yly.xiaozhiapp.assistant.SeparateChatAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName: PromptTest
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/27
 * @Version: 1.0
 */
@SpringBootTest
public class PromptTest {

    @Autowired
    private SeparateChatAssistant separateChatAssistant;
    @Test
    public void testPrompt() {

        String answer = separateChatAssistant.chat(3,"今天几号");
        System.out.println(answer);

    }

    @Test
    public void testPrompt2() {

        String answer = separateChatAssistant.chat2(3,"你是我的好朋友，请用东北话回答问题，回答问题的时候适当添加表情符号。\n" +
                "今天是 {{current_date}}。");
        System.out.println(answer);

    }

}
