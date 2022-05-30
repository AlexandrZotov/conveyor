package com.example.conveyor;

import com.example.conveyor.dicts.EmploymentStatus;
import com.example.conveyor.dto.*;
import com.example.conveyor.service.ConveyorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = ConveyorService.class
)
public class ConveyorServiceTest {

    @MockBean
    private static ConveyorService conveyorService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final BigDecimal rate = new BigDecimal(12);

    private static LoanApplicationRequestDTO loanApplicationRequestDTO;
    private static List<LoanOfferDTO> listOffer;
    private static LoanOfferDTO firstListOffer;
    private static BigDecimal monthlyPayment;
    private static ScoringDataDTO scoringDataDTO;
    private static CreditDTO creditDTO;

    @BeforeAll
    public static void setUp() throws IOException {
        loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        String jsonLoanApplicationRequest = JsonUtil.readResourseAsString("src/test/resources/json/loanApplicationRequest.json");
        String jsonListLoanOffer = JsonUtil.readResourseAsString("src/test/resources/json/listLoanOffer.json");
        String jsonScoringData = JsonUtil.readResourseAsString("src/test/resources/json/scoringData.json");
        String jsonCreditDTO = JsonUtil.readResourseAsString("src/test/resources/json/creditDTO.json");
        objectMapper.findAndRegisterModules();

        loanApplicationRequestDTO = objectMapper.readValue(jsonLoanApplicationRequest, LoanApplicationRequestDTO.class);
        LoanOfferDTO[] loanOfferDTOS = objectMapper.readValue(jsonListLoanOffer, LoanOfferDTO[].class);
        listOffer = new ArrayList(Arrays.asList(loanOfferDTOS));
        scoringDataDTO = objectMapper.readValue(jsonScoringData, ScoringDataDTO.class);
        creditDTO = objectMapper.readValue(jsonCreditDTO, CreditDTO.class);

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
                ).setScale(2);
    }

    @Test
    public void getOffersTest() {
        given(conveyorService.getOffers(eq(loanApplicationRequestDTO))).willReturn(listOffer);
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

    @Test
    public void getCreditConditionsTest() {
        assertThat(scoringDataDTO).isNotNull();
        assertThat(creditDTO).isNotNull();
        given(conveyorService.getCreditConditions(eq(scoringDataDTO))).willReturn(creditDTO);
    }

    @Test
    public void creditDenialValidationTest() {
        assertNotEquals(scoringDataDTO.getEmployment().getEmploymentStatus(), EmploymentStatus.UNEMPLOYED);
        assertFalse((Calendar.getInstance().get(Calendar.YEAR) - scoringDataDTO.getBirthdate().getYear()) < 20);
        assertFalse(scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3);
    }

    @Test
    public void getInterestRateTest() {
        assertEquals(rate.subtract(BigDecimal.valueOf(6)), BigDecimal.valueOf(6));
    }

    @Test
    public void getPaymentScheduleTest() {
        assertThat(creditDTO.getPaymentSchedule()).isNotEmpty();
        when(conveyorService.getCreditConditions(scoringDataDTO)).thenReturn(creditDTO);
        List<PaymentScheduleElement> paymentScheduleElementList = conveyorService.getCreditConditions(scoringDataDTO).getPaymentSchedule();
        assertThat(paymentScheduleElementList).isNotEmpty();
        assertEquals(paymentScheduleElementList.size(), scoringDataDTO.getTerm());
    }
}
