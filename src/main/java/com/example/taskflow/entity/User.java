package com.example.taskflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class User {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id")
    private Long id;

@Column(
nullable = false,
length = 50,
name = "username"
)
private String username; // display name — NOT unique, many users may share one
    @Column(
            nullable = false,
            unique = true,
            length = 100,
            name = "email"
    )
private String email;
    @Column(name = "password",
    nullable = false)
private String password;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
private LocalDateTime createdAt;

    // relation
    // user can have one or more task
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY // prevents an unnecessary heavy database query
    )
    private List<Task> task = new ArrayList<>();

    //user can one or more  categories
    @OneToMany(mappedBy = "user",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY // prevents an unnecessary heavy database query
            // it will only load the data explicit
    )
    private List<Category> categories = new ArrayList<>();
    // bill kent
//    "Every non-key column must provide a fact about the key [1NF],"
//    "the whole key [2NF], and nothing but the key [3NF], so help me Codd."
    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
