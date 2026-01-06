package top.ooyyh.edusphere.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CourseCreateRequest {
    private String title;
    private String subtitle;
    private String description;
    private String coverImage;
    private Integer categoryId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Boolean isFree;
    private String level;
    private String duration;
    private Boolean isHot;
    private Boolean isNew;
    private String status;
}
