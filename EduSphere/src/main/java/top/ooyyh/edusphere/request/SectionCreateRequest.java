package top.ooyyh.edusphere.request;

import lombok.Data;

@Data
public class SectionCreateRequest {
    private Integer courseId;
    private String title;
    private String description;
    private Integer sortOrder;
}
