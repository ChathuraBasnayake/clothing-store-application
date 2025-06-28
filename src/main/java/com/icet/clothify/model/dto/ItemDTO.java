package com.icet.clothify.model.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    private Integer id;
    private String name;
    private String category;
    private String description;
    private String size;
    private Double price;
    private Integer quantity;
    private Integer supplier_id;

}
