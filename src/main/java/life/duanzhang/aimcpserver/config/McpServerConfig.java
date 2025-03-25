package life.duanzhang.aimcpserver.config;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import life.duanzhang.aimcpserver.service.DBService;
import life.duanzhang.aimcpserver.service.FileService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider fileTools(FileService fileService) {
        return MethodToolCallbackProvider.builder().toolObjects(fileService).build();
    }

    @Bean
    public ToolCallbackProvider dbTools(DBService dbService) {
        return MethodToolCallbackProvider.builder().toolObjects(dbService).build();
    }

    @Bean
    public List<McpServerFeatures.SyncPromptRegistration> myPrompts() {
        var prompt = new McpSchema.Prompt("打招呼", "有好的打招呼",
                List.of(new McpSchema.PromptArgument("name", "被打招呼的人", true)));

        var promptRegistration = new McpServerFeatures.SyncPromptRegistration(prompt, getPromptRequest -> {
            String nameArgument = (String) getPromptRequest.arguments().get("name");
            if (nameArgument == null) {
                nameArgument = "朋友";
            }
            var userMessage = new McpSchema.PromptMessage(McpSchema.Role.USER, new McpSchema.TextContent("你好 " + nameArgument + "，有什么需要我为你做的吗"));
            return new McpSchema.GetPromptResult("一个专属打招呼信息", List.of(userMessage));
        });
        return List.of(promptRegistration);
    }
}
