package top.ooyyh.edusphere.utils;

import java.util.function.Supplier;

/**
 * 服务层通用工具类
 */
public class ServiceUtils {
    
    /**
     * 执行数据库操作并处理异常
     * @param operation 数据库操作
     * @param errorMessage 错误信息
     * @param <T> 返回类型
     * @return 操作结果
     */
    public static <T> Result<T> executeOperation(Supplier<T> operation, String errorMessage) {
        try {
            T result = operation.get();
            if (result == null) {
                return Result.error(errorMessage);
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(errorMessage + ": " + e.getMessage());
        }
    }
    
    /**
     * 执行数据库操作并处理异常（无返回值）
     * @param operation 数据库操作
     * @param successMessage 成功信息
     * @param errorMessage 错误信息
     * @return 操作结果
     */
    public static Result<String> executeOperation(Runnable operation, String successMessage, String errorMessage) {
        try {
            operation.run();
            return Result.success(successMessage);
        } catch (Exception e) {
            return Result.error(errorMessage + ": " + e.getMessage());
        }
    }
    
    /**
     * 执行数据库操作并处理异常（返回影响行数）
     * @param operation 数据库操作
     * @param successMessage 成功信息
     * @param errorMessage 错误信息
     * @return 操作结果
     */
    public static Result<String> executeUpdateOperation(Supplier<Integer> operation, String successMessage, String errorMessage) {
        try {
            Integer result = operation.get();
            if (result != null && result > 0) {
                return Result.success(successMessage);
            } else {
                return Result.error(errorMessage);
            }
        } catch (Exception e) {
            return Result.error(errorMessage + ": " + e.getMessage());
        }
    }
}
