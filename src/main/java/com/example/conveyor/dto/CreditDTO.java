package com.example.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Кредит")
public class CreditDTO {

    @Schema(description = "Сумма")
    private BigDecimal amount;

    @Schema(description = "Срок")
    private Integer term;

    @Schema(description = "Ежемесячный платеж")
    private BigDecimal monthlyPayment;

    @Schema(description = "Процентная ставка")
    private BigDecimal rate;

    @Schema(description = "Полная стоимость кредита")
    private BigDecimal psk;

    @Schema(description = "Включена ли страховка")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Зарплатный клиент")
    private Boolean isSalaryClient;

    @Schema(description = "График платежей")
    private List<PaymentScheduleElement> paymentSchedule;
}
