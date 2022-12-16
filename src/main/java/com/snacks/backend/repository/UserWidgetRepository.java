package com.snacks.backend.repository;

import com.snacks.backend.entity.UserWidget;
import com.snacks.backend.entity.UserWidgetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWidgetRepository extends JpaRepository<UserWidget, UserWidgetId> {
  UserWidget save(UserWidget userWidget);

  @Query(value = "SELECT * FROM USER_WIDGET WHERE USER_ID =?1", nativeQuery = true)
  UserWidget[] findWidgets(Long id);

}
