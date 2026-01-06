package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ooyyh.edusphere.entity.RechargeRecord;
import top.ooyyh.edusphere.entity.UserBalance;
import top.ooyyh.edusphere.mapper.RechargeRecordMapper;
import top.ooyyh.edusphere.mapper.UserBalanceMapper;
import top.ooyyh.edusphere.request.RechargeRequest;
import top.ooyyh.edusphere.response.UserBalanceResponse;
import top.ooyyh.edusphere.service.UserBalanceService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户余额服务实现类
 */
@Service
public class UserBalanceServiceImpl implements UserBalanceService {
    
    @Autowired
    private UserBalanceMapper userBalanceMapper;
    
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;
    
    @Override
    public UserBalanceResponse getUserBalance(Integer userId) {
        UserBalance userBalance = userBalanceMapper.getByUserId(userId);
        if (userBalance == null) {
            // 如果用户没有余额记录，初始化一个
            initUserBalance(userId);
            userBalance = userBalanceMapper.getByUserId(userId);
        }
        
        UserBalanceResponse response = new UserBalanceResponse();
        response.setUserId(userId);
        response.setBalance(userBalance.getBalance());
        response.setFrozenBalance(userBalance.getFrozenBalance());
        response.setTotalRecharge(userBalance.getTotalRecharge());
        response.setTotalConsumption(userBalance.getTotalConsumption());
        response.setAvailableBalance(userBalance.getBalance().subtract(userBalance.getFrozenBalance()));
        
        return response;
    }
    
    @Override
    @Transactional
    public boolean recharge(Integer userId, RechargeRequest request) {
        try {
            UserBalance userBalance = userBalanceMapper.getByUserId(userId);
            if (userBalance == null) {
                initUserBalance(userId);
                userBalance = userBalanceMapper.getByUserId(userId);
            }
            
            BigDecimal balanceBefore = userBalance.getBalance();
            BigDecimal balanceAfter = balanceBefore.add(request.getAmount());
            
            // 更新用户余额
            userBalanceMapper.addBalance(userId, request.getAmount());
            
            // 记录充值记录
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setUserId(userId);
            rechargeRecord.setAmount(request.getAmount());
            rechargeRecord.setBalanceBefore(balanceBefore);
            rechargeRecord.setBalanceAfter(balanceAfter);
            rechargeRecord.setRechargeType("manual");
            rechargeRecord.setRemark(request.getRemark());
            rechargeRecord.setCreatedAt(LocalDateTime.now());
            
            rechargeRecordMapper.insert(rechargeRecord);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean checkBalance(Integer userId, BigDecimal amount) {
        UserBalance userBalance = userBalanceMapper.getByUserId(userId);
        if (userBalance == null) {
            return false;
        }
        
        BigDecimal availableBalance = userBalance.getBalance().subtract(userBalance.getFrozenBalance());
        return availableBalance.compareTo(amount) >= 0;
    }
    
    @Override
    @Transactional
    public boolean deductBalance(Integer userId, BigDecimal amount) {
        try {
            if (!checkBalance(userId, amount)) {
                return false;
            }
            
            userBalanceMapper.subtractBalance(userId, amount);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean addBalance(Integer userId, BigDecimal amount) {
        try {
            userBalanceMapper.addBalance(userId, amount);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean initUserBalance(Integer userId) {
        try {
            UserBalance userBalance = new UserBalance();
            userBalance.setUserId(userId);
            userBalance.setBalance(BigDecimal.ZERO);
            userBalance.setFrozenBalance(BigDecimal.ZERO);
            userBalance.setTotalRecharge(BigDecimal.ZERO);
            userBalance.setTotalConsumption(BigDecimal.ZERO);
            userBalance.setCreatedAt(LocalDateTime.now());
            userBalance.setUpdatedAt(LocalDateTime.now());
            
            userBalanceMapper.insert(userBalance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<RechargeRecord> getRechargeHistory(Integer userId) {
        try {
            return rechargeRecordMapper.getByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
}
