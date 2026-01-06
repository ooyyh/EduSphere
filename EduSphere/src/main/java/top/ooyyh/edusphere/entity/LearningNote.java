package top.ooyyh.edusphere.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("learning_note")
public class LearningNote {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer courseId;

    private Integer lessonId;

    private String content;

    private Integer videoTime;

    private Integer isPublic;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 关联数据
    private String username;
    private String lessonTitle;
}
