package ru.is1.domain.exception;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLExceptionHandler {

    public static String getUserFriendlyMessage(SQLException e) {
        String sqlState = e.getSQLState();
        String message = e.getMessage();

        // Определяем тип ошибки по SQLState
        if (sqlState != null) {
            switch (sqlState) {
                // PostgreSQL ошибки
                case "23505": // unique_violation
                    return extractDuplicateKeyMessage(message);

                case "23502": // not_null_violation
                    return extractNotNullViolationMessage(message);

                case "23503": // foreign_key_violation
                    return extractForeignKeyViolationMessage(message);

                case "22001": // string_data_right_truncation
                    return "Превышена максимальная длина поля. " +
                            extractFieldName(message);

                case "22003": // numeric_value_out_of_range
                    return "Числовое значение выходит за допустимые пределы. " +
                            extractFieldName(message);

                case "42703": // undefined_column
                    return "Ошибка в структуре базы данных: неверное имя поля. " +
                            "Пожалуйста, сообщите администратору.";
            }
        }

        // Общие ошибки соединения
        if (message != null) {
            if (message.contains("connection") || message.contains("Connection")) {
                return "Ошибка соединения с базой данных. Проверьте сетевое подключение.";
            }
            if (message.contains("timeout") || message.contains("Timeout")) {
                return "Превышено время ожидания ответа от базы данных.";
            }
            if (message.contains("deadlock")) {
                return "Обнаружен deadlock (взаимная блокировка). Попробуйте повторить операцию.";
            }
        }

        // Если не удалось определить конкретную ошибку
        return "Ошибка базы данных: " +
                (message != null ? truncateMessage(message) : "неизвестная ошибка");
    }

    private static String extractDuplicateKeyMessage(String message) {
        if (message == null) return "Нарушение уникальности данных.";

        // PostgreSQL: "duplicate key value violates unique constraint..."
        if (message.contains("duplicate key")) {
            String constraint = extractConstraintName(message);
            if (constraint.contains("passport")) {
                return "PassportID должен быть уникальным. Указанный PassportID уже существует.";
            } else if (constraint.contains("person_pkey")) {
                return "Нарушение уникальности первичного ключа.";
            }
            return "Нарушение уникальности по полю: " + constraint;
        }

        // MySQL: "Duplicate entry 'xxx' for key..."
        if (message.contains("Duplicate entry")) {
            String value = extractBetween(message, "'", "'");
            if (message.contains("passport")) {
                return "PassportID '" + value + "' уже существует.";
            }
            return "Дублирующееся значение: " + value;
        }

        return "Нарушение уникальности данных.";
    }

    private static String extractNotNullViolationMessage(String message) {
        if (message == null) return "Обязательное поле не заполнено.";

        String field = extractFieldName(message);
        if (field != null && !field.isEmpty()) {
            return "Обязательное поле '" + field + "' не может быть пустым.";
        }
        return "Обязательное поле не заполнено.";
    }

    private static String extractForeignKeyViolationMessage(String message) {
        if (message == null) return "Ошибка ссылочной целостности.";

        if (message.contains("coordinates") || message.contains("location")) {
            return "Ошибка ссылки на связанные данные (координаты или локация).";
        }
        return "Ошибка ссылочной целостности данных.";
    }

    private static String extractConstraintName(String message) {
        // Извлечение имени констрейнта из сообщения
        Pattern pattern = Pattern.compile("constraint \"([^\"]+)\"");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }

        pattern = Pattern.compile("for key '([^']+)'");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "неизвестное поле";
    }

    private static String extractFieldName(String message) {
        // Извлечение имени поля из сообщения
        Pattern pattern = Pattern.compile("column \"([^\"]+)\"");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }

        pattern = Pattern.compile("Field '([^']+)'");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }

        pattern = Pattern.compile("column '([^']+)'");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    private static String extractBetween(String text, String start, String end) {
        int startIndex = text.indexOf(start);
        if (startIndex == -1) return "";

        startIndex += start.length();
        int endIndex = text.indexOf(end, startIndex);
        if (endIndex == -1) return "";

        return text.substring(startIndex, endIndex);
    }

    private static String truncateMessage(String message) {
        if (message == null) return "";
        if (message.length() > 200) {
            return message.substring(0, 200) + "...";
        }
        return message;
    }
}