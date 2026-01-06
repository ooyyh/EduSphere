package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ooyyh.edusphere.entity.LearningNote;
import top.ooyyh.edusphere.mapper.LearningNoteMapper;
import top.ooyyh.edusphere.service.LearningNoteService;
import top.ooyyh.edusphere.utils.Result;

import java.util.List;

@Service
public class LearningNoteServiceImpl implements LearningNoteService {

    @Autowired
    private LearningNoteMapper noteMapper;

    @Override
    public Result getUserNotes(Integer userId) {
        try {
            List<LearningNote> notes = noteMapper.getUserNotes(userId);
            return Result.success(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取笔记失败: " + e.getMessage());
        }
    }

    @Override
    public Result getCourseNotes(Integer courseId, Integer userId) {
        try {
            List<LearningNote> notes = noteMapper.getCourseNotes(courseId, userId);
            return Result.success(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取课程笔记失败: " + e.getMessage());
        }
    }

    @Override
    public Result getLessonNotes(Integer lessonId, Integer userId) {
        try {
            List<LearningNote> notes = noteMapper.getLessonNotes(lessonId, userId);
            return Result.success(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取课时笔记失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result createNote(LearningNote note) {
        try {
            if (note.getContent() == null || note.getContent().trim().isEmpty()) {
                return Result.error("笔记内容不能为空");
            }

            noteMapper.createNote(note);
            return Result.success("笔记创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建笔记失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result updateNote(LearningNote note) {
        try {
            if (note.getId() == null) {
                return Result.error("笔记ID不能为空");
            }

            if (note.getContent() == null || note.getContent().trim().isEmpty()) {
                return Result.error("笔记内容不能为空");
            }

            // 检查笔记是否存在
            LearningNote existingNote = noteMapper.getNoteById(note.getId());
            if (existingNote == null) {
                return Result.error("笔记不存在");
            }

            // 检查是否是笔记的所有者
            if (!existingNote.getUserId().equals(note.getUserId())) {
                return Result.error("无权修改此笔记");
            }

            noteMapper.updateNote(note);
            return Result.success("笔记更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新笔记失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result deleteNote(Integer noteId, Integer userId) {
        try {
            // 检查笔记是否存在
            LearningNote note = noteMapper.getNoteById(noteId);
            if (note == null) {
                return Result.error("笔记不存在");
            }

            // 检查是否是笔记的所有者
            if (!note.getUserId().equals(userId)) {
                return Result.error("无权删除此笔记");
            }

            noteMapper.deleteNote(noteId, userId);
            return Result.success("笔记删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除笔记失败: " + e.getMessage());
        }
    }

    @Override
    public Result getPublicNotes(Integer courseId, Integer limit) {
        try {
            List<LearningNote> notes = noteMapper.getPublicNotes(courseId, limit);
            return Result.success(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取公开笔记失败: " + e.getMessage());
        }
    }
}
