package com.example.conveyor.dto;

import com.example.conveyor.dicts.Gender;
import com.example.conveyor.dicts.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные для скоринга")
public class ScoringDataDTO {

    @Schema(description = "Сумма")
    private BigDecimal amount;

    @Schema(description = "Срок")
    private Integer term;

    @Schema(description = "Имя")
    private String firstName;

    @Schema(description = "Фамилия")
    private String lastName;

    @Schema(description = "Отчество")
    private String middleName;

    @Schema(description = "Пол")
    private Gender gender;

    @Schema(description = "Дата рождения")
    private LocalDate birthdate;

    @Schema(description = "Серия паспорта")
    private String passportSeries;

    @Schema(description = "Номер паспорта")
    private String passportNumber;

    @Schema(description = "Дата выдачи паспорта")
    private LocalDate passportIssueDate;

    @Schema(description = "Отделение выдачи паспорта")
    private String passportIssueBranch;

    @Schema(description = "Семейное положение")
    private MaritalStatus maritalStatus;

    @Schema(description = "Количество иждивенцев")
    private Integer dependentAmount;

    @Schema(description = "Работник")
    private EmploymentDTO employment;

    @Schema(description = "Счет клиента")
    private String account;

    @Schema(description = "Включена ли страховка")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Зарплатный клиент")
    private Boolean isSalaryClient;
}
