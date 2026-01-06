package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.PurchaseRecord;
import top.ooyyh.edusphere.request.PurchaseRequest;
import top.ooyyh.edusphere.response.PurchaseResponse;
import top.ooyyh.edusphere.response.UserPurchasedCourseResponse;

import java.util.List;

/**
 * 课程购买服务接口
 */
public interface CoursePurchaseService {
    
    /**
     * 购买课程
     */
    PurchaseResponse purchaseCourse(Integer userId, PurchaseRequest request);
    
    /**
     * 检查用户是否已购买课程
     */
    boolean hasPurchasedCourse(Integer userId, Integer courseId);
    
    /**
     * 获取用户购买的课程列表
     */
    List<PurchaseRecord> getUserPurchasedCourses(Integer userId);
    
    /**
     * 获取用户购买的课程列表（包含课程信息）
     */
    List<UserPurchasedCourseResponse> getUserPurchasedCoursesWithInfo(Integer userId);
    
    /**
     * 获取讲师销售记录
     */
    List<PurchaseRecord> getInstructorSales(Integer instructorId);
    
    /**
     * 获取讲师总收入
     */
    Double getInstructorTotalIncome(Integer instructorId);
}
