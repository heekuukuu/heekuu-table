package heekuu.table.store.repository;

import heekuu.table.owner.entity.Owner;
import heekuu.table.store.entity.Store;
import heekuu.table.store.type.StoreCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

  // 소유주의 모든 가게 조회
  List<Store> findAllByOwnerOwnerId(Long ownerId);

  // 🔍 Owner ID로 가게 전체 조회
  Optional<Store> findByOwner_OwnerId(Long ownerId);

  //주소랑이름으로 중복확인
  Optional<Store> findByNameAndAddress(String name, String address);

  //로그인된 오너가 가게의 소유여부
  boolean existsByOwner_OwnerId(Long authenticatedOwnerId);

  List<Store> findByCategory(StoreCategory category);  // 카테고리별 가게 조회

  List<Store> findByNameContainingIgnoreCase(String name); // 가게이름 유사검색포함(대소문자구분 X )
}
