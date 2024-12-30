package heekuu.table.menu.repository;


import heekuu.table.menu.entity.Menu;
import heekuu.table.store.entity.Store;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

  List<Menu> findAllByStore(Store store);

}