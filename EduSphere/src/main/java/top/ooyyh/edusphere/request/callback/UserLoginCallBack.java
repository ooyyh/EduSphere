package top.ooyyh.edusphere.request.callback;

import lombok.Data;
import top.ooyyh.edusphere.entity.User;

@Data
public class UserLoginCallBack {
    private User user;
    private String token;
}
