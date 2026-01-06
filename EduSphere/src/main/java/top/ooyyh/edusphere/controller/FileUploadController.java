package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.ooyyh.edusphere.service.FileUploadService;
import top.ooyyh.edusphere.utils.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadImage(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success("图片上传成功", data);
        } catch (Exception e) {
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传视频
     */
    @PostMapping("/video")
    public Result uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadVideo(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success("视频上传成功", data);
        } catch (Exception e) {
            return Result.error("视频上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文档
     */
    @PostMapping("/document")
    public Result uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadDocument(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success("文档上传成功", data);
        } catch (Exception e) {
            return Result.error("文档上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/file")
    public Result deleteFile(@RequestParam("url") String fileUrl) {
        try {
            fileUploadService.deleteFile(fileUrl);
            return Result.success("文件删除成功");
        } catch (Exception e) {
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }
}
