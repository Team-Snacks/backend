package com.snacks.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserWidget implements Serializable {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "widget_id")
  private Widget widget;

  @Column(name = "title")
  private String title;

  @Column(name = "x", nullable = false)
  private Integer x;

  @Column(name = "y", nullable = false)
  private Integer y;

  @Column(name = "w", nullable = false)
  private Integer w;

  @Column(name = "h", nullable = false)
  private Integer h;

  @Column(name = "data")
  private String data;

  @Column(name = "updated_at")
  private LocalDateTime updated_at;


  public void setUserId(Long userId) {
    user.setId(userId);
  }

  public void setWidgetId(Long widgetId) {
    widget.setId(widgetId);
  }

  
  public Long getUserId() {
    return user.getId();
  }

  public Long getWidgetId() {
    return widget.getId();
  }


  public void update(Integer x, Integer y, Integer w, Integer h, String data) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.data = data;
  }
}
