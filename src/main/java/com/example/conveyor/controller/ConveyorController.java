package com.example.conveyor.controller;

import com.example.conveyor.dto.CreditDTO;
import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import com.example.conveyor.dto.ScoringDataDTO;
import com.example.conveyor.service.ConveyorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/conveyor")
@Validated
@Tag(name = "Кредитный конвейер", description = "расчёт условий кредита")
public class ConveyorController {

    private final ConveyorService conveyorService;

    @Operation(
            summary = "Расчёт возможных условий кредита",
            description = "Получение 4 кредитных предложений"
    )
    @PostMapping("/offers")
    public List<LoanOfferDTO> getOffers(
            @RequestBody @Valid @Parameter(description = "входящие параметры для рассчета кредита")
                    LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return conveyorService.getOffers(loanApplicationRequestDTO);
    }

    @Operation(
            summary = "Полный расчет параметров кредита",
            description = "Валидация присланных данных + скоринг данных + полный расчет параметров кредита"
    )
    @PostMapping("/calculation")
    public CreditDTO getCreditConditions(
            @RequestBody @Parameter(description = "данные для скоринга") ScoringDataDTO scoringDataDTO) {
        return conveyorService.getCreditConditions(scoringDataDTO);
    }
}
