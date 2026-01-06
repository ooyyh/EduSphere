package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.LearningNote;
import top.ooyyh.edusphere.utils.Result;

public interface LearningNoteService {

    /**
     * 获取用户所有笔记
     */
    Result getUserNotes(Integer userId);

    /**
     * 获取课程的笔记
     */
    Result getCourseNotes(Integer courseId, Integer userId);

    /**
     * 获取课时的笔记
     */
    Result getLessonNotes(Integer lessonId, Integer userId);

    /**
     * 创建笔记
     */
    Result createNote(LearningNote note);

    /**
     * 更新笔记
     */
    Result updateNote(LearningNote note);

    /**
     * 删除笔记
     */
    Result deleteNote(Integer noteId, Integer userId);

    /**
     * 获取课程公开笔记
     */
    Result getPublicNotes(Integer courseId, Integer limit);
}
