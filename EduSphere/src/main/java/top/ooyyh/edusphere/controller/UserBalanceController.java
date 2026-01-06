package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.RechargeRecord;
import top.ooyyh.edusphere.request.RechargeRequest;
import top.ooyyh.edusphere.response.UserBalanceResponse;
import top.ooyyh.edusphere.service.UserBalanceService;
import top.ooyyh.edusphere.utils.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户余额控制器
 */
@RestController
@RequestMapping("/balance")
public class UserBalanceController {
    
    @Autowired
    private UserBalanceService userBalanceService;
    
    /**
     * 获取用户余额
     */
    @GetMapping("/info")
    public void getBalance(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            UserBalanceResponse balanceInfo = userBalanceService.getUserBalance(userId);
            ResponseUtils.writeSuccessResponse(response, balanceInfo);
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "获取余额失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户充值
     */
    @PostMapping("/recharge")
    public void recharge(@RequestBody RechargeRequest rechargeRequest, 
                        HttpServletRequest request, 
                        HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            if (rechargeRequest.getAmount() == null || rechargeRequest.getAmount().doubleValue() <= 0) {
                ResponseUtils.writeErrorResponse(response, 400, "充值金额必须大于0");
                return;
            }
            
            boolean success = userBalanceService.recharge(userId, rechargeRequest);
            if (success) {
                ResponseUtils.writeSuccessResponse(response, "充值成功");
            } else {
                ResponseUtils.writeErrorResponse(response, 500, "充值失败");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "充值失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取充值记录
     */
    @GetMapping("/recharge-history")
    public void getRechargeHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                ResponseUtils.writeErrorResponse(response, 401, "用户未登录");
                return;
            }
            
            java.util.List<RechargeRecord> records = userBalanceService.getRechargeHistory(userId);
            ResponseUtils.writeSuccessResponse(response, records);
            
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.writeErrorResponse(response, 500, "获取充值记录失败: " + e.getMessage());
        }
    }
}
