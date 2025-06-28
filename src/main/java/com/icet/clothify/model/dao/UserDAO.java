package com.icet.clothify.model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;

@Entity
@Table(name = "user")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDAO {

    @Id
    private String id;
    private String name;
    private String company;
    private String email;
    private String password;
    @Transient
    private String confirmPassword;
    private Boolean isEmployee;

}
