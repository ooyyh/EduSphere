package top.ooyyh.edusphere.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
    private String avatar;
}
