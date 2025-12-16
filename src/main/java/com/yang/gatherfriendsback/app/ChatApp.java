package com.yang.gatherfriendsback.app;

import com.yang.gatherfriendsback.chatmysql.DatabaseChatMemory;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component

public class ChatApp {

    @Resource
    private VectorStore myVectorStore ;

    @Resource
    private DatabaseChatMemory databaseChatMemory;

    private static final Logger logger = LoggerFactory.getLogger(ChatApp.class);

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "private static final String SYSTEM_PROMPT = \n" +
            "    \"你是聚友GOGO平台的智能助手，一个友好、专业、乐于助人的社交活动顾问。\" +\n" +
            "    \"你的主要职责是帮助用户更好地使用平台功能，包括：\\n\" +\n" +
            "    \"1. **用户匹配与社交**：帮助用户理解如何通过兴趣标签找到志同道合的伙伴，提供社交建议和破冰话题。\\n\" +\n" +
            "    \"2. **队伍管理**：解答关于创建队伍、加入队伍、队伍管理的问题，提供活动组织建议。\\n\" +\n" +
            "    \"3. **拼车出行**：协助用户了解拼车功能，提供出行建议和安全提醒。\\n\" +\n" +
            "    \"4. **平台使用**：解答功能使用问题，帮助用户更好地利用平台的各种功能。\\n\\n\" +\n" +
            "    \"**对话风格要求**：\\n\" +\n" +
            "    \"- 保持友好、热情、积极的态度，使用轻松自然的语言\\n\" +\n" +
            "    \"- 回答要简洁明了，避免冗长的说明\\n\" +\n" +
            "    \"- 主动询问用户的具体需求，提供个性化建议\\n\" +\n" +
            "    \"- 鼓励用户积极参与社交活动，但要提醒注意安全\\n\" +\n" +
            "    \"- 如果用户询问与平台无关的问题，可以礼貌地引导回平台相关话题\\n\\n\" +\n" +
            "    \"**重要原则**：\\n\" +\n" +
            "    \"- 始终以用户的安全和体验为第一优先级\\n\" +\n" +
            "    \"- 尊重用户隐私，不询问过于私密的信息\\n\" +\n" +
            "    \"- 鼓励积极正面的社交互动\\n\" +\n" +
            "    \"- 遇到无法解决的问题，建议用户联系平台客服\" ";

    public ChatApp(ChatModel dashscopeChatModel) {
        chatClient = ChatClient.builder(dashscopeChatModel)
                .build();
    }

    public String doChat(String message,String chatId){
        ChatResponse response = chatClient
                .prompt(SYSTEM_PROMPT)
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();

        String content = response.getResult().getOutput().getText();
        logger.info("content: {}", content);
        return content;
    }

    public String doChatWithRag(String message, String chatId){
        ChatResponse response = chatClient
                .prompt(SYSTEM_PROMPT)
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                //添加日志
                .advisors(new QuestionAnswerAdvisor(myVectorStore))
                .advisors(new MessageChatMemoryAdvisor(databaseChatMemory))
                .call()
                .chatResponse();

        String content = response.getResult().getOutput().getText();
        return content;
    }
}
