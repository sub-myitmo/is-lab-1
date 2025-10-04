//package ru.is1.dal.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "users")
//@Getter
//@Setter
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Column(name = "password_salt", nullable = false)
//    private String passwordSalt;
//
//    @Column(unique = true, nullable = false)
//    private String email;
//
//    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Person> persons = new ArrayList<>();
//
//
//    // Конструкторы
//    public User() {}
//
//    public User(String username, String password, String email) {
//        this.username = username;
//        this.password = password;
//        this.email = email;
//    }
//
//}
