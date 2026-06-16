package com.journal.inspiration.common;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class UserContext {

    private static final String USER_ID_HEADER = "X-User-Id";

    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new SecurityException("无法获取当前请求上下文");
        }
        HttpServletRequest request = attributes.getRequest();
        String userIdStr = request.getHeader(USER_ID_HEADER);
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            throw new SecurityException("用户未登录，请先登录");
        }
        try {
            return Long.parseLong(userIdStr.trim());
        } catch (NumberFormatException e) {
            throw new SecurityException("无效的用户标识");
        }
    }

    public static Long getCurrentUserIdSafe() {
        try {
            return getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
