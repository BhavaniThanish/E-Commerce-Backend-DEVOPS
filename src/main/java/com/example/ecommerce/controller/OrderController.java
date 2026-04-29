package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.OrderItemDto;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setProductId(item.getProductId());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
