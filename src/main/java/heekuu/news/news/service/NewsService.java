package heekuu.news.news.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class NewsService {



  private final RestTemplate restTemplate;
  private final String clientId = System.getenv("NAVER_CLIENT_ID");
  private final String clientSecret = System.getenv("NAVER_CLIENT_SECRET");
  private final String apiUrl = "https://openapi.naver.com/v1/search/news.json";
  private final ObjectMapper objectMapper = new ObjectMapper();

  public NewsService() {
    this.restTemplate = new RestTemplate();
  }

  public ResponseEntity<JsonNode> fetchNewsData(String query, int display, int start, String sort) {
    if (!"sim".equals(sort) && !"date".equals(sort)) {
      throw new IllegalArgumentException("Invalid sort value. Only 'sim' or 'date' are allowed.");
    }

    String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
        .queryParam("query", query)
        .queryParam("display", display)
        .queryParam("start", start)
        .queryParam("sort", sort)
        .toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Naver-Client-Id", clientId);
    headers.set("X-Naver-Client-Secret", clientSecret);
    headers.set("Accept", "application/json");

    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    try {
      JsonNode jsonResponse = objectMapper.readTree(response.getBody());
      return ResponseEntity.status(response.getStatusCode()).body(jsonResponse);
    } catch (IOException e) {
      throw new RuntimeException("Failed to parse JSON response", e);
    }
  }
}