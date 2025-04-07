package se.fulkopinglibraryweb.service.impl;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import se.fulkopinglibraryweb.repository.UserRepository;
import se.fulkopinglibraryweb.service.interfaces.UserService;
import se.fulkopinglibraryweb.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fulkopinglibraryweb.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {
    
    @SuppressWarnings("unused")
    private final Firestore firestore;
    
    @SuppressWarnings("unused")
    private final UserRepository userRepository;
    
    private final PasswordUtils passwordUtils;
    private final CollectionReference userCollection;
    private final Logger logger;

    public UserServiceImpl(Firestore firestore, UserRepository userRepository, PasswordUtils passwordUtils) {
        this.firestore = firestore;
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
        this.userCollection = firestore.collection("users");
        this.logger = LoggerFactory.getLogger(UserServiceImpl.class);
    }

    @Override
    public User create(User user) {
        logger.info("Creating new user: {}", user.getUsername());
        
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
            logger.error("Failed to create user: {}", e.getMessage());
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
        logger.info("Finding user by ID: {}", id);
        
        try {
            DocumentSnapshot document = userCollection.document(id).get().get();
            if (document.exists()) {
                return Optional.of(document.toObject(User.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to find user by ID: {}", e.getMessage());
            throw new RuntimeException("Failed to find user by ID", e);
        }
    }

    @Override
    public List<User> findAll() {
        logger.info("Fetching all users");
        
        try {
            List<QueryDocumentSnapshot> documents = userCollection.get().get().getDocuments();
            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                users.add(document.toObject(User.class));
            }
            return users;
        } catch (Exception e) {
            logger.error("Failed to fetch all users: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch all users", e);
        }
    }

    @Override
    public User update(User user) {
        logger.info("Updating user: {}", user.getId());
        
        try {
            userCollection.document(user.getId()).set(user).get();
            return user;
        } catch (Exception e) {
            logger.error("Failed to update user: {}", e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void delete(String userId) {
        logger.info("Deleting user: {}", userId);
        
        try {
            userCollection.document(userId).delete().get();
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        logger.info("Authenticating user: {}", username);
        
        try {
            Optional<User> userOpt = findByUsername(username);
            if (userOpt.isEmpty()) {
                return false;
            }
            
            User user = userOpt.get();
            String hashedPassword = passwordUtils.hashPassword(password, user.getSalt());
            return hashedPassword.equals(user.getHashedPassword());
        } catch (Exception e) {
            logger.error("Failed to authenticate user: {}", e.getMessage());
            throw new RuntimeException("Failed to authenticate user", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        logger.info("Finding user by username: {}", username);
        
        try {
            QuerySnapshot querySnapshot = userCollection.whereEqualTo("username", username).get().get();
            if (!querySnapshot.isEmpty()) {
                return Optional.of(querySnapshot.getDocuments().get(0).toObject(User.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to find user by username: {}", e.getMessage());
            throw new RuntimeException("Failed to find user by username", e);
        }
    }

    @Override
    public List<User> findAllActiveUsers() {
        logger.info("Fetching all active users");
        
        try {
            QuerySnapshot querySnapshot = userCollection.whereEqualTo("isActive", true).get().get();
            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                users.add(document.toObject(User.class));
            }
            return users;
        } catch (Exception e) {
            logger.error("Failed to fetch active users: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch active users", e);
        }
    }

    @Override
    public void updateUserStatus(String userId, boolean isActive) {
        logger.info("Updating user status: {}", userId);
        
        try {
            userCollection.document(userId).update("isActive", isActive).get();
        } catch (Exception e) {
            logger.error("Failed to update user status: {}", e.getMessage());
            throw new RuntimeException("Failed to update user status", e);
        }
    }

    @Override
    public boolean validateUserCredentials(String username, String password) {
        logger.info("Validating credentials for: {}", username);
        
        try {
            Optional<User> userOpt = findByUsername(username);
            if (userOpt.isEmpty()) {
                return false;
            }
            
            User user = userOpt.get();
            String hashedPassword = passwordUtils.hashPassword(password, user.getSalt());
            return hashedPassword.equals(user.getHashedPassword());
        } catch (Exception e) {
            logger.error("Failed to validate credentials: {}", e.getMessage());
            throw new RuntimeException("Failed to validate credentials", e);
        }
    }

    @Override
    public void changePassword(String userId, String newPassword) {
        logger.info("Changing password for: {}", userId);
        
        try {
            String salt = passwordUtils.generateSalt();
            String hashedPassword = passwordUtils.hashPassword(newPassword, salt);
            userCollection.document(userId).update(
                "hashedPassword", hashedPassword,
                "salt", salt
            ).get();
        } catch (Exception e) {
            logger.error("Failed to change password: {}", e.getMessage());
            throw new RuntimeException("Failed to change password", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        
        try {
            QuerySnapshot querySnapshot = userCollection.whereEqualTo("email", email).get().get();
            if (!querySnapshot.isEmpty()) {
                return Optional.of(querySnapshot.getDocuments().get(0).toObject(User.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to find user by email: {}", e.getMessage());
            throw new RuntimeException("Failed to find user by email", e);
        }
    }
}
