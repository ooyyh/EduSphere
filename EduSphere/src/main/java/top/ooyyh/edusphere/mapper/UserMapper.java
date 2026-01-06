package top.ooyyh.edusphere.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.ooyyh.edusphere.entity.User;
import top.ooyyh.edusphere.request.UserLoginRequest;
import top.ooyyh.edusphere.request.UserRegisterRequest;

import java.util.List;

@Mapper
public interface UserMapper {
    // 判断用户是否存在
    Boolean isUserExist(String userName);

    User getUserByNamePassRole(UserLoginRequest user);

    int register(UserRegisterRequest user);

    // 根据ID获取用户信息
    User getUserById(Integer id);

    // 根据用户名获取用户信息
    User getUserByUsername(String username);

    // 根据邮箱获取用户信息
    User getUserByEmail(String email);

    // 更新用户信息
    void updateUser(User user);
    
    // 获取所有用户
    List<User> getAllUsers();
    
    // 删除用户
    void deleteUser(Integer id);
}
