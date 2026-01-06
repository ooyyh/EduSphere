package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ooyyh.edusphere.entity.Course;
import top.ooyyh.edusphere.entity.InstructorIncome;
import top.ooyyh.edusphere.entity.PurchaseRecord;
import top.ooyyh.edusphere.entity.UserBalance;
import top.ooyyh.edusphere.mapper.CourseMapper;
import top.ooyyh.edusphere.mapper.InstructorIncomeMapper;
import top.ooyyh.edusphere.mapper.PurchaseRecordMapper;
import top.ooyyh.edusphere.mapper.UserBalanceMapper;
import top.ooyyh.edusphere.mapper.UserMapper;
import top.ooyyh.edusphere.request.PurchaseRequest;
import top.ooyyh.edusphere.response.PurchaseResponse;
import top.ooyyh.edusphere.response.UserPurchasedCourseResponse;
import top.ooyyh.edusphere.service.CoursePurchaseService;
import top.ooyyh.edusphere.service.UserBalanceService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程购买服务实现类
 */
@Service
public class CoursePurchaseServiceImpl implements CoursePurchaseService {
    
    @Autowired
    private PurchaseRecordMapper purchaseRecordMapper;
    
    @Autowired
    private UserBalanceMapper userBalanceMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private InstructorIncomeMapper instructorIncomeMapper;
    
    @Autowired
    private UserBalanceService userBalanceService;
    
    @Autowired
    private UserMapper userMapper;
    
    // 平台手续费比例（5%）
    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.05");
    
    @Override
    @Transactional
    public PurchaseResponse purchaseCourse(Integer userId, PurchaseRequest request) {
        try {
            // 1. 检查课程是否存在且已发布
            Course course = courseMapper.getCourseById(request.getCourseId());
            if (course == null || !"published".equals(course.getStatus())) {
                throw new RuntimeException("课程不存在或未发布");
            }
            
            // 2. 检查用户是否已购买
            if (hasPurchasedCourse(userId, request.getCourseId())) {
                throw new RuntimeException("您已购买过此课程");
            }
            
            // 3. 检查余额是否足够
            BigDecimal coursePrice = course.getPrice();
            UserBalance userBalance = userBalanceMapper.getByUserId(userId);
            if (userBalance == null) {
                // 如果用户没有余额记录，初始化一个
                userBalanceService.initUserBalance(userId);
                userBalance = userBalanceMapper.getByUserId(userId);
            }
            
            if (userBalance == null || userBalance.getBalance().compareTo(coursePrice) < 0) {
                throw new RuntimeException("余额不足，当前余额: ¥" + (userBalance != null ? userBalance.getBalance() : "0.00") + "，需要: ¥" + coursePrice);
            }
            
            // 4. 计算费用分配
            BigDecimal platformFee = coursePrice.multiply(PLATFORM_FEE_RATE);
            BigDecimal instructorIncome = coursePrice.subtract(platformFee);
            
            // 5. 扣减用户余额
            BigDecimal balanceBefore = userBalance.getBalance();
            BigDecimal balanceAfter = balanceBefore.subtract(coursePrice);
            userBalanceMapper.subtractBalance(userId, coursePrice);
            
            // 6. 增加讲师余额
            UserBalance instructorBalance = userBalanceMapper.getByUserId(course.getInstructorId());
            if (instructorBalance == null) {
                // 如果讲师没有余额记录，初始化一个
                UserBalance newBalance = new UserBalance();
                newBalance.setUserId(course.getInstructorId());
                newBalance.setBalance(BigDecimal.ZERO);
                newBalance.setFrozenBalance(BigDecimal.ZERO);
                newBalance.setTotalRecharge(BigDecimal.ZERO);
                newBalance.setTotalConsumption(BigDecimal.ZERO);
                newBalance.setCreatedAt(LocalDateTime.now());
                newBalance.setUpdatedAt(LocalDateTime.now());
                userBalanceMapper.insert(newBalance);
            }
            userBalanceMapper.addBalance(course.getInstructorId(), instructorIncome);
            
            // 7. 创建购买记录
            PurchaseRecord purchaseRecord = new PurchaseRecord();
            purchaseRecord.setUserId(userId);
            purchaseRecord.setCourseId(request.getCourseId());
            purchaseRecord.setInstructorId(course.getInstructorId());
            purchaseRecord.setPurchasePrice(coursePrice);
            purchaseRecord.setBalanceBefore(balanceBefore);
            purchaseRecord.setBalanceAfter(balanceAfter);
            purchaseRecord.setInstructorIncome(instructorIncome);
            purchaseRecord.setPlatformFee(platformFee);
            purchaseRecord.setStatus("success");
            purchaseRecord.setCreatedAt(LocalDateTime.now());
            
            purchaseRecordMapper.insert(purchaseRecord);
            
            // 8. 创建讲师收入记录
            InstructorIncome incomeRecord = new InstructorIncome();
            incomeRecord.setInstructorId(course.getInstructorId());
            incomeRecord.setCourseId(request.getCourseId());
            incomeRecord.setPurchaseRecordId(purchaseRecord.getId());
            incomeRecord.setIncomeAmount(instructorIncome);
            
            // 计算讲师累计收入
            BigDecimal totalIncome = instructorIncomeMapper.getTotalIncomeByInstructorId(course.getInstructorId());
            if (totalIncome == null) {
                totalIncome = BigDecimal.ZERO;
            }
            incomeRecord.setTotalIncome(totalIncome.add(instructorIncome));
            incomeRecord.setCreatedAt(LocalDateTime.now());
            
            instructorIncomeMapper.insert(incomeRecord);
            
            // 9. 更新课程学员数量
            courseMapper.updateStudentCount(request.getCourseId());
            
            // 10. 添加到用户课程表
            courseMapper.insertUserCourse(userId, request.getCourseId(), coursePrice);
            
            // 11. 构建响应
            PurchaseResponse response = new PurchaseResponse();
            response.setPurchaseRecordId(purchaseRecord.getId());
            response.setCourseId(request.getCourseId());
            response.setCourseTitle(course.getTitle());
            response.setPurchasePrice(coursePrice);
            response.setBalanceAfter(balanceAfter);
            response.setPurchaseTime(purchaseRecord.getCreatedAt());
            response.setStatus(purchaseRecord.getStatus());
            
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("购买失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean hasPurchasedCourse(Integer userId, Integer courseId) {
        PurchaseRecord record = purchaseRecordMapper.getByUserIdAndCourseId(userId, courseId);
        return record != null && "success".equals(record.getStatus());
    }
    
    @Override
    public List<PurchaseRecord> getUserPurchasedCourses(Integer userId) {
        return purchaseRecordMapper.getByUserId(userId);
    }
    
    @Override
    public List<UserPurchasedCourseResponse> getUserPurchasedCoursesWithInfo(Integer userId) {
        // 获取用户的购买记录
        List<PurchaseRecord> purchaseRecords = purchaseRecordMapper.getByUserId(userId);
        
        // 转换为包含课程信息的响应对象
        return purchaseRecords.stream().map(record -> {
            UserPurchasedCourseResponse response = new UserPurchasedCourseResponse();
            
            // 复制购买记录的基本信息
            response.setId(record.getId());
            response.setUserId(record.getUserId());
            response.setCourseId(record.getCourseId());
            response.setInstructorId(record.getInstructorId());
            response.setPurchasePrice(record.getPurchasePrice());
            response.setBalanceBefore(record.getBalanceBefore());
            response.setBalanceAfter(record.getBalanceAfter());
            response.setInstructorIncome(record.getInstructorIncome());
            response.setPlatformFee(record.getPlatformFee());
            response.setStatus(record.getStatus());
            response.setCreatedAt(record.getCreatedAt());
            
            // 获取课程信息
            Course course = courseMapper.getCourseById(record.getCourseId());
            if (course != null) {
                response.setCourseTitle(course.getTitle());
                response.setCoverImage(course.getCoverImage());
                
                // 获取讲师信息
                top.ooyyh.edusphere.entity.User instructor = userMapper.getUserById(course.getInstructorId());
                if (instructor != null) {
                    response.setInstructorName(instructor.getUsername());
                }
            }
            
            return response;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<PurchaseRecord> getInstructorSales(Integer instructorId) {
        return purchaseRecordMapper.getByInstructorId(instructorId);
    }
    
    @Override
    public Double getInstructorTotalIncome(Integer instructorId) {
        BigDecimal totalIncome = instructorIncomeMapper.getTotalIncomeByInstructorId(instructorId);
        return totalIncome != null ? totalIncome.doubleValue() : 0.0;
    }
}
