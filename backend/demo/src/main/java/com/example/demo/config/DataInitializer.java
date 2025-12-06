package com.example.demo.config;

import com.example.demo.model.Customer;
import com.example.demo.model.OrderTable;
import com.example.demo.model.Product;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

// Disabled - causes JVM exit issues
// @Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final Random random = new Random();

    public DataInitializer(CustomerRepository customerRepository,
                          ProductRepository productRepository,
                          OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        try {
            // Clear existing data
            System.out.println("🗑️  Clearing existing sample data...");
            orderRepository.deleteAll();
            productRepository.deleteAll();
            customerRepository.deleteAll();
            
            System.out.println("✅ Sample data removed. Dashboard is now empty.");
            System.out.println("   📊 Total Customers: " + customerRepository.count());
            System.out.println("   📦 Total Products: " + productRepository.count());
            System.out.println("   📈 Total Orders: " + orderRepository.count());
        } catch (Exception e) {
            System.err.println("❌ Error during data initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeCustomers() {
        String[] firstNames = {"John", "Sarah", "Mike", "Emma", "David", "Lisa", "Robert", "Jennifer", "James", "Mary", 
                              "William", "Patricia", "Richard", "Linda", "Joseph", "Barbara", "Thomas", "Susan", "Charles", "Jessica",
                              "Christopher", "Karen", "Daniel", "Nancy", "Matthew", "Lisa", "Mark", "Betty", "Donald", "Margaret"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Wilson", "Moore",
                             "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Lee", "Perez",
                             "Edwards", "Collins", "Reeves", "Stewart", "Sanchez", "Morris", "Rogers", "Murphy", "Cook", "Morgan"};
        String[] domains = {"gmail.com", "yahoo.com", "outlook.com", "hotmail.com", "protonmail.com", "icloud.com"};

        for (int i = 0; i < 50; i++) {
            Customer customer = new Customer();
            customer.setShopifyId((long) (1000 + i));
            customer.setFirstName(firstNames[random.nextInt(firstNames.length)]);
            customer.setLastName(lastNames[random.nextInt(lastNames.length)]);
            customer.setEmail(customer.getFirstName().toLowerCase() + "." + customer.getLastName().toLowerCase() + i + "@" + domains[random.nextInt(domains.length)]);
            customer.setPhone("+1-" + (600 + random.nextInt(400)) + "-" + String.format("%03d", random.nextInt(1000)) + "-" + String.format("%04d", random.nextInt(10000)));
            customer.setTotalSpent((double) (500 + random.nextInt(10000)));
            customer.setTenantId("xeno-venky");
            customerRepository.save(customer);
        }
    }

    private void initializeProducts() {
        String[] products = {
                "Wireless Headphones Pro", "USB-C Cable 2m", "Laptop Stand Adjustable", "Mechanical Keyboard RGB",
                "4K Webcam Ultra HD", "Portable SSD 1TB", "Desk Lamp LED", "Monitor Stand Aluminum",
                "Cooling Pad Gaming", "Mouse Pad XL Extended", "HDMI 2.1 Cable 3m", "USB Hub 7-Port Active",
                "Phone Stand Flexible", "Cable Organizer Set", "Screen Protector Tempered",
                "Wireless Mouse Pro", "Mechanical Switch Tester", "Monitor Light Bar", "Desk Pad Large",
                "Keyboard Wrist Rest", "Mouse Bungee Cord", "Monitor Arm VESA", "USB Splitter Hub",
                "Portable Speaker Bluetooth", "USB Flash Drive 64GB", "Wireless Charger Pad", "Laptop Cooling Stand",
                "External Keyboard Wireless", "Screen Magnifier Stand", "Cable Management Kit"
        };
        String[] vendors = {"TechCorp", "ElectroHub", "DigitalPro", "GadgetWorld", "Innovation Labs", 
                           "TechGenius", "SmartTech", "ProGear", "EliteTech", "NextGen Electronics"};

        for (int i = 0; i < products.length; i++) {
            Product product = new Product();
            product.setShopifyProductId((long) (2000 + i));
            product.setTitle(products[i]);
            product.setVendor(vendors[random.nextInt(vendors.length)]);
            product.setProductType("Electronics");
            product.setPrice((double) (19.99 + random.nextInt(400)));
            product.setTenantId("xeno-venky");
            productRepository.save(product);
        }
    }

    private void initializeOrders() {
        LocalDateTime baseDate = LocalDateTime.now().minusDays(90);
        String[] statuses = {"paid", "pending", "refunded", "cancelled"};

        // Create 200 orders across 90 days for better graph visualization
        for (int i = 0; i < 200; i++) {
            OrderTable order = new OrderTable();
            order.setShopifyOrderId((long) (3000 + i));
            order.setTotalPrice((double) (29.99 + random.nextInt(5000)));
            order.setCurrency("USD");
            
            // Weight the statuses: 70% paid, 15% pending, 10% refunded, 5% cancelled
            int statusRand = random.nextInt(100);
            if (statusRand < 70) {
                order.setFinancialStatus("paid");
            } else if (statusRand < 85) {
                order.setFinancialStatus("pending");
            } else if (statusRand < 95) {
                order.setFinancialStatus("refunded");
            } else {
                order.setFinancialStatus("cancelled");
            }
            
            order.setTenantId("xeno-venky");
            
            // Spread orders across 90 days with varied times throughout each day
            LocalDateTime orderDate = baseDate.plusDays(random.nextInt(90))
                    .plusHours(random.nextInt(24))
                    .plusMinutes(random.nextInt(60));
            order.setProcessedAt(orderDate);
            
            // Link to a random customer
            long customerCount = customerRepository.count();
            if (customerCount > 0) {
                long randomCustomerId = 1 + random.nextLong(customerCount);
                customerRepository.findById(randomCustomerId).ifPresent(order::setCustomer);
            }
            
            orderRepository.save(order);
        }
    }
}
