package kopo.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/user")
public class UserInfoController {
    private static final Logger log = LoggerFactory.getLogger(UserInfoController.class);
    @GetMapping("/me")
    public Map<String, Object> getMe(org.springframework.security.core.Authentication authentication) {
        log.info("[UserInfoController] /user/me called");
        String managerId = null;
        try {
            if (authentication == null) {
                log.warn("Authentication is null!");
                return Map.of("managerId", null);
            }
            Object principal = authentication.getPrincipal();
            log.info("Authentication: {}", authentication);
            log.info("Principal: {}", principal);
            log.info("Principal class: {}", principal == null ? "null" : principal.getClass().getName());
            if (principal instanceof String) {
                managerId = (String) principal;
                log.info("Principal is String, managerId: {}", managerId);
            } else if (principal instanceof Jwt jwt) {
                managerId = jwt.getClaimAsString("managerId");
                log.info("Principal is Jwt, managerId: {}", managerId);
            } else {
                try {
                    managerId = principal != null ? principal.toString() : null;
                    log.info("Principal is Other, managerId: {}", managerId);
                } catch (Exception e) {
                    log.error("Principal toString error", e);
                }
            }
        } catch (Exception e) {
            log.error("/user/me 예외 발생", e);
            managerId = null;
        }
        return Map.of("managerId", managerId);
    }
}
