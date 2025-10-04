//package ru.is1.domain.service;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import ru.is1.config.utils.JwtUtil;
//import ru.is1.config.utils.PasswordUtil;
//import ru.is1.controller.dto.auth.AuthResponse;
//import ru.is1.controller.dto.auth.LoginRequest;
//import ru.is1.controller.dto.auth.RegisterRequest;
//import ru.is1.dal.dao.UserDAO;
//import ru.is1.dal.entity.User;
//
//import java.util.Optional;
//
//@ApplicationScoped
//public class AuthService {
//    @Inject
//    private UserDAO userDao;
//
//    public AuthResponse register(RegisterRequest request) {
//        // Проверка существования пользователя
//        if (userDao.existsByUsername(request.getUsername())) {
//            return new AuthResponse("Username already exists");
//        }
//
//        if (userDao.existsByEmail(request.getEmail())) {
//            return new AuthResponse("Email already exists");
//        }
//
//        // Валидация
//        if (request.getUsername() == null || request.getUsername().length() < 3) {
//            return new AuthResponse("Username must be at least 3 characters long");
//        }
//
//        if (request.getPassword() == null || request.getPassword().length() < 6) {
//            return new AuthResponse("Password must be at least 6 characters long");
//        }
//
//        // Хеширование пароля
//        PasswordUtil.PasswordHashResult hashResult = PasswordUtil.hashPassword(request.getPassword());
//
//        // Создание пользователя
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setPassword(hashResult.getHashedPassword());
//        user.setPasswordSalt(hashResult.getSalt());
//        user.setEmail(request.getEmail());
//
//        if (userDao.save(user)) {
//            String token = JwtUtil.generateToken(user.getUsername());
//            return new AuthResponse(token, user.getUsername());
//        } else {
//            return new AuthResponse("Registration failed");
//        }
//    }
//
//    public AuthResponse login(LoginRequest request) {
//        User user = userDao.findByUsername(request.getUsername()).orElse(null);
//        if (user == null) {
//            // Для безопасности не говорим, что пользователь не существует
//            return new AuthResponse("Invalid username or password");
//        }
//
//        // Проверка пароля с хэшем
//        boolean passwordValid = PasswordUtil.verifyPassword(
//                request.getPassword(),
//                user.getPasswordSalt(),
//                user.getPassword()
//        );
//
//        if (passwordValid) {
//            String token = JwtUtil.generateToken(user.getUsername());
//            return new AuthResponse(token, user.getUsername());
//        } else {
//            return new AuthResponse("Invalid username or password");
//        }
//    }
//
//    public Optional<User> getUserByUsername(String username) {
//        return userDao.findByUsername(username);
//    }
//}
