package com.icet.clothify.model.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDTO {

    private Integer id;
    private String name;
    private String company;
    private String email;
    private String item;

}
