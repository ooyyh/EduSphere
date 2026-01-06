package top.ooyyh.edusphere.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.ooyyh.edusphere.entity.LearningNote;

import java.util.List;

@Mapper
public interface LearningNoteMapper extends BaseMapper<LearningNote> {

    /**
     * 获取用户所有笔记
     */
    List<LearningNote> getUserNotes(@Param("userId") Integer userId);

    /**
     * 获取课程的笔记
     */
    List<LearningNote> getCourseNotes(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    /**
     * 获取课时的笔记
     */
    List<LearningNote> getLessonNotes(@Param("lessonId") Integer lessonId, @Param("userId") Integer userId);

    /**
     * 创建笔记
     */
    int createNote(LearningNote note);

    /**
     * 更新笔记
     */
    int updateNote(LearningNote note);

    /**
     * 删除笔记
     */
    int deleteNote(@Param("noteId") Integer noteId, @Param("userId") Integer userId);

    /**
     * 根据ID获取笔记
     */
    LearningNote getNoteById(@Param("noteId") Integer noteId);

    /**
     * 获取课程公开笔记
     */
    List<LearningNote> getPublicNotes(@Param("courseId") Integer courseId, @Param("limit") Integer limit);
}
