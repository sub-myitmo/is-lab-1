package ru.is1.domain.service;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class JsonService {

    private final ObjectMapper objectMapper;

    public JsonService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Настройки для более понятных ошибок
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.objectMapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        this.objectMapper.enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
    }

    public <T> T fromJson(InputStream input, Class<T> type) throws IOException {
        try {
            return objectMapper.readValue(input, type);
        } catch (JsonParseException e) {
            throw new IOException(createUserFriendlyMessage(e, "Ошибка разбора JSON"), e);
        } catch (JsonMappingException e) {
            throw new IOException(createUserFriendlyMessage(e, "Ошибка маппинга JSON"), e);
        }
    }

    private String createUserFriendlyMessage(JsonProcessingException e, String baseMessage) {
        StringBuilder message = new StringBuilder(baseMessage);

        JsonLocation location = e.getLocation();
        if (location != null) {
            message.append("\n▸ Место ошибки: строка ").append(location.getLineNr())
                    .append(", столбец ").append(location.getColumnNr());

            if (location.getSourceRef() instanceof String) {
                String source = (String) location.getSourceRef();
                message.append(extractContext(source, location));
            }
        }

        if (e instanceof JsonMappingException) {
            JsonMappingException jme = (JsonMappingException) e;
            List<JsonMappingException.Reference> path = jme.getPath();
            if (path != null && !path.isEmpty()) {
                message.append("\n▸ Путь к ошибке: ")
                        .append(path.stream()
                                .map(ref -> {
                                    if (ref.getFieldName() != null) {
                                        return ref.getFieldName();
                                    } else if (ref.getIndex() >= 0) {
                                        return "Person " + ref.getIndex() + ": ";
                                    }
                                    return "?";
                                })
                                .collect(Collectors.joining(".")));
            }
        }

        message.append("\n▸ Подсказка: проверьте структуру JSON и типы данных");

        return message.toString();
    }

    private String extractContext(String source, JsonLocation location) {
        try {
            int lineStart = 0;
            int lineNum = 1;

            // Находим начало строки с ошибкой
            for (int i = 0; i < source.length() && lineNum < location.getLineNr(); i++) {
                if (source.charAt(i) == '\n') {
                    lineNum++;
                    lineStart = i + 1;
                }
            }

            // Находим конец строки
            int lineEnd = source.indexOf('\n', lineStart);
            if (lineEnd == -1) lineEnd = source.length();

            String errorLine = source.substring(lineStart, lineEnd);
            int column = (int) location.getColumnNr() - 1;

            // Создаем визуальную подсказку
            StringBuilder context = new StringBuilder("\n▸ Контекст: ");
            context.append(errorLine);

            if (column > 0 && column < errorLine.length()) {
                context.append("\n▸          ");
                for (int i = 0; i < column; i++) {
                    context.append(" ");
                }
                context.append("^ здесь");
            }

            return context.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public String toJson(Object object) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonProcessingException(createUserFriendlyMessage(e, "Ошибка сериализации в JSON"), e) {};
        }
    }
}