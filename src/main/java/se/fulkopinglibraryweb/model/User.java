package se.fulkopinglibraryweb.model;

import java.util.Set;
import java.util.HashSet;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.List;
import java.util.ArrayList;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @DocumentId
    private String id;
    private String username;
    private String passwordHash;
    private String salt;
    private String email;
    private String role;
    private boolean active;
    private String hashedPassword;
    private transient String password; // Transient field for temporary password storage
    private Set<String> borrowedItemIds = new HashSet<>();
    private Set<String> reservedItemIds = new HashSet<>();

    public List<String> getBorrowedItems() {
        return new ArrayList<>(borrowedItemIds);
    }

    public List<String> getReservedItems() {
        return new ArrayList<>(reservedItemIds);
    }

    public void addReservedItem(String itemId) {
        reservedItemIds.add(itemId);
    }

    public void removeReservedItem(String itemId) {
        reservedItemIds.remove(itemId);
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean canBorrow() {
        return borrowedItemIds.size() < 5; // Max 5 borrowed items
    }

    public void addBorrowedItem(String itemId) {
        if (canBorrow()) {
            borrowedItemIds.add(itemId);
        }
    }

    public boolean hasBorrowed(String itemId) {
        return borrowedItemIds.contains(itemId);
    }

    public void removeBorrowedItem(String itemId) {
        borrowedItemIds.remove(itemId);
    }
}
