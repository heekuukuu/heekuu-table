package heekuu.table.menu.repository;


import heekuu.table.menu.entity.Menu;
import heekuu.table.menu.type.MenuCategory;
import heekuu.table.store.entity.Store;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

  List<Menu> findAllByStore(Store store);

  // 특정 가게의 카테고리별 메뉴 조회
  List<Menu> findByStore_StoreIdAndCategory(Long storeId, MenuCategory category);
}