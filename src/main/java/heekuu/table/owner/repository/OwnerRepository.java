package heekuu.table.owner.repository;

import heekuu.table.owner.entity.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
  Optional<Owner> findByEmail(String email); // 이메일로 검색
}
