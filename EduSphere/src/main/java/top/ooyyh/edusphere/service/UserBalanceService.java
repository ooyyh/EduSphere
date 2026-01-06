package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.RechargeRecord;
import top.ooyyh.edusphere.entity.UserBalance;
import top.ooyyh.edusphere.request.RechargeRequest;
import top.ooyyh.edusphere.response.UserBalanceResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户余额服务接口
 */
public interface UserBalanceService {
    
    /**
     * 获取用户余额
     */
    UserBalanceResponse getUserBalance(Integer userId);
    
    /**
     * 用户充值
     */
    boolean recharge(Integer userId, RechargeRequest request);
    
    /**
     * 检查用户余额是否足够
     */
    boolean checkBalance(Integer userId, BigDecimal amount);
    
    /**
     * 扣减用户余额
     */
    boolean deductBalance(Integer userId, BigDecimal amount);
    
    /**
     * 增加用户余额
     */
    boolean addBalance(Integer userId, BigDecimal amount);
    
    /**
     * 初始化用户余额
     */
    boolean initUserBalance(Integer userId);
    
    /**
     * 获取充值记录
     */
    List<RechargeRecord> getRechargeHistory(Integer userId);
}
