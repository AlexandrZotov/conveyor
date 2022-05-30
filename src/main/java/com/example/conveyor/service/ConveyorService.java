package com.example.conveyor.service;

import com.example.conveyor.dicts.EmploymentStatus;
import com.example.conveyor.dicts.Gender;
import com.example.conveyor.dicts.MaritalStatus;
import com.example.conveyor.dicts.Position;
import com.example.conveyor.dto.*;
import com.example.conveyor.exception.CreditValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class ConveyorService {

    private final BigDecimal rate;

    public ConveyorService(@Value("${rate}") BigDecimal propertyRate) {
        this.rate = propertyRate;
    }

    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("getOffers :: Loan application request DTO={}", loanApplicationRequestDTO);
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

    public CreditDTO getCreditConditions(ScoringDataDTO scoringDataDTO) throws CreditValidationException {
        CreditDTO creditDTO = new CreditDTO();

        String messageValid = creditDenialValidation(scoringDataDTO);

        if (messageValid != "") {
            log.info("Valid message: {}", messageValid);
            throw new CreditValidationException(messageValid);
        }

        BigDecimal interestRate = getInterestRate(scoringDataDTO);
        log.info("getCreditConditions :: Interest rate={}", interestRate);

        BigDecimal monthlyInterestRate = getMonthlyInterestRate(interestRate);
        log.info("getCreditConditions :: Monthly interest rate={}", monthlyInterestRate);

        BigDecimal monthlyPayment = getMonthlyPayment(
                scoringDataDTO.getAmount(), scoringDataDTO.getTerm(), monthlyInterestRate).setScale(2);
        log.info("getCreditConditions :: Monthly payment={}", monthlyPayment);

        BigDecimal psk = monthlyPayment.multiply(BigDecimal.valueOf(scoringDataDTO.getTerm()));
        log.info("getCreditConditions :: PSK={}", psk);

        List<PaymentScheduleElement> listPaymentScheduleElement = getPaymentSchedule(scoringDataDTO.getAmount(),
                scoringDataDTO.getTerm(), monthlyPayment, interestRate);

        creditDTO.setAmount(scoringDataDTO.getAmount());
        creditDTO.setTerm(scoringDataDTO.getTerm());
        creditDTO.setMonthlyPayment(monthlyPayment);
        creditDTO.setRate(interestRate);
        creditDTO.setPsk(psk);
        creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
        creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());
        creditDTO.setPaymentSchedule(listPaymentScheduleElement);
        log.info("getCreditConditions :: creditDTO={}", creditDTO);

        return creditDTO;
    }

    private LoanOfferDTO getLoanOffer(Long id,
                                      LoanApplicationRequestDTO loanApplicationRequestDTO,
                                      Boolean isInsuranceEnabled,
                                      Boolean isSalaryClient) {

        BigDecimal currentRate = rate;

        currentRate = isInsuranceEnabled ? currentRate.subtract(BigDecimal.valueOf(2)) : currentRate.add(BigDecimal.valueOf(2));
        currentRate = isSalaryClient ? currentRate.subtract(BigDecimal.ONE) : currentRate.add(BigDecimal.ONE);

        log.info("getLoanOffer :: Current rate={}", currentRate);

        BigDecimal monthlyInterestRate = getMonthlyInterestRate(currentRate);
        log.info("getLoanOffer :: Monthly interest rate={}", monthlyInterestRate);

        BigDecimal monthlyPayment = getMonthlyPayment(
                loanApplicationRequestDTO.getAmount(), loanApplicationRequestDTO.getTerm(), monthlyInterestRate).setScale(2);
        log.info("getLoanOffer :: Monthly payment={}", monthlyPayment);

        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm()));
        log.info("getLoanOffer :: Total amount={}", totalAmount);

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

    private BigDecimal getMonthlyPayment(BigDecimal amount, Integer term, BigDecimal monthlyInterestRate) {

        return amount
                .multiply(
                        monthlyInterestRate.add(
                                monthlyInterestRate.divide(
                                        monthlyInterestRate
                                                .add(BigDecimal.ONE)
                                                .pow(term)
                                                .subtract(BigDecimal.ONE), 8, RoundingMode.CEILING
                                )
                        )
                );
    }

    private BigDecimal getInterestRate(ScoringDataDTO scoringDataDTO) {
        BigDecimal currentRate = rate;
        Enum<EmploymentStatus> employmentStatus = scoringDataDTO.getEmployment().getEmploymentStatus();
        Enum<Position> positionEnum = scoringDataDTO.getEmployment().getPosition();
        Enum<MaritalStatus> maritalStatus = scoringDataDTO.getMaritalStatus();
        Enum<Gender> genderEnum = scoringDataDTO.getGender();
        int age = Calendar.getInstance().get(Calendar.YEAR) - scoringDataDTO.getBirthdate().getYear();

        if (employmentStatus.equals(EmploymentStatus.SELF_EMPLOYED)) {
            currentRate = currentRate.add(BigDecimal.ONE);
        } else if (employmentStatus.equals(EmploymentStatus.BUSINESS_OWNER)) {
            currentRate = currentRate.add(BigDecimal.valueOf(3));
        }

        if (positionEnum.equals(Position.MID_MANAGER)) {
            currentRate = currentRate.subtract(BigDecimal.valueOf(2));
        } else if (positionEnum.equals(Position.TOP_MANAGER)) {
            currentRate = currentRate.subtract(BigDecimal.valueOf(4));
        }

        if (maritalStatus.equals(MaritalStatus.MARRIED)) {
            currentRate = currentRate.subtract(BigDecimal.valueOf(3));
        } else if (maritalStatus.equals(MaritalStatus.DIVORCED)) {
            currentRate = currentRate.add(BigDecimal.ONE);
        }

        if ((genderEnum.equals(Gender.FEMALE) && (age >= 35 && age <= 60)) ||
                (genderEnum.equals(Gender.MALE) && (age >= 30 && age <= 55))) {
            currentRate = currentRate.subtract(BigDecimal.valueOf(3));
        } else if (genderEnum.equals(Gender.NON_BINARY)) {
            currentRate = currentRate.add(BigDecimal.valueOf(3));
        }

        if (scoringDataDTO.getDependentAmount() > 1)
            currentRate = currentRate.add(BigDecimal.ONE);

        return currentRate;
    }

    private String creditDenialValidation(ScoringDataDTO scoringDataDTO) {
        String employmentStatusValid = "", salaryValid = "", ageLessThanTwenty = "", ageOverSixtyYears = "",
                totalExperienceLessThanTwelveMonths = "", currentExperienceLessThanThreeMonths = "";
        int age = Calendar.getInstance().get(Calendar.YEAR) - scoringDataDTO.getBirthdate().getYear();
        BigDecimal salary = scoringDataDTO.getEmployment().getSalary();

        if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED))
            employmentStatusValid = "статус - безработный; ";
        if (scoringDataDTO.getAmount().compareTo(salary.multiply(BigDecimal.valueOf(20))) >= 0)
            salaryValid = "сумма займа больше, чем 20 зарплат; ";
        if (age < 20)
            ageLessThanTwenty = "возраст менее 20 лет; ";
        if (age > 60)
            ageOverSixtyYears = "возраст более 60 лет; ";
        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12)
            totalExperienceLessThanTwelveMonths = "общий стаж менее 12 месяцев; ";
        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3)
            currentExperienceLessThanThreeMonths = "текущий стаж менее 3 месяцев; ";

        String messageValid = new StringBuilder()
                .append(employmentStatusValid)
                .append(salaryValid)
                .append(ageLessThanTwenty)
                .append(ageOverSixtyYears)
                .append(totalExperienceLessThanTwelveMonths)
                .append(currentExperienceLessThanThreeMonths)
                .toString();

        return messageValid;
    }

    private List<PaymentScheduleElement> getPaymentSchedule(BigDecimal amount, Integer term, BigDecimal monthlyPayment,
                                                            BigDecimal interestRate) {
        BigDecimal interestPayment;
        BigDecimal debtPayment;
        List<PaymentScheduleElement> paymentScheduleElementList = new ArrayList<>();
        BigDecimal remainingDebt = amount;
        BigDecimal annualInterestRate = interestRate.divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING);

        for (int i = 0; i < term; i++) {
            interestPayment = remainingDebt.multiply(annualInterestRate).divide(BigDecimal.valueOf(12), 2, RoundingMode.CEILING);
            debtPayment = monthlyPayment.subtract(interestPayment);
            remainingDebt = remainingDebt.subtract(debtPayment);
            PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement();
            paymentScheduleElement.setNumber(i + 1);
            paymentScheduleElement.setDate(LocalDate.now().plusMonths(i));
            paymentScheduleElement.setTotalPayment(monthlyPayment);
            paymentScheduleElement.setInterestPayment(interestPayment);
            paymentScheduleElement.setDebtPayment(debtPayment);
            paymentScheduleElement.setRemainingDebt(remainingDebt);
            paymentScheduleElementList.add(paymentScheduleElement);
        }

        return paymentScheduleElementList;
    }

}
