package com.snacks.backend.repository;

import com.snacks.backend.entity.Widget;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetRepository extends JpaRepository<Widget, Long> {
  List<Widget> findAll();

  Widget save(Widget widget);

  @Query(value = "SELECT * FROM WIDGET WHERE NAME = ?1", nativeQuery = true)
  Widget findByName(String name);

  @Query(value = "SELECT * FROM WIDGET WHERE WIDGET_ID = ?1", nativeQuery = true)
  Widget findByWidgetId(Long id);
}
