# 基于 ARM 架构的 OpenJDK 17 基础镜像
FROM arm64v8/openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 将打包好的 Spring Boot JAR 文件复制到容器中
COPY target/ai-mcp-server-0.0.1-SNAPSHOT.jar app.jar

# 暴露 Spring Boot 应用默认的端口（假设是 8080）
EXPOSE 8888

# 运行 Spring Boot 应用
CMD ["java", "-jar", "app.jar"]    