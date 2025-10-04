//package ru.is1.config.utils;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.util.Base64;
//
//public class PasswordUtil {
//
//    private static final SecureRandom random = new SecureRandom();
//
//    public static String generateSalt() {
//        byte[] salt = new byte[16];
//        random.nextBytes(salt);
//        return Base64.getEncoder().encodeToString(salt);
//    }
//
//    public static String hashPassword(String password, String salt) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            md.update(Base64.getDecoder().decode(salt));
//            byte[] hashedPassword = md.digest(password.getBytes());
//            return Base64.getEncoder().encodeToString(hashedPassword);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Error hashing password", e);
//        }
//    }
//
//    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
//        String newHash = hashPassword(password, salt);
//        return newHash.equals(hashedPassword);
//    }
//
//    public static PasswordHashResult hashPassword(String password) {
//        String salt = generateSalt();
//        String hashedPassword = hashPassword(password, salt);
//        return new PasswordHashResult(hashedPassword, salt);
//    }
//
//    public static class PasswordHashResult {
//        private final String hashedPassword;
//        private final String salt;
//
//        public PasswordHashResult(String hashedPassword, String salt) {
//            this.hashedPassword = hashedPassword;
//            this.salt = salt;
//        }
//
//        public String getHashedPassword() { return hashedPassword; }
//        public String getSalt() { return salt; }
//    }
//}