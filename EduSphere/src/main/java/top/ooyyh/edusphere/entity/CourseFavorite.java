package top.ooyyh.edusphere.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_favorite")
public class CourseFavorite {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer courseId;

    private LocalDateTime createdAt;

    // 关联数据
    private Course course;
}
