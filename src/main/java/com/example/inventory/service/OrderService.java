package com.example.inventory.service;

import com.example.inventory.dto.OrderItemRequest;
import com.example.inventory.dto.OrderRequest;
import com.example.inventory.exception.InsufficientStockException;
import com.example.inventory.exception.ResourceNotFoundException;
import com.example.inventory.model.*;
import com.example.inventory.repository.CustomerRepository;
import com.example.inventory.repository.OrderRepository;
import com.example.inventory.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    /** Resolves customer/product references by id, checks stock, and computes the order total. */
    @Transactional
    public Order createOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        Order order = new Order();
        order.setCustomer(customer);
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest line : request.getItems()) {
            Product product = productRepository.findById(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", line.getProductId()));

            if (product.getStockQuantity() < line.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for '" + product.getName()
                        + "'. Available: " + product.getStockQuantity() + ", requested: " + line.getQuantity());
            }
            product.setStockQuantity(product.getStockQuantity() - line.getQuantity());

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(line.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(line.getQuantity())));
            order.addItem(item);

            total = total.add(item.getLineTotal());
        }
        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() { return orderRepository.findAll(); }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    /** Cancels an order and restores stock. */
    @Transactional
    public Order cancel(Long id) {
        Order order = findById(id);
        if (order.getStatus() == OrderStatus.CANCELLED) return order;
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
