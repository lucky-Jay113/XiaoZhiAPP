package com.yly.xiaozhiapp.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassName: ChatMessages
 * @Author: YLY
 * @Description: 聊天内容对象
 * @Date: 2026/2/27
 * @Version: 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("chat_Messages")
public class ChatMessages {

    //创建唯一标识。映射到MongoDB文档的_id字段
    @Id
    private ObjectId id;

    //聊天记录的id
    private String memoryId;
    //聊天内容 将聊天记录转换成JSON字符串
    private String content;

}
