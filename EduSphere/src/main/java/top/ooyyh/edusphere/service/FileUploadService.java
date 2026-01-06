package top.ooyyh.edusphere.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {

    /**
     * 上传图片
     * @param file 图片文件
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file) throws Exception;

    /**
     * 上传视频
     * @param file 视频文件
     * @return 视频访问URL
     */
    String uploadVideo(MultipartFile file) throws Exception;

    /**
     * 上传文档
     * @param file 文档文件
     * @return 文档访问URL
     */
    String uploadDocument(MultipartFile file) throws Exception;

    /**
     * 删除文件
     * @param fileUrl 文件URL
     */
    void deleteFile(String fileUrl) throws Exception;
}
