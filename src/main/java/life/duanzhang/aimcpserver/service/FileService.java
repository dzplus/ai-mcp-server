package life.duanzhang.aimcpserver.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileService {

    @Tool(description = "获取电脑上的所有文件信息，返回文件名和文件路径")
    public Map<String,String> getAllFiles() {
        // 获取用户主目录
        String userHome = System.getProperty("user.home");
        return getFilesByPath(userHome);
    }

    @Tool(description = "获取指定路径里面的所有文件信息，返回文件名和文件路径")
    public Map<String,String> getFilesByPath(String path) {
        Map<String,String> fileInfo = new HashMap<>();
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    fileInfo.put(file.getName(), file.getAbsolutePath());
                }
            }
        }
        return fileInfo;
    }
}
