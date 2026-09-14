package com.yly.xiaozhiapp;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import com.alibaba.dashscope.tokenizers.QwenTokenizer;
import dev.langchain4j.community.model.dashscope.QwenTokenCountEstimator;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;

import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenCountEstimator;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: RAGTest
 * @Author: YLY
 * @Description:
 * @Date: 2026/3/2
 * @Version: 1.0
 */
@SpringBootTest
public class RAGTest {

    /**
     * 解析PDF
     */
    @Test
    public void testParsePDF() {
        Document document = FileSystemDocumentLoader.loadDocument(
                "src/main/resources/knowledge/医院信息.pdf",
                new ApachePdfBoxDocumentParser()
        );
        System.out.println(document);
    }

    /**
     * 加载文档并存入向量数据库
     */
    @Test
    public void testReadDocumentAndStore() {
        // 使用FileSystemDocumentLoader读取指定目录下的知识库文档
        // 并使用默认的文档解析器对文档进行解析(TextDocumentParser)
        Document document = FileSystemDocumentLoader.loadDocument("src/main/resources/knowledge/人工智能.md");

        // 为了简单起见，我们暂时使用基于内存的向量存储
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // ingest过程：
        // 1、分割文档：默认使用递归分割器，将文档分割为多个文本片段，
        //    每个片段包含不超过300个token，并且有30个token的重叠部分保证连贯性
        //    DocumentByParagraphSplitter(DocumentByLineSplitter(DocumentBySentenceSplitter(DocumentByWordSplitter)))
        // 2、文本向量化：使用一个LangChain4j内置的轻量化向量模型对每个文本片段进行向量化
        // 3、将原始文本和向量存储到向量数据库中(InMemoryEmbeddingStore)
        EmbeddingStoreIngestor.ingest(document, embeddingStore);

        // 查看向量数据库内容
        System.out.println(embeddingStore);
    }



    /**
     * 文档分割
     */
        @Test
    public void testDocumentSplitter() {
        // 使用 FileSystemDocumentLoader 读取指定目录下的知识库文档
        // 并使用默认的文档解析器对文档进行解析 (TextDocumentParser)
        Document document = FileSystemDocumentLoader.loadDocument("src/main/resources/knowledge/人工智能.md");

        // 为了简单起见，我们暂时使用基于内存的向量存储
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 自定义文档分割器
        // 按段落分割文档：每个片段包含不超过 300 个 token，并且有 30 个 token 的重叠部分保证连贯性
        // 注意：当段落长度总和小于设定的最大长度时，就不会有重叠的必要。

        // 方式一：使用 HuggingFaceTokenizer 按 token 计算
        DocumentByParagraphSplitter documentSplitter = new DocumentByParagraphSplitter(
                300,
                30,
                new HuggingFaceTokenCountEstimator());

        // 方式二：按字符计算（备选方案）
        // DocumentByParagraphSplitter documentSplitter = new DocumentByParagraphSplitter(300, 30);

        EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .documentSplitter(documentSplitter)
                .build()
                .ingest(document);
    }

    @Test
    public void testTokenCount() {
        String text = "这是一个示例文本，用于测试 token 长度的计算。";
        UserMessage userMessage = UserMessage.userMessage(text);

        // 计算 token 长度 - 使用 QwenTokenizer（需要配置 API KEY）
        QwenTokenCountEstimator tokenizer = new QwenTokenCountEstimator(System.getenv("DASHSCOPE_API_KEY"), "qwen-max");
        //HuggingFaceTokenCountEstimator tokenizer = new HuggingFaceTokenCountEstimator();
        int count = tokenizer.estimateTokenCountInMessage(userMessage);

        System.out.println("token长度：" + count);    }

    @Autowired
    private EmbeddingStore embeddingStore;
    @Autowired
    private EmbeddingModel embeddingModel;
    /**
     * 将文本转换成向量，然后存储到pinecone中
     *
     * 参考：
     * https://docs.langchain4j.dev/tutorials/embedding-stores
     */
    @Test
    public void testPineconeEmbeded() {
        //将文本转换成向量
        TextSegment segment1 = TextSegment.from("我喜欢羽毛球");
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        //存入向量数据库
        embeddingStore.add(embedding1, segment1);
        TextSegment segment2 = TextSegment.from("今天天气很好");
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);
    }

    /**
     * Pinecone-相似度匹配
     */
    @Test
    public void embeddingSearch() {
        //提问，并将问题转成向量数据
        Embedding queryEmbedding = embeddingModel.embed("你最喜欢的运动是什么？").content();
        //创建搜索请求对象
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1) //匹配最相似的一条记录
                //.minScore(0.8)
                .build();
        //根据搜索请求 searchRequest 在向量存储中进行相似度搜索
        EmbeddingSearchResult<TextSegment> searchResult =  embeddingStore.search(searchRequest);

        //searchResult.matches()：获取搜索结果中的匹配项列表。
        //.get(0)：从匹配项列表中获取第一个匹配项
        EmbeddingMatch<TextSegment> embeddingMatch = searchResult.matches().get(0);

        //获取匹配项的相似度得分
        System.out.println(embeddingMatch.score()); // 0.8144288515898701
        //返回文本结果
        System.out.println(embeddingMatch.embedded().text());
    }

    /**
     * 上传知识库文档到向量数据库
     */
    @Test
    public void testUploadKnowledgeLibrary() {
        // 使用 FileSystemDocumentLoader 读取指定目录下的知识库文档
        // 并使用默认的文档解析器对文档进行解析
        Document document1 = FileSystemDocumentLoader.loadDocument("src/main/resources/knowledge/医院信息.md");
        Document document2 = FileSystemDocumentLoader.loadDocument("src/main/resources/knowledge/科室信息.md");
        Document document3 = FileSystemDocumentLoader.loadDocument("src/main/resources/knowledge/神经内科.md");

        List<Document> documents = Arrays.asList(document1, document2, document3);

        // 文本向量化并存入向量数据库：将每个片段进行向量化，得到一个嵌入向量
        EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build()
                .ingest(documents);
    }
}
