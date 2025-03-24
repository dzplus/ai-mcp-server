package life.duanzhang.aimcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiMcpServerApplication {
    /**
     * 启动服务后
     * npx @modelcontextprotocol/inspector
     * 通过 mcp Inspector 访问 http://localhost:8080/sse 验证连通性
     * 文档参考 https://modelcontextprotocol.io/docs/tools/inspector
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(AiMcpServerApplication.class, args);
    }

}
