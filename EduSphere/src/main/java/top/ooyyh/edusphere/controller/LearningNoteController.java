package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.LearningNote;
import top.ooyyh.edusphere.service.LearningNoteService;
import top.ooyyh.edusphere.utils.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * 学习笔记控制器
 */
@RestController
@RequestMapping("/notes")
@CrossOrigin
public class LearningNoteController {

    @Autowired
    private LearningNoteService noteService;

    /**
     * 获取当前用户ID
     */
    private Integer getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            return (Integer) userIdObj;
        }
        return -1;
    }

    /**
     * 获取用户所有笔记
     */
    @GetMapping("/my")
    public Result getUserNotes(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return noteService.getUserNotes(userId);
    }

    /**
     * 获取课程的笔记
     */
    @GetMapping("/course/{courseId}")
    public Result getCourseNotes(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return noteService.getCourseNotes(courseId, userId);
    }

    /**
     * 获取课时的笔记
     */
    @GetMapping("/lesson/{lessonId}")
    public Result getLessonNotes(@PathVariable Integer lessonId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return noteService.getLessonNotes(lessonId, userId);
    }

    /**
     * 创建笔记
     */
    @PostMapping("/create")
    public Result createNote(@RequestBody LearningNote note, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        note.setUserId(userId);
        return noteService.createNote(note);
    }

    /**
     * 更新笔记
     */
    @PutMapping("/update")
    public Result updateNote(@RequestBody LearningNote note, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        note.setUserId(userId);
        return noteService.updateNote(note);
    }

    /**
     * 删除笔记
     */
    @DeleteMapping("/delete/{noteId}")
    public Result deleteNote(@PathVariable Integer noteId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return noteService.deleteNote(noteId, userId);
    }

    /**
     * 获取课程公开笔记
     */
    @GetMapping("/public/{courseId}")
    public Result getPublicNotes(@PathVariable Integer courseId, @RequestParam(defaultValue = "20") Integer limit) {
        return noteService.getPublicNotes(courseId, limit);
    }
}
