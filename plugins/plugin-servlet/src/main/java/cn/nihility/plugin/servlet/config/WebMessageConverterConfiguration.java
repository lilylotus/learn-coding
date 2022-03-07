package cn.nihility.plugin.servlet.config;

import cn.nihility.common.util.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Configuration
public class WebMessageConverterConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WebMessageConverterConfiguration.class);

    /**
     * POST body json 日期序列化
     */
    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.newObjectMapperInstance();
    }

    @Configuration
    static class DateFormatConverterConfig {

        static final ThreadLocal<SimpleDateFormat> SDF_DATE_TIME =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        static final ThreadLocal<SimpleDateFormat> SDF_DATE =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

        /*
         * 注意：这里 Converter 不能使用 lambda 形式，不然会报转换格式错误
         * 针对 GET 请求参数转换
         */

        @Bean
        public Converter<String, LocalDate> localDateConverter() {
            return new Converter<String, LocalDate>() {
                @Override
                public LocalDate convert(String source) {
                    return StringUtils.hasText(source) ?
                        LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                        null;
                }
            };
        }

        @Bean
        public Converter<String, LocalDateTime> localDateTimeConverter() {
            return new Converter<String, LocalDateTime>() {
                @Override
                public LocalDateTime convert(String source) {
                    return StringUtils.hasText(source) ?
                        LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
                        null;
                }
            };
        }

        @Bean
        public Converter<String, Date> dateConverter() {
            return new Converter<String, Date>() {
                @Override
                public Date convert(String source) {
                    Date result = null;
                    try {
                        result = StringUtils.hasText(source) ?
                            SDF_DATE_TIME.get().parse(source) :
                            null;
                    } catch (ParseException e) {
                        try {
                            result = SDF_DATE.get().parse(source);
                        } catch (ParseException ex) {
                            logger.error("解析转化字符串日期格式 [{}] 为 Date 异常", source, e);
                        }
                    } finally {
                        SDF_DATE_TIME.remove();
                        SDF_DATE.remove();
                    }
                    return result;
                }
            };
        }

    }

}
