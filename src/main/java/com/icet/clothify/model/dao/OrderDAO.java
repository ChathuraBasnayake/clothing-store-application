package com.icet.clothify.model.dao;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Orders")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDAO {

    @Id
    private String id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<OrderItemDAO> orderItems;

    private Double total;
    private String paymentMethod;
    private String employeeId;
}
