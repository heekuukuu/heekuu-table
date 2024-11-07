package heekuu.news;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "heekuu.news")
public class newsApplication {

  public static void main(String[] args) {
    SpringApplication.run(newsApplication.class, args);
  }

}
