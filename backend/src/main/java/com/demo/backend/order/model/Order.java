package com.demo.backend.order.model;

import com.demo.backend.tutorial.model.Tutorial;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Tutorial> tutorialsOrder = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
