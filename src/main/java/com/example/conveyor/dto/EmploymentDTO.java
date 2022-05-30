package com.example.conveyor.dto;

import com.example.conveyor.dicts.EmploymentStatus;
import com.example.conveyor.dicts.Position;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Работник")
public class EmploymentDTO {

    @Schema(description = "Рабочий статус")
    private EmploymentStatus employmentStatus;

    @Schema(description = "ИНН работника")
    private String employerINN;

    @Schema(description = "Зарплата")
    private BigDecimal salary;

    @Schema(description = "Должность")
    private Position position;

    @Schema(description = "Общий опыт работы")
    private Integer workExperienceTotal;

    @Schema(description = "Опыт работы на текущем месте")
    private Integer workExperienceCurrent;
}
