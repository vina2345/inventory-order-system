package com.example.inventory.controller;

import com.example.inventory.dto.OrderRequest;
import com.example.inventory.model.Order;
import com.example.inventory.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) { this.service = service; }

    @GetMapping
    public List<Order> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public Order getOne(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@Valid @RequestBody OrderRequest request) { return service.createOrder(request); }

    /** Cancels the order and restores stock. */
    @DeleteMapping("/{id}")
    public Order cancel(@PathVariable Long id) { return service.cancel(id); }
}
