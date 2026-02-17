package com.example.BillGeneration.service;

import com.example.BillGeneration.config.properties.AdminProperties;
import com.example.BillGeneration.config.properties.StockReportProperties;
import com.example.BillGeneration.entity.Product;
import com.example.BillGeneration.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class StockReportService {

    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final AdminProperties adminProperties;
    private final StockReportProperties stockReportProperties;

    public StockReportService(
            ProductRepository productRepository,
            EmailService emailService,
            AdminProperties adminProperties,
            StockReportProperties stockReportProperties
    ) {
        this.productRepository = productRepository;
        this.emailService = emailService;
        this.adminProperties = adminProperties;
        this.stockReportProperties = stockReportProperties;
    }

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
        byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        saveCsvToLocal(fileName, csvBytes);

        emailService.sendMailWithAttachment(
                adminProperties.getEmail(),
                "Daily Stock Report",
                "Please find the daily stock report attached.",
                fileName,
                csvBytes
        );
    }

    private void saveCsvToLocal(String fileName, byte[] fileBytes) {
        try {
            Path outputDir = Path.of(stockReportProperties.getOutputDir());
            Files.createDirectories(outputDir);
            Files.write(outputDir.resolve(fileName), fileBytes);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save stock report CSV locally", ex);
        }
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
