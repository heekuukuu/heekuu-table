package heekuu.news.news.controller;

import com.fasterxml.jackson.databind.JsonNode;
import heekuu.news.news.service.NewsService;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class NewsController {

  private final NewsService newsService;

  public NewsController(NewsService newsService) {
    this.newsService = newsService;
  }

  @GetMapping("/api/news")
  public ResponseEntity<JsonNode> getNews(
      @RequestParam String query,
      @RequestParam(defaultValue = "10") int display,
      @RequestParam(defaultValue = "1") int start,
      @RequestParam(defaultValue = "sim") String sort) throws IOException {

    return newsService.fetchNewsData(query, display, start, sort);
  }
}