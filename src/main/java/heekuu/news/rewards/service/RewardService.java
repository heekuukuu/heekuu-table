package heekuu.news.rewards.service;

import heekuu.news.rewards.dto.UserRewardsDTO;
import heekuu.news.rewards.entity.Rewards;
import heekuu.news.rewards.repository.RewardRepository;
import heekuu.news.user.entity.User;
import heekuu.news.user.repository.UserRepository;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RewardService {

  private final RewardRepository rewardRepository;
  private final UserRepository userRepository;

  @Autowired
  public RewardService(RewardRepository rewardRepository, UserRepository userRepository) {
    this.rewardRepository = rewardRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public void addReward(Long userId, String action, int points) {

    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    // 포인트 지급 생성 내역
    Rewards rewards = new Rewards();
    rewards.setUser(user);
    rewards.setAction(action);
    rewards.setPoints(points);
    rewards.setEarnedAt(new Timestamp(System.currentTimeMillis()));

    // 포인트 내역 저장
    rewardRepository.save(rewards);

    // 사용자의 총 포인트 업데이트
    user.setTotalPoints(user.getTotalPoints() + points);
    userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public List<Rewards> getRewardsByUserId(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with ID:" + userId));
    return rewardRepository.findAllByUser(user);
  }

  //포인트 취소
  @Transactional
  public void cancelReward(Long rewardId) {
    // 적립 내역 조회
    Rewards reward = rewardRepository.findById(rewardId)
        .orElseThrow(() -> new IllegalArgumentException("Reward not found with ID: " + rewardId));

    // 해당 적립 내역의 사용자 조회
    User user = reward.getUser();

    // 총 포인트에서 해당 적립 포인트 차감
    user.setTotalPoints(user.getTotalPoints() - reward.getPoints());

    // 사용자 저장
    userRepository.save(user);

    // 적립 내역 삭제
    rewardRepository.delete(reward);
  }

  // 포인트 차감

  @Transactional
  public void subtractPoints(Long userId, String action, int points) {
    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    // 사용자의 총 포인트가 차감할 포인트보다 적은지 확인
    if (user.getTotalPoints() < points) {
      throw new IllegalArgumentException("Not enough points to subtract.");
    }

    // 총 포인트에서 차감
    user.setTotalPoints(user.getTotalPoints() - points);

    // 차감 내역 저장
    Rewards rewards = new Rewards();
    rewards.setUser(user);
    rewards.setAction(action);
    rewards.setPoints(-points); // 차감된 포인트를 음수로 설정
    rewards.setEarnedAt(new Timestamp(System.currentTimeMillis()));

    rewardRepository.save(rewards);
    userRepository.save(user);
  }

  public UserRewardsDTO getUserRewards(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    int points = rewardRepository.getPointsByUserId(userId).orElse(0); // 포인트를 계산하는 로직
    return new UserRewardsDTO(user.getUserId(), user.getNickname(), points);
  }

  public static class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
      super(message);
    }
  }
}