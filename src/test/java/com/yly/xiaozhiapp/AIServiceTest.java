package com.yly.xiaozhiapp;

import com.yly.xiaozhiapp.assistant.Assistant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName: AIServiceTest
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/26
 * @Version: 1.0
 */
@SpringBootTest
public class AIServiceTest {

    //创建AIService
    @Autowired
    private QwenChatModel qwenChatModel;

    @Test
    public void testChat() {
        //创建AIService
        Assistant assistant = AiServices.create(Assistant.class, qwenChatModel);
        //调用service的接口
        String answer = assistant.chat("Hello");
        System.out.println(answer);
    }


    @Autowired
    private Assistant assistant;

    @Test
    public void testChat2() {
        String answer = assistant.chat("Hello");
        System.out.println(answer);
    }




}
