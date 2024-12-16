//package heekuu.news.Preferences.entity;
//
//
//import heekuu.news.user.entity.User;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//// 개인 뉴스 카테고리
//public class Preferences {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @Column(name = "preferences_id", nullable = false)
//  private Long preferencesId;
//
//  @Column(nullable = false)
//  private String category;
//
//  @Column(length = 50)
//  private String keyword;
//
//
//  // User와의 관계 설정 (다대일 관계)
//  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "user_id", nullable = false)
//  private User user;
//
//  public Preferences(User user, String category, String keyword) {
//    this.user = user;
//    this.category = category;
//    this.keyword = keyword;
//  }
//}