package se.fulkopinglibraryweb.service.interfaces;

import se.fulkopinglibraryweb.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(User user);
    User createUser(String username, String email, String password, String role);
    Optional<User> findById(String id);
    List<User> findAll();
    User update(User user);
    void delete(String userId);
    boolean authenticateUser(String username, String password);
    Optional<User> findByUsername(String username);
    List<User> findAllActiveUsers();
    void updateUserStatus(String userId, boolean isActive);
    boolean validateUserCredentials(String username, String password);
    void changePassword(String userId, String newPassword);
    Optional<User> findByEmail(String email);
}
