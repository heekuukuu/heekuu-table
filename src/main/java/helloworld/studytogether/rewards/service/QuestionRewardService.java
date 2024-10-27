package helloworld.studytogether.rewards.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionRewardService {

  private final RewardService rewardService;

  @Autowired
  public QuestionRewardService(RewardService rewardService) {
    this.rewardService = rewardService;
  }

  public void rewardForQuestion(Long userId) {
    int rewardPoints = 50; // 보상 포인트를 동적으로 가져오도록 설정 가능
    String actionDescription = "질문 등록";

    rewardService.addReward(userId, actionDescription, rewardPoints);
  }
}