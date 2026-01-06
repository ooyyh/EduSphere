package top.ooyyh.edusphere.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 返回结果
 * @param <T> data 类型
 */
@Data
// 无参构造注解
@NoArgsConstructor
// 全参构造注解
@AllArgsConstructor
public class Result<T> {
    private int code;       // 0 = success, 1 = error
    private String msg;
    private T data;

    // 成功返回
    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(0, msg, data);
    }

    // 错误返回
    public static <T> Result<T> error() {
        return new Result<>(1, "error", null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(1, msg, null);
    }

    public static <T> Result<T> error(String msg, T data) {
        return new Result<>(1, msg, data);
    }

    // 链式调用（可选）
    public Result<T> withMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Result<T> withData(T data) {
        this.data = data;
        return this;
    }

    public Result<T> withCode(int code) {
        this.code = code;
        return this;
    }
}
