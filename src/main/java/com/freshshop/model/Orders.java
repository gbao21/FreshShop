package com.freshshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Orders extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int orderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "customer_id",referencedColumnName = "customerId",nullable = true)
    private Customer customer;

    private String status;
    private double totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderDetails> orderDetails;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Orders orders = (Orders) o;
        return orderId == orders.orderId && Double.compare(orders.totalAmount, totalAmount) == 0 && Objects.equals(customer, orders.customer) && Objects.equals(status, orders.status) && Objects.equals(orderDetails, orders.orderDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId, customer, status, totalAmount, orderDetails);
    }
}
