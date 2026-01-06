package top.ooyyh.edusphere.response;

import lombok.Data;
import top.ooyyh.edusphere.entity.Course;

import java.util.List;

@Data
public class CourseSearchResponse {
    private List<Course> courses;
    private Integer total;
    private Integer pageNum;
    private Integer pageSize;
    
    public CourseSearchResponse(List<Course> courses, Integer total, Integer pageNum, Integer pageSize) {
        this.courses = courses;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
