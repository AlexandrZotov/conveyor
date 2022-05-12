package com.example.conveyor.validation;

import java.time.LocalDate;
import java.util.Calendar;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BirthDateValidator implements ConstraintValidator<BirthDate, LocalDate> {
    @Override
    public boolean isValid(final LocalDate valueToValidate, final ConstraintValidatorContext context) {

        return Calendar.getInstance().get(Calendar.YEAR) - valueToValidate.getYear() >= 18;
    }
}
