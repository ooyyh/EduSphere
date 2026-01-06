package top.ooyyh.edusphere.request;

import lombok.Data;

@Data
public class CourseSearchRequest {
    private String keyword;
    private String category;
    private String level;
    private String priceRange;
    private String sortBy;
    private Integer pageNum = 1;
    private Integer pageSize = 12;
    
    // 计算offset
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
