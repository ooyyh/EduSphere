#!/usr/bin/env python3
"""
修复TeacherController中的方法签名，添加HttpServletRequest参数
"""

import re

# 读取文件
with open('D:/Project/IdeaProjects/EduSphere/EduSphereB/src/main/java/top/ooyyh/edusphere/controller/TeacherController.java', 'r', encoding='utf-8') as f:
    content = f.read()

# 定义需要修复的方法模式
patterns = [
    # 更新课程
    (r'public Result<Course> updateCourse\(@PathVariable Integer courseId, @RequestBody CourseCreateRequest request\) \{\s*return teacherService\.updateCourse\(courseId, request, getCurrentUserId\(\)\);\s*\}',
     'public Result<Course> updateCourse(@PathVariable Integer courseId, @RequestBody CourseCreateRequest request, HttpServletRequest httpRequest) {\n        return teacherService.updateCourse(courseId, request, getCurrentUserId(httpRequest));\n    }'),
    
    # 删除课程
    (r'public Result<String> deleteCourse\(@PathVariable Integer courseId\) \{\s*return teacherService\.deleteCourse\(courseId, getCurrentUserId\(\)\);\s*\}',
     'public Result<String> deleteCourse(@PathVariable Integer courseId, HttpServletRequest request) {\n        return teacherService.deleteCourse(courseId, getCurrentUserId(request));\n    }'),
    
    # 发布课程
    (r'public Result<String> publishCourse\(@PathVariable Integer courseId\) \{\s*return teacherService\.publishCourse\(courseId, getCurrentUserId\(\)\);\s*\}',
     'public Result<String> publishCourse(@PathVariable Integer courseId, HttpServletRequest request) {\n        return teacherService.publishCourse(courseId, getCurrentUserId(request));\n    }'),
    
    # 下架课程
    (r'public Result<String> unpublishCourse\(@PathVariable Integer courseId\) \{\s*return teacherService\.unpublishCourse\(courseId, getCurrentUserId\(\)\);\s*\}',
     'public Result<String> unpublishCourse(@PathVariable Integer courseId, HttpServletRequest request) {\n        return teacherService.unpublishCourse(courseId, getCurrentUserId(request));\n    }'),
    
    # 获取课程章节
    (r'public Result<List<CourseSection>> getCourseSections\(@PathVariable Integer courseId\) \{\s*return teacherService\.getCourseSections\(courseId, getCurrentUserId\(\)\);\s*\}',
     'public Result<List<CourseSection>> getCourseSections(@PathVariable Integer courseId, HttpServletRequest request) {\n        return teacherService.getCourseSections(courseId, getCurrentUserId(request));\n    }'),
    
    # 创建章节
    (r'public Result<CourseSection> createSection\(@PathVariable Integer courseId, @RequestBody SectionCreateRequest request\) \{\s*request\.setCourseId\(courseId\);\s*return teacherService\.createSection\(request, getCurrentUserId\(\)\);\s*\}',
     'public Result<CourseSection> createSection(@PathVariable Integer courseId, @RequestBody SectionCreateRequest request, HttpServletRequest httpRequest) {\n        request.setCourseId(courseId);\n        return teacherService.createSection(request, getCurrentUserId(httpRequest));\n    }'),
    
    # 更新章节
    (r'public Result<CourseSection> updateSection\(@PathVariable Integer sectionId, @RequestBody SectionCreateRequest request\) \{\s*return teacherService\.updateSection\(sectionId, request, getCurrentUserId\(\)\);\s*\}',
     'public Result<CourseSection> updateSection(@PathVariable Integer sectionId, @RequestBody SectionCreateRequest request, HttpServletRequest httpRequest) {\n        return teacherService.updateSection(sectionId, request, getCurrentUserId(httpRequest));\n    }'),
    
    # 删除章节
    (r'public Result<String> deleteSection\(@PathVariable Integer sectionId\) \{\s*return teacherService\.deleteSection\(sectionId, getCurrentUserId\(\)\);\s*\}',
     'public Result<String> deleteSection(@PathVariable Integer sectionId, HttpServletRequest request) {\n        return teacherService.deleteSection(sectionId, getCurrentUserId(request));\n    }'),
    
    # 获取章节课时
    (r'public Result<List<CourseLesson>> getSectionLessons\(@PathVariable Integer sectionId\) \{\s*return teacherService\.getSectionLessons\(sectionId, getCurrentUserId\(\)\);\s*\}',
     'public Result<List<CourseLesson>> getSectionLessons(@PathVariable Integer sectionId, HttpServletRequest request) {\n        return teacherService.getSectionLessons(sectionId, getCurrentUserId(request));\n    }'),
    
    # 创建课时
    (r'public Result<CourseLesson> createLesson\(@PathVariable Integer sectionId, @RequestBody LessonCreateRequest request\) \{\s*request\.setSectionId\(sectionId\);\s*return teacherService\.createLesson\(request, getCurrentUserId\(\)\);\s*\}',
     'public Result<CourseLesson> createLesson(@PathVariable Integer sectionId, @RequestBody LessonCreateRequest request, HttpServletRequest httpRequest) {\n        request.setSectionId(sectionId);\n        return teacherService.createLesson(request, getCurrentUserId(httpRequest));\n    }'),
    
    # 更新课时
    (r'public Result<CourseLesson> updateLesson\(@PathVariable Integer lessonId, @RequestBody LessonCreateRequest request\) \{\s*return teacherService\.updateLesson\(lessonId, request, getCurrentUserId\(\)\);\s*\}',
     'public Result<CourseLesson> updateLesson(@PathVariable Integer lessonId, @RequestBody LessonCreateRequest request, HttpServletRequest httpRequest) {\n        return teacherService.updateLesson(lessonId, request, getCurrentUserId(httpRequest));\n    }'),
    
    # 删除课时
    (r'public Result<String> deleteLesson\(@PathVariable Integer lessonId\) \{\s*return teacherService\.deleteLesson\(lessonId, getCurrentUserId\(\)\);\s*\}',
     'public Result<String> deleteLesson(@PathVariable Integer lessonId, HttpServletRequest request) {\n        return teacherService.deleteLesson(lessonId, getCurrentUserId(request));\n    }'),
    
    # 获取课程统计
    (r'public Result<Object> getCourseStats\(@PathVariable Integer courseId\) \{\s*return teacherService\.getCourseStats\(courseId, getCurrentUserId\(\)\);\s*\}',
     'public Result<Object> getCourseStats(@PathVariable Integer courseId, HttpServletRequest request) {\n        return teacherService.getCourseStats(courseId, getCurrentUserId(request));\n    }'),
]

# 应用所有修复
for pattern, replacement in patterns:
    content = re.sub(pattern, replacement, content, flags=re.MULTILINE | re.DOTALL)

# 写回文件
with open('D:/Project/IdeaProjects/EduSphere/EduSphereB/src/main/java/top/ooyyh/edusphere/controller/TeacherController.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("TeacherController修复完成！")
