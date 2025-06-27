package org.grupo1.finanzas.estimating.domain.model.valueobjects;

import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondValuationCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.UpdateBondValuationCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record BondParameters(
        Money faceValue, Money issuePrice, Money purchasePrice,
        LocalDate issueDate, LocalDate maturityDate,
        int totalPeriods, InterestRate couponRate, Frequency frequency,
        GracePeriod gracePeriod, BigDecimal commission, InterestRate marketRate
) {

    // Constructor para el comando de creación
    public BondParameters(CreateBondValuationCommand command) {
        this(
                new Money(command.faceValue()),
                new Money(command.issuePrice()),
                new Money(command.purchasePrice()),
                command.issueDate(),
                command.maturityDate(),
                command.totalPeriods(),
                new InterestRate(command.rateValue(), command.rateType(), Optional.ofNullable(command.capitalization())),
                command.frequency(),
                new GracePeriod(command.graceType(), command.graceCapital(), command.graceInterest()),
                command.commission(),
                new InterestRate(command.marketRate(), RateType.EFFECTIVE, Optional.empty())
        );
    }

    // Constructor para el comando de actualización
    public BondParameters(UpdateBondValuationCommand command) {
        this(
                new Money(command.faceValue()),
                new Money(command.issuePrice()),
                new Money(command.purchasePrice()),
                command.issueDate(),
                command.maturityDate(),
                command.totalPeriods(),
                new InterestRate(command.rateValue(), command.rateType(), Optional.ofNullable(command.capitalization())),
                command.frequency(),
                new GracePeriod(command.graceType(), command.graceCapital(), command.graceInterest()),
                command.commission(),
                new InterestRate(command.marketRate(), RateType.EFFECTIVE, Optional.empty())
        );
    }
}