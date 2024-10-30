package helloworld.studytogether.rewards.controller;

import helloworld.studytogether.rewards.service.RewardService;
import helloworld.studytogether.rewards.dto.UserRewardsDTO;
import helloworld.studytogether.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RewardsController {

  private RewardService rewardService;

  @Autowired
  public RewardsController(RewardService rewardService) {
    this.rewardService = rewardService;
  }


  @GetMapping("/rewards/user/{userId}")
  public ResponseEntity<UserRewardsDTO> getUserRewards(@PathVariable("userId") Long userId) {
    UserRewardsDTO rewards = rewardService.getUserRewards(userId);
    return ResponseEntity.ok(rewards);
  }
}