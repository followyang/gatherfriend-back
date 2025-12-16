package com.yang.gatherfriendsback.config;

import com.yang.gatherfriendsback.document.MyTextReader;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: VectorStoreConfig
 * @description: TODO
 * @date: 2025/11/29 下午8:09
 */
@Configuration
public class VectorStoreConfig {

    @Resource
    private MyTextReader myTextReader;

    @Bean
    VectorStore myVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 加载文档
        List<Document> documents = myTextReader.loadText();

        // 过滤掉空文档并确保文本长度在有效范围内
        List<Document> validDocuments = documents.stream()
                .filter(doc -> doc.getText() != null &&
                        !doc.getText().trim().isEmpty() &&
                        doc.getText().length() <= 2048)
                .collect(Collectors.toList());

        if (!validDocuments.isEmpty()) {
            simpleVectorStore.add(validDocuments);
        }

        return simpleVectorStore;
    }
}
