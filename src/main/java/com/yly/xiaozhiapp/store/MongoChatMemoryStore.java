package com.yly.xiaozhiapp.store;

import com.yly.xiaozhiapp.bean.ChatMessages;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: MongoChatMemoryStore
 * @Author: YLY
 * @Description: 对聊天记录的持久化类
 * @Date: 2026/2/28
 * @Version: 1.0
 */
@Component // 将类注册为Spring Bean
public class MongoChatMemoryStore implements ChatMemoryStore {



    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 获取聊天记录
     * @param memoryId The ID of the chat memory.
     * @return
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);

        //这个ChatMessages 类是自定义的，用于存储聊天记录
        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);
        if (chatMessages == null) {
            return new LinkedList<>();
        }
        String content = chatMessages.getContent();
        //将JSON字符串转换为List<ChatMessage>
        return ChatMessageDeserializer.messagesFromJson(content);
    }

    /**
     * 更新聊天记录
     * @param memoryId The ID of the chat memory.
     * @param messagesList
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messagesList) {

        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        Update update = new Update();
        // 将List<ChatMessage>转换为JSON字符串
        String messagesToJson = ChatMessageSerializer.messagesToJson(messagesList);
        Update set = update.set("content",messagesToJson);

        //修改或新增文档
        mongoTemplate.upsert(query, set, ChatMessages.class);
    }

    /**
     * 删除聊天记录
     * @param memoryId The ID of the chat memory.
     */
    @Override
    public void deleteMessages(Object memoryId) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, ChatMessages.class);
    }
}
