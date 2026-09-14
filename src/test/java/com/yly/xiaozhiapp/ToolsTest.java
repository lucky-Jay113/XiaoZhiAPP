package com.yly.xiaozhiapp;

import com.yly.xiaozhiapp.assistant.SeparateChatAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName: ToolsTest
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/28
 * @Version: 1.0
 */
@SpringBootTest
public class ToolsTest {

    @Autowired
    private SeparateChatAssistant separateChatAssistant;

    @Test
    public void testCalculatorTools() {
        String result = separateChatAssistant.chat(1, "1+1=?,475695037565的平方根是多\n" +
                "少？");
        System.out.println(result);
    }

}
