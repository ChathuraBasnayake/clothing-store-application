package com.icet.clothify.model.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Order_Items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key for this table

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderDAO order;

    private Integer itemId;
    private String name;
    private Integer qty;
    private Double price;
    private Double total;
}
