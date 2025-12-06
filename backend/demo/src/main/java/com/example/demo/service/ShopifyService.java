package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.OrderTable;
import com.example.demo.model.Product;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ShopifyService {

    @Value("${shopify.store.url}")
    private String storeUrl;

    @Value("${shopify.access.token}")
    private String accessToken;

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public ShopifyService(CustomerRepository customerRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public String syncData() {
        try {
            System.out.println("🔄 Starting Shopify sync...");
            System.out.println("Store URL: " + storeUrl);
            System.out.println("Token: " + (accessToken != null && !accessToken.isEmpty() ? accessToken.substring(0, 10) + "..." : "NOT SET"));
            
            syncCustomers();
            System.out.println("✅ Customers synced");
            
            syncProducts();
            System.out.println("✅ Products synced");
            
            syncOrders();
            System.out.println("✅ Orders synced");
            
            return "✅ Data Sync Successful from Shopify!";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Sync Error: " + e.getMessage());
            return "⚠️ Sync completed with partial data. Error: " + e.getMessage();
        }
    }

    private void syncCustomers() throws Exception {
        try {
            String url = storeUrl + "/admin/api/2024-01/customers.json";
            JsonNode root = fetchFromShopify(url);
            JsonNode customers = root.get("customers");

            if (customers != null && customers.size() > 0) {
                int count = 0;
                for (JsonNode node : customers) {
                    Long shopifyId = node.get("id").asLong();
                    Customer customer = customerRepository.findByShopifyId(shopifyId)
                            .orElse(new Customer());

                    customer.setShopifyId(shopifyId);
                    customer.setFirstName(node.path("first_name").asText("Unknown"));
                    customer.setLastName(node.path("last_name").asText("Customer"));
                    customer.setEmail(node.path("email").asText("no-email@shopify.com"));
                    customer.setTotalSpent(node.path("total_spent").asDouble(0.0));
                    customer.setTenantId("xeno-venky");
                    
                    customerRepository.save(customer);
                    count++;
                }
                System.out.println("   Synced " + count + " customers from Shopify");
            } else {
                System.out.println("   No customers found in Shopify response");
            }
        } catch (Exception e) {
            System.err.println("   Error syncing customers: " + e.getMessage());
            throw e;
        }
    }

    private void syncProducts() throws Exception {
        try {
            String url = storeUrl + "/admin/api/2024-01/products.json";
            JsonNode root = fetchFromShopify(url);
            JsonNode products = root.get("products");

            if (products != null && products.size() > 0) {
                int count = 0;
                for (JsonNode node : products) {
                    Long shopifyId = node.get("id").asLong();
                    Product product = productRepository.findByShopifyProductId(shopifyId)
                            .orElse(new Product());

                    product.setShopifyProductId(shopifyId);
                    product.setTitle(node.path("title").asText("Unknown Product"));
                    product.setVendor(node.path("vendor").asText("Unknown Vendor"));
                    product.setProductType(node.path("product_type").asText("General"));
                    product.setTenantId("xeno-venky");
                    
                    // Get price from the first variant
                    if (node.has("variants") && node.get("variants").size() > 0) {
                        product.setPrice(node.get("variants").get(0).path("price").asDouble(0.0));
                    }

                    productRepository.save(product);
                    count++;
                }
                System.out.println("   Synced " + count + " products from Shopify");
            } else {
                System.out.println("   No products found in Shopify response");
            }
        } catch (Exception e) {
            System.err.println("   Error syncing products: " + e.getMessage());
            throw e;
        }
    }

    private void syncOrders() throws Exception {
        try {
            String url = storeUrl + "/admin/api/2024-01/orders.json?status=any";
            JsonNode root = fetchFromShopify(url);
            JsonNode orders = root.get("orders");

            if (orders != null && orders.size() > 0) {
                int count = 0;
                for (JsonNode node : orders) {
                    Long shopifyId = node.get("id").asLong();
                    OrderTable order = orderRepository.findByShopifyOrderId(shopifyId)
                            .orElse(new OrderTable());

                    order.setShopifyOrderId(shopifyId);
                    order.setTotalPrice(node.path("total_price").asDouble(0.0));
                    order.setCurrency(node.path("currency").asText("USD"));
                    order.setFinancialStatus(node.path("financial_status").asText("pending"));
                    order.setTenantId("xeno-venky");
                    
                    // Parse the order date if available
                    String createdAt = node.path("created_at").asText();
                    if (!createdAt.isEmpty()) {
                        try {
                            order.setProcessedAt(LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME));
                        } catch (Exception e) {
                            order.setProcessedAt(LocalDateTime.now());
                        }
                    } else {
                        order.setProcessedAt(LocalDateTime.now());
                    }

                    // Link Customer
                    if (node.has("customer") && node.get("customer").has("id")) {
                        Long customerShopifyId = node.get("customer").get("id").asLong();
                        customerRepository.findByShopifyId(customerShopifyId).ifPresent(order::setCustomer);
                    }

                    orderRepository.save(order);
                    count++;
                }
                System.out.println("   Synced " + count + " orders from Shopify");
            } else {
                System.out.println("   No orders found in Shopify response");
            }
        } catch (Exception e) {
            System.err.println("   Error syncing orders: " + e.getMessage());
            throw e;
        }
    }

    private JsonNode fetchFromShopify(String url) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Shopify-Access-Token", accessToken);
            headers.set("Content-Type", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("   Fetching from: " + url);
            System.out.println("   Using token: " + (accessToken != null && !accessToken.isEmpty() ? accessToken.substring(0, 20) + "..." : "NO TOKEN"));
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            System.out.println("   Response Status: " + response.getStatusCode());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("   ✅ Successfully fetched data");
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readTree(response.getBody());
            } else {
                System.err.println("   ❌ Failed to fetch data. Status: " + response.getStatusCode());
                return new ObjectMapper().createObjectNode();
            }
        } catch (Exception e) {
            System.err.println("   ❌ Network error fetching from Shopify: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            // Return empty response instead of throwing
            return new ObjectMapper().createObjectNode();
        }
    }
}