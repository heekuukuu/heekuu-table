//package heekuu.news.Preferences.repository;
//
//import heekuu.news.Preferences.entity.Preferences;
//import heekuu.news.user.entity.User;
//import java.util.List;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface PreferencesRepository extends JpaRepository<Preferences, Long> {
//
// // 유저아이디로 키워드찾기
// @Query("SELECT p FROM Preferences p WHERE p.user.id = :userId")
// List<Preferences> findByUserId(@Param("userId") Long userId);
//
//
// boolean existsByUserAndCategoryAndKeyword(User user, String category, String keyword);
// // 페이징 지원
// @Query("SELECT p FROM Preferences p WHERE p.user.userId = :userId")
// Page<Preferences> findByUserId(@Param("userId") Long userId, Pageable pageable);
//
//}
////Page<Preferences> findByUser_Id(Long userId, Pageable pageable);