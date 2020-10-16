package cn.nihility.unify.config;

import cn.nihility.unify.util.TimeUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
public class JacksonConfiguration {

    /**
     * 处理序列化后的 1.8 的日期时间格式
     */
    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());

        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());

        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value,
                              JsonGenerator jsonGenerator,
                              SerializerProvider provider)
                throws IOException {
            if (value == null) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeNumber(TimeUtil.toMilli(value));
            }
        }
    }

    static class LocalDateSerializer extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator jsonGenerator,
                              SerializerProvider provider) throws IOException {
            if (value == null) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeNumber(TimeUtil.toMilli(value));
            }
        }
    }

    static class LocalTimeSerializer extends JsonSerializer<LocalTime> {
        @Override
        public void serialize(LocalTime value, JsonGenerator jsonGenerator, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeNumber(TimeUtil.toMilli(value));
            }
        }
    }

    static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext deserializationContext)
                throws IOException {
            if (parser.hasTokenId(JsonTokenId.ID_NUMBER_INT)) {
                String string = parser.getText().trim();
                if (string.length() == 0) {
                    return null;
                }
                return TimeUtil.toLocalDateTime(Long.parseLong(string));
            }
            return null;
        }
    }

    static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser parser, DeserializationContext deserializationContext)
                throws IOException {
            if (parser.hasTokenId(JsonTokenId.ID_NUMBER_INT)) {
                String string = parser.getText().trim();
                if (string.length() == 0) {
                    return null;
                }
                return TimeUtil.toLocalDate(Long.parseLong(string));
            }
            return null;
        }
    }

    static class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(JsonParser parser, DeserializationContext deserializationContext)
                throws IOException {
            if (parser.hasTokenId(JsonTokenId.ID_NUMBER_INT)) {
                String string = parser.getText().trim();
                if (string.length() == 0) {
                    return null;
                }
                return TimeUtil.toLocalTime(Long.parseLong(string));
            }
            return null;
        }
    }

}
