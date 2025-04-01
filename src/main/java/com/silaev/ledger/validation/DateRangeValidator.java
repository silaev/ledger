package com.silaev.ledger.validation;

import com.silaev.ledger.request.GetHistoryFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, GetHistoryFilter> {
    @Override
    public boolean isValid(GetHistoryFilter filter, ConstraintValidatorContext context) {
        if (filter == null || filter.from() == null || filter.to() == null) {
            return false;
        }
        return !filter.from().isAfter(filter.to());
    }
}
