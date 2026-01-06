package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.ooyyh.edusphere.config.FileUploadConfig;
import top.ooyyh.edusphere.service.FileUploadService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    // 允许的图片格式
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    // 允许的视频格式
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm");

    // 允许的文档格式
    private static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "md");

    @Override
    public String uploadImage(MultipartFile file) throws Exception {
        return uploadFile(file, fileUploadConfig.getImagePath(), IMAGE_EXTENSIONS, "图片");
    }

    @Override
    public String uploadVideo(MultipartFile file) throws Exception {
        return uploadFile(file, fileUploadConfig.getVideoPath(), VIDEO_EXTENSIONS, "视频");
    }

    @Override
    public String uploadDocument(MultipartFile file) throws Exception {
        return uploadFile(file, fileUploadConfig.getDocumentPath(), DOCUMENT_EXTENSIONS, "文档");
    }

    @Override
    public void deleteFile(String fileUrl) throws Exception {
        if (fileUrl == null || !fileUrl.startsWith(fileUploadConfig.getUrlPrefix())) {
            throw new IllegalArgumentException("无效的文件URL");
        }

        // 从URL中提取相对路径
        String relativePath = fileUrl.replace(fileUploadConfig.getUrlPrefix(), "");
        String filePath = fileUploadConfig.getUploadPath() + relativePath;

        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new IOException("文件删除失败: " + filePath);
            }
        }
    }

    /**
     * 通用文件上传方法
     */
    private String uploadFile(MultipartFile file, String uploadPath, List<String> allowedExtensions, String fileType) throws Exception {
        // 1. 校验文件
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(fileType + "文件不能为空");
        }

        // 2. 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("不支持的" + fileType + "格式: " + extension + "。支持的格式: " + allowedExtensions);
        }

        // 3. 生成新文件名（使用日期 + UUID避免重名）
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String newFilename = dateStr + "_" + uuid + "." + extension;

        // 4. 创建日期子目录
        String datePath = uploadPath + dateStr + "/";
        File dateDir = new File(datePath);
        if (!dateDir.exists()) {
            dateDir.mkdirs();
        }

        // 5. 保存文件
        String filePath = datePath + newFilename;
        File dest = new File(filePath);
        file.transferTo(dest);

        // 6. 生成访问URL
        String fileType1 = getFileTypeFromPath(uploadPath);
        String relativePath = fileType1 + "/" + dateStr + "/" + newFilename;
        return fileUploadConfig.getUrlPrefix() + relativePath;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        throw new IllegalArgumentException("无法获取文件扩展名");
    }

    /**
     * 从路径中获取文件类型
     */
    private String getFileTypeFromPath(String path) {
        if (path.contains("images")) {
            return "images";
        } else if (path.contains("videos")) {
            return "videos";
        } else if (path.contains("documents")) {
            return "documents";
        }
        return "files";
    }
}
