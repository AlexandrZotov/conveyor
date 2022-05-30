package com.example.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "График платежей")
public class PaymentScheduleElement {

    @Schema(description = "Номер")
    private Integer number;

    @Schema(description = "Дата платежа")
    private LocalDate date;

    @Schema(description = "Платеж")
    private BigDecimal totalPayment;

    @Schema(description = "Выплата процентов")
    private BigDecimal interestPayment;

    @Schema(description = "Оплата долга")
    private BigDecimal debtPayment;

    @Schema(description = "Оставшийся долг")
    private BigDecimal remainingDebt;
}
