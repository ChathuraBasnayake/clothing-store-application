package com.icet.clothify.model.dao;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDAO {

    private Integer id;
    private String name;
    private String company;
    private String email;
    private String password;
    private String confirmPassword;
    private boolean isEmployee;

}
