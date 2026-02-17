package com.example.BillGeneration.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "stock.report")
public class StockReportProperties {
    private String outputDir = "stock-reports";
    private String cron = "0 0 9 * * *";
}
