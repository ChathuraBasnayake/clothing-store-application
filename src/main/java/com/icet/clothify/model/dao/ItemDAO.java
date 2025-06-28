package com.icet.clothify.model.dao;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String category;
    private String description;
    private String size;
    private Double price;
    private Integer quantity;
    private Integer supplier_id;

}
