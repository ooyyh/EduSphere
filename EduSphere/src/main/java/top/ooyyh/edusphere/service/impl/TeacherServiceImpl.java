package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.CourseSection;
import top.ooyyh.edusphere.entity.CourseLesson;
import top.ooyyh.edusphere.mapper.CourseMapper;
import top.ooyyh.edusphere.mapper.CourseSectionMapper;
import top.ooyyh.edusphere.mapper.CourseLessonMapper;
import top.ooyyh.edusphere.request.CourseCreateRequest;
import top.ooyyh.edusphere.request.SectionCreateRequest;
import top.ooyyh.edusphere.request.LessonCreateRequest;
import top.ooyyh.edusphere.service.TeacherService;
import top.ooyyh.edusphere.utils.Result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl implements TeacherService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private CourseSectionMapper courseSectionMapper;
    
    @Autowired
    private CourseLessonMapper courseLessonMapper;
    
    @Override
    public Result<List<Course>> getTeacherCourses(Integer teacherId) {
        try {
            List<Course> courses = courseMapper.getCoursesByInstructor(teacherId);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error("获取教师课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Course> createCourse(CourseCreateRequest request, Integer teacherId) {
        try {
            Course course = new Course();
            course.setTitle(request.getTitle());
            course.setSubtitle(request.getSubtitle());
            course.setDescription(request.getDescription());
            course.setCoverImage(request.getCoverImage());
            course.setInstructorId(teacherId); // 使用传入的教师ID
            course.setCategoryId(request.getCategoryId());
            course.setPrice(request.getPrice());
            course.setOriginalPrice(request.getOriginalPrice());
            course.setIsFree(request.getIsFree());
            course.setLevel(request.getLevel());
            course.setDuration(request.getDuration());
            course.setStudentCount(0);
            course.setRating(BigDecimal.valueOf(0.0));
            course.setRatingCount(0);
            course.setIsHot(request.getIsHot() != null ? request.getIsHot() : false);
            course.setIsNew(request.getIsNew() != null ? request.getIsNew() : true);
            course.setStatus(request.getStatus() != null ? request.getStatus() : "published"); // 默认直接发布
            course.setCreatedAt(LocalDateTime.now());
            course.setUpdatedAt(LocalDateTime.now());
            
            courseMapper.insertCourse(course);
            return Result.success(course);
        } catch (Exception e) {
            return Result.error("创建课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Course> updateCourse(Integer courseId, CourseCreateRequest request, Integer teacherId) {
        try {
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            // 验证课程是否属于当前教师
            if (!course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限操作此课程");
            }
            
            course.setTitle(request.getTitle());
            course.setSubtitle(request.getSubtitle());
            course.setDescription(request.getDescription());
            course.setCoverImage(request.getCoverImage());
            course.setCategoryId(request.getCategoryId());
            course.setPrice(request.getPrice());
            course.setOriginalPrice(request.getOriginalPrice());
            course.setIsFree(request.getIsFree());
            course.setLevel(request.getLevel());
            course.setDuration(request.getDuration());
            course.setIsHot(request.getIsHot());
            course.setIsNew(request.getIsNew());
            course.setUpdatedAt(LocalDateTime.now());
            
            courseMapper.updateCourse(course);
            return Result.success(course);
        } catch (Exception e) {
            return Result.error("更新课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> deleteCourse(Integer courseId, Integer teacherId) {
        try {
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            // 验证课程是否属于当前教师
            if (!course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限操作此课程");
            }
            
            courseMapper.deleteCourse(courseId);
            return Result.success("删除课程成功");
        } catch (Exception e) {
            return Result.error("删除课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> publishCourse(Integer courseId, Integer teacherId) {
        try {
            System.out.println("发布课程 - 课程ID: " + courseId + ", 教师ID: " + teacherId);
            
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                System.out.println("课程不存在: " + courseId);
                return Result.error("课程不存在");
            }
            
            System.out.println("找到课程: " + course.getTitle() + ", 当前状态: " + course.getStatus());
            System.out.println("课程讲师ID: " + course.getInstructorId() + ", 请求教师ID: " + teacherId);
            
            // 验证课程是否属于当前教师
            if (!course.getInstructorId().equals(teacherId)) {
                System.out.println("权限验证失败: 课程不属于当前教师");
                return Result.error("无权限操作此课程");
            }
            
            course.setStatus("pending"); // 提交审核
            course.setUpdatedAt(LocalDateTime.now());
            System.out.println("准备更新课程状态为: pending");
            
            courseMapper.updateCourse(course);
            System.out.println("数据库更新完成");
            
            return Result.success("课程已提交审核，等待管理员审核");
        } catch (Exception e) {
            System.out.println("发布课程异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("发布课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> unpublishCourse(Integer courseId, Integer teacherId) {
        try {
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            // 验证课程是否属于当前教师
            if (!course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限操作此课程");
            }
            
            course.setStatus("draft"); // 撤回为草稿
            course.setUpdatedAt(LocalDateTime.now());
            courseMapper.updateCourse(course);
            return Result.success("课程已撤回为草稿状态");
        } catch (Exception e) {
            return Result.error("下架课程失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<CourseSection>> getCourseSections(Integer courseId, Integer teacherId) {
        try {
            // 验证课程是否属于当前教师
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            if (!course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限访问此课程的章节");
            }
            
            List<CourseSection> sections = courseSectionMapper.getSectionsByCourseId(courseId);
            return Result.success(sections);
        } catch (Exception e) {
            return Result.error("获取课程章节失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<CourseSection> createSection(SectionCreateRequest request, Integer teacherId) {
        try {
            // 验证课程是否属于当前教师
            Course course = courseMapper.getCourseById(request.getCourseId());
            if (course == null) {
                return Result.error("课程不存在");
            }
            if (!course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限在此课程中创建章节");
            }
            
            CourseSection section = new CourseSection();
            section.setCourseId(request.getCourseId());
            section.setTitle(request.getTitle());
            section.setDescription(request.getDescription());
            section.setSortOrder(request.getSortOrder());
            section.setCreatedAt(LocalDateTime.now());
            section.setUpdatedAt(LocalDateTime.now());
            
            courseSectionMapper.insertSection(section);
            return Result.success(section);
        } catch (Exception e) {
            return Result.error("创建章节失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<CourseSection> updateSection(Integer sectionId, SectionCreateRequest request, Integer teacherId) {
        try {
            CourseSection section = courseSectionMapper.getSectionById(sectionId);
            if (section == null) {
                return Result.error("章节不存在");
            }
            
            // 验证章节所属课程是否属于当前教师
            Course course = courseMapper.getCourseById(section.getCourseId());
            if (course == null || !course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限操作此章节");
            }
            
            section.setTitle(request.getTitle());
            section.setDescription(request.getDescription());
            section.setSortOrder(request.getSortOrder());
            section.setUpdatedAt(LocalDateTime.now());
            
            courseSectionMapper.updateSection(section);
            return Result.success(section);
        } catch (Exception e) {
            return Result.error("更新章节失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> deleteSection(Integer sectionId, Integer teacherId) {
        try {
            CourseSection section = courseSectionMapper.getSectionById(sectionId);
            if (section == null) {
                return Result.error("章节不存在");
            }
            
            // 验证章节所属课程是否属于当前教师
            Course course = courseMapper.getCourseById(section.getCourseId());
            if (course == null || !course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限操作此章节");
            }
            
            courseSectionMapper.deleteSection(sectionId);
            return Result.success("删除章节成功");
        } catch (Exception e) {
            return Result.error("删除章节失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<List<CourseLesson>> getSectionLessons(Integer sectionId, Integer teacherId) {
        try {
            // 验证章节所属课程是否属于当前教师
            CourseSection section = courseSectionMapper.getSectionById(sectionId);
            if (section == null) {
                return Result.error("章节不存在");
            }
            
            Course course = courseMapper.getCourseById(section.getCourseId());
            if (course == null || !course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限访问此章节的课时");
            }
            
            List<CourseLesson> lessons = courseLessonMapper.getLessonsBySectionId(sectionId);
            return Result.success(lessons);
        } catch (Exception e) {
            return Result.error("获取章节课时失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<CourseLesson> createLesson(LessonCreateRequest request, Integer teacherId) {
        try {
            // 验证章节所属课程是否属于当前教师
            CourseSection section = courseSectionMapper.getSectionById(request.getSectionId());
            if (section == null) {
                return Result.error("章节不存在");
            }
            
            Course course = courseMapper.getCourseById(section.getCourseId());
            if (course == null || !course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限在此章节中创建课时");
            }
            
            CourseLesson lesson = new CourseLesson();
            lesson.setSectionId(request.getSectionId());
            lesson.setTitle(request.getTitle());
            lesson.setDescription(request.getDescription());
            lesson.setType(request.getType());
            // 根据类型设置不同的URL
            if ("video".equals(request.getType())) {
                lesson.setVideoUrl(request.getContent());
            } else if ("document".equals(request.getType())) {
                lesson.setDocumentUrl(request.getContent());
            }
            lesson.setDuration(request.getDuration());
            lesson.setIsFree(request.getIsFree() != null ? request.getIsFree() : false);
            lesson.setSortOrder(request.getSortOrder());
            lesson.setCreatedAt(LocalDateTime.now());
            lesson.setUpdatedAt(LocalDateTime.now());
            
            courseLessonMapper.insertLesson(lesson);
            return Result.success(lesson);
        } catch (Exception e) {
            return Result.error("创建课时失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<CourseLesson> updateLesson(Integer lessonId, LessonCreateRequest request, Integer teacherId) {
        try {
            CourseLesson lesson = courseLessonMapper.getLessonById(lessonId);
            if (lesson == null) {
                return Result.error("课时不存在");
            }
            
            // 验证课时所属课程是否属于当前教师
            CourseSection section = courseSectionMapper.getSectionById(lesson.getSectionId());
            if (section == null) {
                return Result.error("章节不存在");
            }
            
            Course course = courseMapper.getCourseById(section.getCourseId());
            if (course == null || !course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限操作此课时");
            }
            
            lesson.setTitle(request.getTitle());
            lesson.setDescription(request.getDescription());
            lesson.setType(request.getType());
            // 根据类型设置不同的URL
            if ("video".equals(request.getType())) {
                lesson.setVideoUrl(request.getContent());
            } else if ("document".equals(request.getType())) {
                lesson.setDocumentUrl(request.getContent());
            }
            lesson.setDuration(request.getDuration());
            lesson.setIsFree(request.getIsFree());
            lesson.setSortOrder(request.getSortOrder());
            lesson.setUpdatedAt(LocalDateTime.now());
            
            courseLessonMapper.updateLesson(lesson);
            return Result.success(lesson);
        } catch (Exception e) {
            return Result.error("更新课时失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> deleteLesson(Integer lessonId, Integer teacherId) {
        try {
            CourseLesson lesson = courseLessonMapper.getLessonById(lessonId);
            if (lesson == null) {
                return Result.error("课时不存在");
            }
            
            // 验证课时所属课程是否属于当前教师
            CourseSection section = courseSectionMapper.getSectionById(lesson.getSectionId());
            if (section == null) {
                return Result.error("章节不存在");
            }
            
            Course course = courseMapper.getCourseById(section.getCourseId());
            if (course == null || !course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限操作此课时");
            }
            
            courseLessonMapper.deleteLesson(lessonId);
            return Result.success("删除课时成功");
        } catch (Exception e) {
            return Result.error("删除课时失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<Object> getCourseStats(Integer courseId, Integer teacherId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取课程基本信息
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            // 验证课程是否属于当前教师
            if (!course.getInstructorId().equals(teacherId)) {
                return Result.error("无权限查看此课程的统计信息");
            }
            
            // 获取章节数
            List<CourseSection> sections = courseSectionMapper.getSectionsByCourseId(courseId);
            int sectionCount = sections.size();
            
            // 获取课时数
            int lessonCount = 0;
            for (CourseSection section : sections) {
                List<CourseLesson> lessons = courseLessonMapper.getLessonsBySectionId(section.getId());
                lessonCount += lessons.size();
            }
            
            stats.put("courseId", courseId);
            stats.put("title", course.getTitle());
            stats.put("status", course.getStatus());
            stats.put("sectionCount", sectionCount);
            stats.put("lessonCount", lessonCount);
            stats.put("studentCount", course.getStudentCount());
            stats.put("rating", course.getRating());
            stats.put("ratingCount", course.getRatingCount());
            stats.put("createdAt", course.getCreatedAt());
            stats.put("updatedAt", course.getUpdatedAt());
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取课程统计失败: " + e.getMessage());
        }
    }
}
