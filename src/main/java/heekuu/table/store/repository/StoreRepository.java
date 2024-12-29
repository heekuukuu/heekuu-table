package heekuu.table.store.repository;

import heekuu.table.store.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
  // 소유주의 모든 가게 조회
  List<Store> findAllByOwnerOwnerId(Long ownerId);
}
