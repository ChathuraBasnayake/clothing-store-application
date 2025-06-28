package com.icet.clothify.model.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private String id;
    private List<OrderItemDTO> orderItems;
    private Double total;
    private String paymentMethod;
    private String employeeId;

}
