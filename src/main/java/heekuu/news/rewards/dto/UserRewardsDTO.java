package heekuu.news.rewards.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRewardsDTO {
  private Long userId;
  private String nickname;
  private int points;
}