package com.icet.clothify.model.dto;

import com.icet.clothify.controller.DashboardController;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private String id;
    private List<DashboardController.OrderItem> orderItems;
    private Double total;
    private String paymentMethod;
    private String employeeId;


}
