package helloworld.studytogether.entity;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Column(nullable = false)
    private Date created_at;



    @Column(nullable = true)
    private Date updated_at;


    @PrePersist
    protected void onCreate() {
        this.created_at = Date.valueOf(LocalDate.now());
    }
    @PreUpdate
    protected void onUpdate() {
        this.updated_at = Date.valueOf(LocalDate.now());
}
}