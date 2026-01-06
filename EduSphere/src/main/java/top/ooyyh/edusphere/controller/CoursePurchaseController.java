package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.PurchaseRecord;
import top.ooyyh.edusphere.request.PurchaseRequest;
import top.ooyyh.edusphere.response.PurchaseResponse;
import top.ooyyh.edusphere.response.UserPurchasedCourseResponse;
import top.ooyyh.edusphere.service.CoursePurchaseService;
import top.ooyyh.edusphere.utils.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 课程购买控制器
 */
@RestController
@RequestMapping("/purchase")
public class CoursePurchaseController {    
    @Autowired
    private CoursePurchaseService coursePurchaseService;
    
    /**
     * 购买课程
     */
    @PostMapping("/course")
    public void purchaseCourse(@RequestBody PurchaseRequest purchaseRequest,
                              HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            if (purchaseRequest.getCourseId() == null) {
                ResponseUtils.writeErrorResponse(response, 400, "课程ID不能为空");
                return;
            }
            
            PurchaseResponse purchaseResponse = coursePurchaseService.purchaseCourse(userId, purchaseRequest);
            ResponseUtils.writeSuccessResponse(response, purchaseResponse);
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, e.getMessage());
        }
    }
    
    /**
     * 检查用户是否已购买课程
     */
    @GetMapping("/check/{courseId}")
    public void checkPurchase(@PathVariable Integer courseId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            boolean hasPurchased = coursePurchaseService.hasPurchasedCourse(userId, courseId);
            ResponseUtils.writeSuccessResponse(response, hasPurchased);
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "检查购买状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户购买的课程列表
     */
    @GetMapping("/my-courses")
    public void getMyCourses(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            // 使用包含课程信息的新方法
            List<UserPurchasedCourseResponse> courses = coursePurchaseService.getUserPurchasedCoursesWithInfo(userId);
            ResponseUtils.writeSuccessResponse(response, courses);
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "获取我的课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取讲师销售记录
     */
    @GetMapping("/sales")
    public void getSales(HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        try {
            Integer instructorId = (Integer) request.getAttribute("userId");
            if (instructorId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            List<PurchaseRecord> sales = coursePurchaseService.getInstructorSales(instructorId);
            ResponseUtils.writeSuccessResponse(response, sales);
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "获取销售记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取讲师总收入
     */
    @GetMapping("/income")
    public void getIncome(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        try {
            Integer instructorId = (Integer) request.getAttribute("userId");
            if (instructorId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            Double totalIncome = coursePurchaseService.getInstructorTotalIncome(instructorId);
            ResponseUtils.writeSuccessResponse(response, totalIncome);
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "获取收入信息失败: " + e.getMessage());
        }
    }
}
