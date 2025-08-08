package com.icet.clothify.model.dao;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Order_Items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderDAO order;

    private Integer itemId;
    private String name;
    private Integer qty;
    private Double price;
    private Double total;
}
