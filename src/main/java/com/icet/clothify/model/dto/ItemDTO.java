package com.icet.clothify.model.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    private String id;
    private String name;
    private String category;
    private String size;
    private Double price;
    private Integer quantity;
    private String supplierId;

}
