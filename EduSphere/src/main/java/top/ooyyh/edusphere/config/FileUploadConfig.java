package top.ooyyh.edusphere.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * 文件上传配置类
 */
@Configuration
public class FileUploadConfig {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.image-path}")
    private String imagePath;

    @Value("${file.upload.video-path}")
    private String videoPath;

    @Value("${file.upload.document-path}")
    private String documentPath;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    /**
     * 初始化上传目录
     */
    @PostConstruct
    public void init() {
        createDirectoryIfNotExists(uploadPath);
        createDirectoryIfNotExists(imagePath);
        createDirectoryIfNotExists(videoPath);
        createDirectoryIfNotExists(documentPath);
    }

    /**
     * 创建目录（如果不存在）
     */
    private void createDirectoryIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("创建目录: " + path);
            }
        }
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }
}
