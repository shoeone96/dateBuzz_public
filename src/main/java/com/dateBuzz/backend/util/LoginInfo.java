package com.dateBuzz.backend.util;

public class LoginInfo {
    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

    public static Long getUserId() {
        return userIdThreadLocal.get();
    }

    public static void setUserId(Long userId) {
        userIdThreadLocal.set(userId);
    }

    public static void clear() {
        userIdThreadLocal.remove();
    }
}
