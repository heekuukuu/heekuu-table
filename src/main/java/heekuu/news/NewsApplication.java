package heekuu.news;

import heekuu.news.news.service.NewsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;

@EnableJpaAuditing
@SpringBootApplication
public class NewsApplication {

  public static void main(String[] args) {
    SpringApplication.run(NewsApplication.class, args);

    NewsService newsService = new NewsService();
    ResponseEntity<JsonNode> response = newsService.fetchNewsData("인공지능", 10, 1, "date");

    if(response.getStatusCode().is2xxSuccessful()){
      System.out.println("뉴스데이터: " + response.getBody().toPrettyString());
    } else {
      System.out.println("API 요청실패: " + response.getStatusCode());
    }
  }
}