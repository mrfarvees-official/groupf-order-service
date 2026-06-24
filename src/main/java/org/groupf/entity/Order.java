package org.groupf.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.groupf.annotation.PrefixedId;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @PrefixedId(prefix = "order_", sequenceName = "order_seq")
    @Column(name = "order_id", length = 20, nullable = false, updatable = false)
    private String orderId;

    @Column(name = "customer_id", length = 20, nullable = false, updatable = false)
    private String customerId;

    @Column(name = "product_id", length = 20, nullable = false, updatable = false)
    private String productId;

    @Column(name = "product_name")
    private String productName;

    private int quantity;

    @Column(name = "total_price")
    private double totalPrice;

    @Column (name = "order_date")
    private LocalDateTime orderDate;

    private String status;
}
