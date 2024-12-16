package heekuu.table.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  @LastModifiedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = truncateToMinutes(LocalDateTime.now());
    this.updatedAt = truncateToMinutes(LocalDateTime.now());
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = truncateToMinutes(LocalDateTime.now());
  }

  private LocalDateTime truncateToMinutes(LocalDateTime dateTime) {
    return dateTime.withSecond(0).withNano(0);
  }
}
