package com.example.conveyor;

import com.example.conveyor.dto.LoanApplicationRequestDTO;
import com.example.conveyor.dto.LoanOfferDTO;
import com.example.conveyor.service.ConveyorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ConveyorServiceTest {

    private static ConveyorService conveyorService = new ConveyorService();

    @TestConfiguration
    private static class MyConfig {
        @Bean
        ConveyorService conveyorService() {return new ConveyorService();}
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final BigDecimal rate = new BigDecimal(13.5);

    private static LoanApplicationRequestDTO loanApplicationRequestDTO;
    private static List<LoanOfferDTO> listOffer;
    private static LoanOfferDTO firstListOffer;
    private static BigDecimal monthlyPayment;

    @BeforeAll
    public static void setUp() throws IOException {
        loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        String jsonLoanApplicationRequest = JsonUtil.readResourseAsString("src/test/resources/loanApplicationRequest.json");
        objectMapper.findAndRegisterModules();
        loanApplicationRequestDTO = objectMapper.readValue(jsonLoanApplicationRequest, LoanApplicationRequestDTO.class);
        listOffer = conveyorService.getOffers(loanApplicationRequestDTO);
        firstListOffer = listOffer.get(0);
        BigDecimal currentRate = rate.add(BigDecimal.valueOf(3));
        BigDecimal monthlyInterestRate = currentRate.divide(BigDecimal.valueOf(100), 8, RoundingMode.CEILING)
                .divide(BigDecimal.valueOf(12), 8, RoundingMode.CEILING);
        monthlyPayment = loanApplicationRequestDTO.getAmount()
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

    @Test
    public void getOffersTest() {
        assertThat(listOffer).isNotEmpty();
        assertThat(listOffer.size()).isEqualTo(4);
    }

    @Test
    public void getLoanOfferTest() {
        assertNotNull(firstListOffer);
        assertEquals(firstListOffer.getRate(), rate.add(BigDecimal.valueOf(3)));
        assertEquals(firstListOffer.getTerm(), 36);
    }

    @Test
    public void getMonthlyPaymentTest() {
        assertThat(monthlyPayment).isNotNull();
        assertEquals(firstListOffer.getMonthlyPayment(), monthlyPayment);
    }

    @Test
    public void getTotalAmountTest() {
        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm()));
        assertThat(totalAmount).isNotNull();
        assertEquals(firstListOffer.getTotalAmount(), totalAmount);
    }
}
