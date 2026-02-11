package com.example.BillGeneration.service;

import com.example.BillGeneration.entity.Product;
import com.example.BillGeneration.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class StockReportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Value("${admin.email}")
    private String adminEmail;

    @Scheduled(cron = "${stock.report.cron:0 0 9 * * *}")
    public void sendDailyStockReport() {
        List<Product> products = productRepository.findAll();

        StringBuilder csv = new StringBuilder();
        csv.append("product_name,remaining_stock,threshold\n");

        for (Product product : products) {
            csv.append(safeCsv(product.getName())).append(",");
            csv.append(valueOrEmpty(product.getQuantity())).append(",");
            csv.append(valueOrEmpty(product.getThreshold())).append("\n");
        }

        String fileName = "stock-report-" + LocalDate.now() + ".csv";
        emailService.sendMailWithAttachment(
                adminEmail,
                "Daily Stock Report",
                "Please find the daily stock report attached.",
                fileName,
                csv.toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    private String valueOrEmpty(Long value) {
        return value == null ? "" : value.toString();
    }

    private String safeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
