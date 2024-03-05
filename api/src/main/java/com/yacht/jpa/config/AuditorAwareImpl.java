package com.yacht.jpa.config;

import com.yacht.app.auth.domain.CustomUserDetails;
import com.yacht.app.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<User> {
    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() == "anonymousUser") {
            return Optional.empty();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return Optional.of(userDetails.getAccount().getUser());
    }
}
