package garcias.api.identity.user.domain.entities;

import garcias.api.identity.user.domain.enums.UserRole;
import garcias.api.identity.user.domain.enums.UserStatus;
import garcias.api.identity.user.domain.valueobjects.Password;
import garcias.api.identity.user.domain.valueobjects.UserCode;
import garcias.api.identity.user.domain.valueobjects.UserId;
import garcias.api.identity.user.domain.valueobjects.UserName;

import java.time.LocalDateTime;

public class User {


    private UserId id;

    private UserName name;

    private UserCode userCode;

    private Password password;

    private UserRole role;

    private UserStatus status;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private User() {
    }

    public static User create(
            UserName name,
            UserCode userCode,
            Password password,
            UserRole role,
            UserStatus status
    ) {

        User user = new User();

        user.id = UserId.generate();
        user.name = name;
        user.userCode = userCode;
        user.password = password;
        user.role = role;
        user.status = status;

        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        return user;
    }

    public void changeName(UserName name) {

        if (name == null) {
            throw new IllegalArgumentException(
                    "User name cannot be null"
            );
        }

        this.name = name;
        updateTimestamp();
    }

    public void changePassword(Password newPassword) {

        if (newPassword == null) {
            throw new IllegalArgumentException(
                    "User password cannot be null"
            );
        }

        this.password = newPassword;
        updateTimestamp();
    }

    public void changeRole(UserRole role) {

        if (role == null) {
            throw new IllegalArgumentException(
                    "User role cannot be null"
            );
        }

        this.role = role;
        updateTimestamp();
    }

    public void changeStatus(UserStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "User status cannot be null"
            );
        }

        if (status == UserStatus.ACTIVE) {
            activate();
            return;
        }

        deactivate();
    }

    public void activate() {

        this.status = UserStatus.ACTIVE;
        updateTimestamp();
    }


    public void deactivate() {

        this.status = UserStatus.INACTIVE;
        updateTimestamp();
    }


    public void recordLogin() {

        this.lastLoginAt = LocalDateTime.now();
    }


    private void updateTimestamp() {

        this.updatedAt = LocalDateTime.now();
    }

    public static User restore(
            UserId id,
            UserName name,
            UserCode userCode,
            Password password,
            UserRole role,
            UserStatus status,
            LocalDateTime lastLoginAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {

        User user = new User();

        user.id = id;
        user.name = name;
        user.userCode = userCode;
        user.password = password;
        user.role = role;
        user.status = status;
        user.lastLoginAt = lastLoginAt;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;

        return user;
    }

    public UserId getId() {
        return id;
    }

    public UserName getName() {
        return name;
    }

    public UserCode getUserCode() {
        return userCode;
    }

    public Password getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
