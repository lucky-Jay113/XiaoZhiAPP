# XiaoZhiAPP（蚂蚁小智）

基于 **Spring Boot 3 + LangChain4j** 构建的 AI 智能客服应用，模拟「北京协和医院」智能客服与医疗伴诊助手。

用户通过自然语言与 AI 对话，即可完成**智能分导诊**、**号源查询**、**预约挂号**、**取消挂号**等就医流程操作；同时接入 RAG 知识库，让 AI 基于医院科室、医生等本地知识回答问询。

## 功能特性

- 🤖 **多模型接入**：基于 LangChain4j 统一接入多种大模型
  - 阿里云百炼 DashScope（OpenAI 兼容模式）：`deepseek-v3.2`
  - 百炼 DashScope 社区模型：`qwen-max` / `qwen-plus`（流式）
  - 本地 Ollama：`deepseek-r1:1.5b`
- 💬 **流式对话**：基于 WebFlux + `Flux<String>` 实现 SSE 流式输出（`text/stream`）
- 🧠 **多轮会话记忆**：基于 `MessageWindowChatMemory`，会话记录通过自研 `MongoChatMemoryStore` 持久化到 MongoDB，按 `memoryId` 隔离会话
- 📚 **RAG 知识库**：本地知识文档（科室信息、医院信息等 Markdown / PDF / TXT）经 `text-embedding-v3` 向量化，存入 Pinecone 向量库，回答时按相似度检索（minScore ≥ 0.8，最多 1 条）
- 🛠️ **AI 工具调用（Function Calling）**
  - 预约挂号 / 取消预约挂号 / 查询号源（MyBatis-Plus + MySQL）
  - 加法、平方根等计算工具（示例）
- 📖 **接口文档**：Knife4j（OpenAPI 3，Jakarta）在线调试

## 技术栈

| 分类 | 技术 |
| ---- | ---- |
| 基础框架 | Spring Boot 3.5.11、Java 17、Maven |
| AI 框架 | LangChain4j 1.11.0-beta19（open-ai / ollama / dashscope / easy-rag / pinecone / reactor） |
| 数据存储 | MySQL（预约数据，MyBatis-Plus 3.5.14）、MongoDB（会话记忆） |
| 向量检索 | Pinecone（Serverless，AWS us-east-1） |
| 接口文档 | Knife4j 4.3.0（knife4j-openapi3-jakarta） |
| 响应式流 | Spring WebFlux |

## 项目结构

```
XiaoZhiAPP
├── src/main/java/com/yly/xiaozhiapp
│   ├── XiaoZhiAppApplication.java      # 启动类
│   ├── assistant/                      # AI 服务接口（@AiService）
│   │   ├── XiaozhiAgent.java           # 主智能体（流式 + 记忆 + 工具 + RAG）
│   │   ├── Assistant.java              # 基础对话
│   │   ├── MemoryChatAssistant.java    # 带记忆对话
│   │   ├── PromptAssistant.java        # 提示词模板对话
│   │   └── SeparateChatAssistant.java  # 独立会话 + 计算工具
│   ├── controller/                     # REST 接口
│   │   └── XiaozhiController.java      # POST /xiaozhi/chat（流式）
│   ├── config/                         # Bean 配置（向量存储、记忆、检索器）
│   ├── entity/                         # 实体（Appointment）
│   ├── mapper/                         # MyBatis-Plus Mapper
│   ├── service/                        # 业务层（预约挂号）
│   ├── store/                          # 会话记忆持久化（MongoDB）
│   ├── tools/                          # AI 工具（预约、计算）
│   └── bean/                           # 请求参数（ChatForm、ChatMessages）
├── src/main/resources
│   ├── application.properties          # 配置文件
│   ├── xiaozhi-prompt-template.txt     # 小智系统提示词
│   ├── my-prompt-template.txt          # 示例提示词
│   ├── knowledge/                      # RAG 知识库文档
│   └── mapper/                         # Mapper XML
└── pom.xml
```

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.x（预约挂号表）
- MongoDB（会话记忆库，默认本地 27017）
- Pinecone 账号（向量库）
- 阿里云百炼（DashScope）API Key（可选：本地 Ollama 可替代）

## 快速开始

### 1. 配置环境变量

项目从环境变量读取密钥，请提前配置：

| 变量 | 说明 |
| ---- | ---- |
| `DASHSCOPE_API_KEY` | 阿里云百炼平台 API Key（对话模型 + 向量模型） |
| `PINECONE_API_KEY` | Pinecone API Key（向量库） |

### 2. 初始化 MySQL

创建数据库与预约表（表名与实体 `Appointment` 对应）：

```sql
CREATE DATABASE IF NOT EXISTS guiguxiaozhi DEFAULT CHARACTER SET utf8mb4;

USE guiguxiaozhi;

CREATE TABLE IF NOT EXISTS appointment (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(64)  DEFAULT NULL COMMENT '姓名',
    id_card     VARCHAR(32)  DEFAULT NULL COMMENT '身份证号',
    department  VARCHAR(64)  DEFAULT NULL COMMENT '预约科室',
    date        VARCHAR(32)  DEFAULT NULL COMMENT '预约日期',
    time        VARCHAR(16)  DEFAULT NULL COMMENT '预约时间（上午/下午）',
    doctor_name VARCHAR(64)  DEFAULT NULL COMMENT '医生姓名',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '预约挂号记录';
```

### 3. 修改配置

按需修改 `src/main/resources/application.properties` 中的数据库连接、模型名称等：

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/guiguxiaozhi?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
spring.datasource.username=root
spring.datasource.password=123456

# MongoDB（会话记忆）
spring.data.mongodb.uri=mongodb://localhost:27017/chat_memory_db
```

### 4. 启动

```bash
mvn spring-boot:run
```

或打包后运行：

```bash
mvn clean package
java -jar target/XiaoZhiAPP-0.0.1-SNAPSHOT.jar
```

启动成功后：

- 服务地址：`http://localhost:8080`
- 接口文档：`http://localhost:8080/doc.html`

## 接口说明

### 对话（流式）

```
POST /xiaozhi/chat
Content-Type: application/json
Accept: text/stream
```

请求体：

```json
{
  "memoryId": 1,
  "message": "我想挂神经内科的号"
}
```

| 字段 | 类型 | 说明 |
| ---- | ---- | ---- |
| `memoryId` | Long | 会话 ID，用于区分多轮会话与记忆 |
| `message` | String | 用户输入的问题 |

响应：`Flux<String>` 流式返回 AI 回复内容（SSE 文本流）。

> 预约/取消预约时，请按提示提供姓名（必选）、身份证号（必选）、科室（必选）、日期（必选，如 2025-04-14）、时间（必选，上午/下午）、医生（可选）。

## 测试

项目包含多种单元测试，覆盖 AI 服务、会话记忆、MongoDB CRUD、RAG、工具调用等场景：

```bash
mvn test
```

## 常见问题

- **接口报错 401 / 鉴权失败**：检查 `DASHSCOPE_API_KEY` 与 `PINECONE_API_KEY` 环境变量是否配置。
- **不使用 Pinecone**：可将 `XiaozhiAgentConfig` 中注入的 `contentRetrieverXiaozhiPincone` 替换为被注释的本地 `InMemoryEmbeddingStore` 实现（依赖本地知识文件）。
- **知识库更新**：将新的科室/医生资料放入 `src/main/resources/knowledge/`，并在入库逻辑中加载对应文档。

## License

内部学习/演示项目，仅作交流使用。
