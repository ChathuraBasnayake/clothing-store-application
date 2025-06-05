package com.icet.clothify.model.dao;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemDAO {

    private Integer id;
    private String name;
    private String category;
    private String size;
    private Double price;
    private Integer quantity;
    private Integer supplier_id;

}
