package dev.kush.springaineo4j.config;

public class UserUtils {

    public static String getCurrentUser() {
//        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return jwt.getSubject();
        return "kush";
    }
}
