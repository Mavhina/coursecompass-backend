package com.coursecompass.backend.domain;
import jakarta.persistence.*;
import lombok.*;

@Entity // jakarta.persistence.*;
@Table(name = "users") // jakarta.persistence.*;
@Getter // lombok.*.lambok will generate it at run time
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

   //optional
    private String role = "USER"; // I'll later add an admin

}
