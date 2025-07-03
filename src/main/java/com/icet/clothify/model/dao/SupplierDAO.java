package com.icet.clothify.model.dao;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "suppliers")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String company;
    private String email;
    private String mobileNO;


}