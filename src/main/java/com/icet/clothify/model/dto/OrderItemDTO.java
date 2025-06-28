package com.icet.clothify.model.dto;

import lombok.Data;

@Data
public class OrderItemDTO {

    private Long id;
    private OrderDTO order;
    private Integer itemId;
    private String name;
    private Integer qty;
    private Double price;
    private Double total;

}
