package com.yly.xiaozhiapp;

import com.yly.xiaozhiapp.bean.ChatMessages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @ClassName: MongoCrudTest
 * @Author: YLY
 * @Description:
 * @Date: 2026/2/27
 * @Version: 1.0
 */
@SpringBootTest
public class MongoCrudTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
      插入文档
     */
    @Test
    public void testInsert(){
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.setContent("hello world");
        mongoTemplate.insert(chatMessages);
    }

    /**
     * 根据id查询文档
     */
    @Test
    public void testFindById() {
        ChatMessages chatMessages = mongoTemplate.findById("69a1aaef034e0ddb3f309d2e",
                ChatMessages.class);
        System.out.println(chatMessages);
    }

    /**
     * 修改文档
     */
    @Test
    public void testUpdate(){

        Criteria criteria = Criteria.where("_id").is("69a1aaef034e0ddb3f309d2e");
        Query query = new Query(criteria);
        Update update = new Update();
        Update set = update.set("content", "新新的聊天记录列表");

        //修改或新增文档
        mongoTemplate.upsert(query, set, ChatMessages.class);
    }

    /**
     * 删除文档
     */
    @Test
    public void testDelete(){
        Criteria criteria = Criteria.where("_id").is("100");
        Query query = new Query(criteria);
        mongoTemplate.remove(query, ChatMessages.class);
    }




}
