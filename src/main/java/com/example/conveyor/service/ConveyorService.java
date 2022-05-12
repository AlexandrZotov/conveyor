package com.example.conveyor.service;

import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConveyorService {

    private final BigDecimal rate = new BigDecimal(13.5);

    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("loanApplicationRequestDTO " + loanApplicationRequestDTO);
        List<LoanOfferDTO> listOffer = new ArrayList<>();

        listOffer.add(getLoanOffer(1L, loanApplicationRequestDTO, false, false));
        listOffer.add(getLoanOffer(2L, loanApplicationRequestDTO, false, true));
        listOffer.add(getLoanOffer(3L, loanApplicationRequestDTO, true, false));
        listOffer.add(getLoanOffer(4L, loanApplicationRequestDTO, true, true));

        return listOffer;
    }

    private LoanOfferDTO getLoanOffer(Long id,
                                      LoanApplicationRequestDTO loanApplicationRequestDTO,
                                      Boolean isInsuranceEnabled,
                                      Boolean isSalaryClient) {

        BigDecimal currentRate = rate;

        currentRate = isInsuranceEnabled ? currentRate.subtract(BigDecimal.valueOf(2)) : currentRate.add(BigDecimal.valueOf(2));
        currentRate = isSalaryClient ? currentRate.subtract(BigDecimal.valueOf(1)) : currentRate.add(BigDecimal.valueOf(1));

        BigDecimal monthlyInterestRate = getMonthlyInterestRate(currentRate);

        BigDecimal monthlyPayment = getMonthlyPayment(loanApplicationRequestDTO, monthlyInterestRate).setScale(2);

        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm()));

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(id);
        loanOfferDTO.setRequestedAmount(loanApplicationRequestDTO.getAmount());
        loanOfferDTO.setTotalAmount(totalAmount);
        loanOfferDTO.setTerm(loanApplicationRequestDTO.getTerm());
        loanOfferDTO.setMonthlyPayment(monthlyPayment);
        loanOfferDTO.setRate(currentRate);
        loanOfferDTO.setIsInsuranceEnabled(isInsuranceEnabled);
        loanOfferDTO.setIsSalaryClient(isSalaryClient);

        return loanOfferDTO;
    }

    private BigDecimal getMonthlyInterestRate(BigDecimal currentRate) {

        return currentRate.divide(BigDecimal.valueOf(100), 8, RoundingMode.CEILING)
                .divide(BigDecimal.valueOf(12), 8, RoundingMode.CEILING);
    }

    private BigDecimal getMonthlyPayment(LoanApplicationRequestDTO loanApplicationRequestDTO, BigDecimal monthlyInterestRate) {

        return loanApplicationRequestDTO.getAmount()
                .multiply(
                        monthlyInterestRate.add(
                                monthlyInterestRate.divide(
                                        monthlyInterestRate
                                                .add(BigDecimal.valueOf(1))
                                                .pow(loanApplicationRequestDTO.getTerm())
                                                .subtract(BigDecimal.valueOf(1)), 8, RoundingMode.CEILING
                                )
                        )
                );
    }

}
