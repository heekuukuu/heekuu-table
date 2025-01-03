package heekuu.table.store.repository;

import heekuu.table.owner.entity.Owner;
import heekuu.table.store.entity.Store;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
  // 소유주의 모든 가게 조회
  List<Store> findAllByOwnerOwnerId(Long ownerId);


  //주소랑이름으로 중복확인
  Optional<Store> findByNameAndAddress(String name, String address);
}
