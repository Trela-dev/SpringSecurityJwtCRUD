package com.github.treladev.security;

import com.github.treladev.exception.AdminRoleAssignmentException;
import com.github.treladev.exception.AdminUpdateForbiddenException;
import com.github.treladev.model.User;
import com.github.treladev.service.UserService;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * Custom permission evaluator for updating users.
 * Handles role-based logic to restrict sensitive actions like updating ADMIN users or assigning ADMIN roles.
 */
@Component
public class UserUpdatePermissionEvaluator implements PermissionEvaluator {

    private final UserService userService;

    public UserUpdatePermissionEvaluator(UserService userService) {
        this.userService = userService;
    }

    /**
     * Main permission check for user update operations.
     *
     * @param authentication       the authentication of the current user
     * @param targetDomainObject   the ID of the user to update
     * @param permission           the new user data being submitted (updatedUser)
     * @return true if permission is granted, false otherwise (exception thrown)
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Long presentUserId = (Long) targetDomainObject;
        User presentUser = userService.findUserById(presentUserId);
        User updatedUser = (User) permission;


        boolean isCurrentUserAdmin = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isCurrentUserModerator = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MODERATOR"));

        boolean isTargetUserAdmin = presentUser.getRole().getName().equals("ROLE_ADMIN");
        boolean isAssignedRoleAdmin = updatedUser.getRole().getName().equals("ROLE_ADMIN");

        // Allow only moderators and admins to proceed
        if (isCurrentUserAdmin || isCurrentUserModerator) {
            // Case 1: Updating another ADMIN
            if (isTargetUserAdmin) {
                if (isCurrentUserAdmin) {
                    return true;
                } else {
                    throw new AdminUpdateForbiddenException("Only admins can update other admins.");
                }
            }

            // Case 2: Assigning ADMIN role
            if (isAssignedRoleAdmin) {
                if (isCurrentUserAdmin) {
                    return true;
                } else {
                    throw new AdminRoleAssignmentException("Only admins can assign the ADMIN role.");
                }
            }

            // Case 3: Regular update
            return true;
        }

        // If the user is neither a moderator nor an admin, deny access
        throw new AccessDeniedException("Insufficient permission to perform this operation.");
    }

    /**
     * Not implemented - for other kinds of permission checks (e.g. ACL).
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
