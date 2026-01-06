package top.ooyyh.edusphere.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String role;
    private String username;
    private String password;
    private String email;
}
