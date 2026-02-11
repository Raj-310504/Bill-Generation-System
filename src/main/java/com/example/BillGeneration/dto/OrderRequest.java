package com.example.BillGeneration.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotBlank
    private String customerName;
    @NotBlank
    private String mobileNo;
    @NotNull
    private Long productId;
    @Min(1)
    private Long quantity;
}
