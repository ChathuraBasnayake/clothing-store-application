package com.icet.clothify.model.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private String id;
    private String items;
    private Double total;
    private String paymentMethod;
    private String employeeId;

}
