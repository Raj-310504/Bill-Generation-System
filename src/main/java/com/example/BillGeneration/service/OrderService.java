package com.example.BillGeneration.service;

import com.example.BillGeneration.dto.OrderRequest;
import com.example.BillGeneration.dto.OrderResponse;
import com.example.BillGeneration.entity.Bill;
import com.example.BillGeneration.entity.BillItem;
import com.example.BillGeneration.entity.OrderDetails;
import com.example.BillGeneration.entity.Product;
import com.example.BillGeneration.repository.BillRepository;
import com.example.BillGeneration.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BillRepository billRepository;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {

        Product product = productService.getProduct(request.getProductId());

        double total = product.getPrice() * request.getQuantity();
        double gst = total * 0.18;
        double finalAmount = total + gst;

        // total orders count
        long orderCount = orderRepository.count() + 1;

        String paymentStatus;

        if (orderCount % 5 == 0) {
            paymentStatus = "FAILED";
        } else {
            paymentStatus = "SUCCESS";
            productService.updateStock(product, (long) request.getQuantity());

            Product updatedProduct =
                    productService.getProduct(product.getId());

            // Threshold check
            if (updatedProduct.getQuantity() <= updatedProduct.getThreshold()) {
                System.out.println("LOW STOCK ALERT FOR " + updatedProduct.getName());

                notificationService.sendEmail(
                        "rajkhunt2004@gmail.com",
                        "Low Stock Alert",
                        "Product: " + updatedProduct.getName() +
                                "\nRemaining Stock: " + updatedProduct.getQuantity() +
                                "\nThreshold: " + updatedProduct.getThreshold()
                );
            }
        }

        OrderDetails order = new OrderDetails();
        order.setProduct(product);
        order.setCustomerName(request.getCustomerName());
        order.setMobileNo(request.getMobileNo());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(total);
        order.setGst(gst);
        order.setFinalAmount(finalAmount);
        order.setPaymentStatus(paymentStatus);

        orderRepository.save(order);

        if ("SUCCESS".equals(paymentStatus)) {
            Bill bill = new Bill();
            bill.setBillNo("BILL-" + System.currentTimeMillis());
            bill.setBillDate(LocalDate.now());
            bill.setCustomerName(order.getCustomerName());
            bill.setFinalAmount(order.getFinalAmount());

            BillItem item = new BillItem();
            item.setBill(bill);
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setQuantity(order.getQuantity());
            item.setPriceAtTime(product.getPrice());
            bill.getItems().add(item);

            Bill savedBill = billRepository.save(bill);
            order.setBill(savedBill);
            orderRepository.save(order);

            String sms =
                    "Hi " + order.getCustomerName() +
                            ", payment successful. Amount Rs " +
                            order.getFinalAmount() +
                            ". Thanks for shopping with us!";

            notificationService.sendSms(order.getMobileNo(), sms);
            notificationService.sendWhatsApp(order.getMobileNo(), sms);
        }

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getFinalAmount(),
                order.getPaymentStatus(),
                paymentStatus.equals("SUCCESS")
                        ? "Order placed successfully"
                        : "Payment failed. Please try again !!"
        );
    }
}

