package top.ooyyh.edusphere.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 响应工具类
 */
public class ResponseUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 注册JavaTimeModule以支持LocalDateTime序列化
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 写入JSON响应
     */
    public static void writeJsonResponse(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        String json = objectMapper.writeValueAsString(new ApiResponse(1, msg, null));
        response.getWriter().write(json);
    }

    /**
     * 写入成功响应（字符串消息）
     */
    public static void writeSuccessResponse(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        String json = objectMapper.writeValueAsString(new ApiResponse(0, msg, null));
        response.getWriter().write(json);
    }

    /**
     * 写入成功响应（对象数据）
     */
    public static void writeSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        String json = objectMapper.writeValueAsString(new ApiResponse(0, "success", data));
        response.getWriter().write(json);
    }

    /**
     * 写入错误响应
     */
    public static void writeErrorResponse(HttpServletResponse response, int status, String msg) throws IOException {
        writeJsonResponse(response, status, msg);
    }

    /**
     * API响应包装类
     */
    public static class ApiResponse {
        private int code;
        private String msg;
        private Object data;

        public ApiResponse(int code, String msg, Object data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        // Getters and Setters
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMsg() { return msg; }
        public void setMsg(String msg) { this.msg = msg; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}
