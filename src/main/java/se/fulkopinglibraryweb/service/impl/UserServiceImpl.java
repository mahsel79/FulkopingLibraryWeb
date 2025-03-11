package se.fulkopinglibraryweb.service.impl;

import com.google.cloud.firestore.*;
import se.fulkopinglibraryweb.repository.UserRepository;
import se.fulkopinglibraryweb.service.interfaces.UserService;
import se.fulkopinglibraryweb.utils.PasswordUtils;
import se.fulkopinglibraryweb.utils.LoggingUtils;
import se.fulkopinglibraryweb.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @SuppressWarnings("unused")
    private final Firestore firestore;
    
    @SuppressWarnings("unused")
    private final UserRepository userRepository;
    
    private final PasswordUtils passwordUtils;
    private final CollectionReference userCollection;

    public UserServiceImpl(Firestore firestore, UserRepository userRepository, PasswordUtils passwordUtils) {
        this.firestore = firestore;
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
        this.userCollection = firestore.collection("users");
    }

    @Override
    public User create(User user) {
        LoggingUtils.logServiceOperation("UserService", "create", "Creating new user: " + user.getUsername());
        
        try {
            String salt = passwordUtils.generateSalt();
            String hashedPassword = passwordUtils.hashPassword(user.getPassword(), salt);
            user.setHashedPassword(hashedPassword);
            user.setSalt(salt);

            DocumentReference docRef = userCollection.document();
            docRef.set(user).get();
            user.setId(docRef.getId());
            return user;
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to create user", e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public User createUser(String username, String email, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(password);
        return create(user);
    }

    @Override
    public Optional<User> findById(String id) {
        LoggingUtils.logServiceOperation("UserService", "findById", "Finding user by ID: " + id);
        
        try {
            DocumentSnapshot document = userCollection.document(id).get().get();
            if (document.exists()) {
                return Optional.of(document.toObject(User.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to find user by ID", e);
            throw new RuntimeException("Failed to find user by ID", e);
        }
    }

    @Override
    public List<User> findAll() {
        LoggingUtils.logServiceOperation("UserService", "findAll", "Fetching all users");
        
        try {
            List<QueryDocumentSnapshot> documents = userCollection.get().get().getDocuments();
            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                users.add(document.toObject(User.class));
            }
            return users;
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to fetch all users", e);
            throw new RuntimeException("Failed to fetch all users", e);
        }
    }

    @Override
    public User update(User user) {
        LoggingUtils.logServiceOperation("UserService", "update", "Updating user: " + user.getId());
        
        try {
            userCollection.document(user.getId()).set(user).get();
            return user;
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to update user", e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void delete(String userId) {
        LoggingUtils.logServiceOperation("UserService", "delete", "Deleting user: " + userId);
        
        try {
            userCollection.document(userId).delete().get();
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to delete user", e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        LoggingUtils.logServiceOperation("UserService", "authenticateUser", "Authenticating user: " + username);
        
        try {
            Optional<User> userOpt = findByUsername(username);
            if (userOpt.isEmpty()) {
                return false;
            }
            
            User user = userOpt.get();
            String hashedPassword = passwordUtils.hashPassword(password, user.getSalt());
            return hashedPassword.equals(user.getHashedPassword());
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to authenticate user", e);
            throw new RuntimeException("Failed to authenticate user", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LoggingUtils.logServiceOperation("UserService", "findByUsername", "Finding user by username: " + username);
        
        try {
            QuerySnapshot querySnapshot = userCollection.whereEqualTo("username", username).get().get();
            if (!querySnapshot.isEmpty()) {
                return Optional.of(querySnapshot.getDocuments().get(0).toObject(User.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to find user by username", e);
            throw new RuntimeException("Failed to find user by username", e);
        }
    }

    @Override
    public List<User> findAllActiveUsers() {
        LoggingUtils.logServiceOperation("UserService", "findAllActiveUsers", "Fetching all active users");
        
        try {
            QuerySnapshot querySnapshot = userCollection.whereEqualTo("isActive", true).get().get();
            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                users.add(document.toObject(User.class));
            }
            return users;
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to fetch active users", e);
            throw new RuntimeException("Failed to fetch active users", e);
        }
    }

    @Override
    public void updateUserStatus(String userId, boolean isActive) {
        LoggingUtils.logServiceOperation("UserService", "updateUserStatus", "Updating user status: " + userId);
        
        try {
            userCollection.document(userId).update("isActive", isActive).get();
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to update user status", e);
            throw new RuntimeException("Failed to update user status", e);
        }
    }

    @Override
    public boolean validateUserCredentials(String username, String password) {
        LoggingUtils.logServiceOperation("UserService", "validateUserCredentials", "Validating credentials for: " + username);
        
        try {
            Optional<User> userOpt = findByUsername(username);
            if (userOpt.isEmpty()) {
                return false;
            }
            
            User user = userOpt.get();
            String hashedPassword = passwordUtils.hashPassword(password, user.getSalt());
            return hashedPassword.equals(user.getHashedPassword());
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to validate credentials", e);
            throw new RuntimeException("Failed to validate credentials", e);
        }
    }

    @Override
    public void changePassword(String userId, String newPassword) {
        LoggingUtils.logServiceOperation("UserService", "changePassword", "Changing password for: " + userId);
        
        try {
            String salt = passwordUtils.generateSalt();
            String hashedPassword = passwordUtils.hashPassword(newPassword, salt);
            userCollection.document(userId).update(
                "hashedPassword", hashedPassword,
                "salt", salt
            ).get();
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to change password", e);
            throw new RuntimeException("Failed to change password", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        LoggingUtils.logServiceOperation("UserService", "findByEmail", "Finding user by email: " + email);
        
        try {
            QuerySnapshot querySnapshot = userCollection.whereEqualTo("email", email).get().get();
            if (!querySnapshot.isEmpty()) {
                return Optional.of(querySnapshot.getDocuments().get(0).toObject(User.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Failed to find user by email", e);
            throw new RuntimeException("Failed to find user by email", e);
        }
    }
}
