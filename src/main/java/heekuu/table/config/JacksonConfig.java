package heekuu.table.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  // 전역날짜포맷 기본설정
  private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";


  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    // JavaTimeModule 생성 및 전역 포맷 지정
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(LocalDateTime.class,
        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
    javaTimeModule.addDeserializer(LocalDateTime.class,
        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));

    // 모듈 등록
    mapper.registerModule(javaTimeModule);

    // 날짜를 Timestamp 대신 지정된 포맷으로 직렬화
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return mapper;
  }
}