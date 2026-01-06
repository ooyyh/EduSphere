package top.ooyyh.edusphere.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("learning_progress")
public class LearningProgress {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private Integer courseId;
    
    private Integer lessonId;
    
    private Integer progress;
    
    private Boolean completed;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}