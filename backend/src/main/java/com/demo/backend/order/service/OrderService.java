package com.demo.backend.order.service;

import com.demo.backend.order.model.Order;
import com.demo.backend.order.model.OrderStatus;
import com.demo.backend.order.repository.OrderRepository;
import com.demo.backend.tutorial.model.Tutorial;
import com.demo.backend.user.model.User;
import com.demo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public Order createOrder(Tutorial tutorial, User user) {
        Order newOrder = Order
                .builder()
                .userId(user.getId())
                .build();

        List<Tutorial> tutorialList = new ArrayList<>();

        Optional.ofNullable(Optional.ofNullable(user.getOrder()).orElse(newOrder).getTutorialsOrder()).orElse(tutorialList).add(tutorial);

        if (user.getOrder() == null) {
            newOrder.setTutorialsOrder(tutorialList);
            user.setOrder(newOrder);

            Order savedOrderIfPresentOtherOrders = orderRepository.save(newOrder);
            userRepository.save(user);

            return savedOrderIfPresentOtherOrders;
        }

        user.getOrder().setOrderStatus(OrderStatus.OPENLY);

        Order savedOrder = orderRepository.save(user.getOrder());
        userRepository.save(user);

        return savedOrder;
    }

    public Order payOrder(User user) {
        Order order = user.getOrder();
        order.setOrderStatus(OrderStatus.SUCCESSFULLY);

        user.setOrder(null);
        orderRepository.save(order);
        userRepository.save(user);

        return order;
    }
}
