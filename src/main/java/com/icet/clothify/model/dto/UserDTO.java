package com.icet.clothify.model.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String id;
    private String name;
    private String company;
    private String email;
    private String password;
    private String confirmPassword;
    private Boolean isEmployee;

}
