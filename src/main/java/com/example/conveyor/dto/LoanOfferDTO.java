package com.example.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Кредитное предложение")
public class LoanOfferDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long applicationId;

    @Schema(description = "Запрошенная сумма")
    private BigDecimal requestedAmount;

    @Schema(description = "Общая сумма")
    private BigDecimal totalAmount;

    @Schema(description = "Срок")
    private Integer term;

    @Schema(description = "Ежемесячный платеж")
    private BigDecimal monthlyPayment;

    @Schema(description = "Процентная ставка")
    private BigDecimal rate;

    @Schema(description = "Включена ли страховка")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Зарплатный клиент")
    private Boolean isSalaryClient;
}
