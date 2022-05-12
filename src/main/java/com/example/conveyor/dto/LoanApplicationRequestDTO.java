package com.example.conveyor.dto;

import com.example.conveyor.validation.BirthDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequestDTO {

    @NotNull
    @DecimalMin(value = "10000")
    private BigDecimal amount;

    @NotNull
    @Min(6)
    private Integer term;

    @NotEmpty
    @Pattern(regexp = "[\\w\\.]{2,30}")
    private String firstName;

    @NotEmpty
    @Pattern(regexp = "[\\w\\.]{2,30}")
    private String lastName;

    @Pattern(regexp = "^$|[\\w\\.]{2,30}")
    private String middleName;

    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}")
    private String email;

    @BirthDate(message = "Дата рождения должна быть больше или равна 18")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @NotNull
    @Size(min = 4, max=4)
    private String passportSeries;

    @NotNull
    @Size(min = 6, max=6)
    private String passportNumber;
}
