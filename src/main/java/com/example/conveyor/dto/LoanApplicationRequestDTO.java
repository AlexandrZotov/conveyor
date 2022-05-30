package com.example.conveyor.dto;

import com.example.conveyor.validation.BirthDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Входящие параметры для рассчета кредита")
public class LoanApplicationRequestDTO {

    @NotNull
    @DecimalMin(value = "10000")
    @Schema(description = "Сумма")
    private BigDecimal amount;

    @NotNull
    @Min(6)
    @Schema(description = "Срок")
    private Integer term;

    @NotEmpty
    @Pattern(regexp = "[\\w\\.]{2,30}")
    @Schema(description = "Имя")
    private String firstName;

    @NotEmpty
    @Pattern(regexp = "[\\w\\.]{2,30}")
    @Schema(description = "Фамилия")
    private String lastName;

    @Pattern(regexp = "^$|[\\w\\.]{2,30}")
    @Schema(description = "Отчество")
    private String middleName;

    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}")
    @Schema(description = "Email адрес")
    private String email;

    @BirthDate(message = "Дата рождения должна быть больше или равна 18")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата рождения")
    private LocalDate birthdate;

    @NotNull
    @Size(min = 4, max=4)
    @Schema(description = "Серия паспорта")
    private String passportSeries;

    @NotNull
    @Size(min = 6, max=6)
    @Schema(description = "Номер паспорта")
    private String passportNumber;

}
