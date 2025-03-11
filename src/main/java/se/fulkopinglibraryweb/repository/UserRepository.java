package se.fulkopinglibraryweb.repository;

import se.fulkopinglibraryweb.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for User entities.
 * Extends FirestoreRepository to inherit Firestore-specific operations.
 */
@Repository
public interface UserRepository extends FirestoreRepository<User, String> {
    
    /**
     * Find a user by their email address.
     *
     * @param email The email address to search for
     * @return A CompletableFuture containing the user with the specified email, or null if not found
     */
    CompletableFuture<User> findByEmail(String email);
    
    /**
     * Check if a user with the specified email exists.
     *
     * @param email The email address to check
     * @return A CompletableFuture containing a boolean indicating if the user exists
     */
    CompletableFuture<Boolean> existsByEmail(String email);
    
    /**
     * Find users by their role.
     *
     * @param role The role to search for
     * @return A CompletableFuture containing a list of users with the specified role
     */
    CompletableFuture<List<User>> findByRole(String role);
    
    /**
     * Find users by their status (active/inactive).
     *
     * @param active The status to search for
     * @return A CompletableFuture containing a list of users with the specified status
     */
    CompletableFuture<List<User>> findByActive(boolean active);
    
    /**
     * Find users who have borrowed items.
     *
     * @return A CompletableFuture containing a list of users who have borrowed items
     */
    CompletableFuture<List<User>> findUsersWithLoans();
}
