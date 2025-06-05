package com.icet.clothify.model.dao;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDAO {

    private String id;
    private String items;
    private Double total;
    private String paymentMethod;
    private String employeeId;

}
