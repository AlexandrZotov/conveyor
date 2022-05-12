package com.example.conveyor.controller;

import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import com.example.conveyor.service.ConveyorService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/conveyor")
public class ConveyorController {

    private final ConveyorService conveyorService;

    @PostMapping("/offers")
    public List<LoanOfferDTO> getOffers(@RequestBody @Valid LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return conveyorService.getOffers(loanApplicationRequestDTO);
    }
}
