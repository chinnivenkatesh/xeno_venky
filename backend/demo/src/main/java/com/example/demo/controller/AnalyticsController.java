package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.model.OrderTable;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.ShopifyService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // <--- This is the fix for "Network Error"
public class AnalyticsController {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ShopifyService shopifyService;

    public AnalyticsController(CustomerRepository customerRepository, 
                               OrderRepository orderRepository,
                               ShopifyService shopifyService) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.shopifyService = shopifyService;
    }

    // --- SYNC BUTTON ENDPOINT ---
    @PostMapping("/sync")
    public String syncShopifyData() {
        return shopifyService.syncData();
    }

    // --- DASHBOARD API ENDPOINTS ---
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/orders")
    public List<OrderTable> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/dashboard-stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", customerRepository.count());
        stats.put("totalOrders", orderRepository.count());
        
        // Calculate Total Revenue
        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(OrderTable::getTotalPrice)
                .sum();
        stats.put("totalRevenue", totalRevenue);
        
        return stats;
    }
}