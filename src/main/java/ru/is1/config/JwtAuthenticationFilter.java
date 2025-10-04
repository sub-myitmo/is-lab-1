//package ru.is1.config;
//
//import jakarta.annotation.Priority;
//import jakarta.ws.rs.Priorities;
//import jakarta.ws.rs.container.ContainerRequestContext;
//import jakarta.ws.rs.container.ContainerRequestFilter;
//import jakarta.ws.rs.core.HttpHeaders;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.ext.Provider;
//import ru.is1.config.utils.JwtUtil;
//
//import java.io.IOException;
//
//@Provider
//@Priority(Priorities.AUTHENTICATION)
//public class JwtAuthenticationFilter implements ContainerRequestFilter {
//
//    private static final String AUTHENTICATION_SCHEME = "Bearer";
//    private static final String[] PUBLIC_PATHS = {
//            "/auth/login",
//            "/auth/register",
//            "/auth/verify"
//    };
//
//    @Override
//    public void filter(ContainerRequestContext requestContext) throws IOException {
//        String path = requestContext.getUriInfo().getPath();
//
//        // Пропускаем публичные endpoints
//        if (isPublicPath(path)) {
//            return;
//        }
//        System.err.println(path);
//
//        // Получаем заголовок Authorization
//        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
//
//        // Если заголовок отсутствует
//        if (authorizationHeader == null) {
//            abortWithUnauthorized(requestContext, "Missing authorization header");
//            return;
//        }
//
//        // Проверяем формат заголовка
//        if (!authorizationHeader.startsWith(AUTHENTICATION_SCHEME + " ")) {
//            abortWithUnauthorized(requestContext, "Invalid authorization format");
//            return;
//        }
//
//        // Извлекаем токен
//        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
//
//        // Валидируем токен
//        String username = JwtUtil.validateToken(token);
//        if (username == null) {
//            abortWithUnauthorized(requestContext, "Invalid or expired token");
//            return;
//        }
//
//        // Устанавливаем username в контекст для использования в ресурсах
//        requestContext.setProperty("username", username);
//    }
//
//    private boolean isPublicPath(String path) {
//        for (String publicPath : PUBLIC_PATHS) {
//            if (path.startsWith(publicPath)) { // Убираем "/api" из PUBLIC_PATHS
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
//        requestContext.abortWith(
//                Response.status(Response.Status.UNAUTHORIZED)
//                        .entity("{\"message\": \"" + message + "\"}")
//                        .build()
//        );
//    }
//}
