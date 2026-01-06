package top.ooyyh.edusphere.request;

import lombok.Data;

@Data
public class LessonCreateRequest {
    private Integer sectionId;
    private String title;
    private String description;
    private String type; // video, document, quiz
    private String content; // 视频URL或文档内容
    private String duration;
    private Boolean isFree;
    private Integer sortOrder;
}
