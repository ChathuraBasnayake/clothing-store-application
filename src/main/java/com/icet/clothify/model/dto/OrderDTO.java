package com.icet.clothify.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private String id;
    @ToString.Exclude
    private List<OrderItemDTO> orderItems;
    private Double total;
    private String paymentMethod;
    private LocalDateTime orderDate;
    private String employeeId;

}
