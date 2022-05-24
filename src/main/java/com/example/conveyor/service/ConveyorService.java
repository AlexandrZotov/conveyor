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
        log.info("Loan application request DTO :: {} ", loanApplicationRequestDTO);
        List<LoanOfferDTO> listOffer = new ArrayList<>();
        log.info("---------------------------------------------------------");
        listOffer.add(getLoanOffer(1L, loanApplicationRequestDTO, false, false));
        log.info("---------------------------------------------------------");
        listOffer.add(getLoanOffer(2L, loanApplicationRequestDTO, false, true));
        log.info("---------------------------------------------------------");
        listOffer.add(getLoanOffer(3L, loanApplicationRequestDTO, true, false));
        log.info("---------------------------------------------------------");
        listOffer.add(getLoanOffer(4L, loanApplicationRequestDTO, true, true));

        return listOffer;
    }

    private LoanOfferDTO getLoanOffer(Long id,
                                      LoanApplicationRequestDTO loanApplicationRequestDTO,
                                      Boolean isInsuranceEnabled,
                                      Boolean isSalaryClient) {

        BigDecimal currentRate = rate;

        currentRate = isInsuranceEnabled ? currentRate.subtract(BigDecimal.valueOf(2)) : currentRate.add(BigDecimal.valueOf(2));
        currentRate = isSalaryClient ? currentRate.subtract(BigDecimal.ONE) : currentRate.add(BigDecimal.ONE);

        log.info("Current rate :: {} ", currentRate);

        BigDecimal monthlyInterestRate = getMonthlyInterestRate(currentRate);
        log.info("Monthly interest rate :: {} ", monthlyInterestRate);

        BigDecimal monthlyPayment = getMonthlyPayment(loanApplicationRequestDTO, monthlyInterestRate).setScale(2);
        log.info("Monthly payment :: {} ", monthlyPayment);

        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm()));
        log.info("Total amount :: {} ", totalAmount);

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
                                                .add(BigDecimal.ONE)
                                                .pow(loanApplicationRequestDTO.getTerm())
                                                .subtract(BigDecimal.ONE), 8, RoundingMode.CEILING
                                )
                        )
                );
    }

}
