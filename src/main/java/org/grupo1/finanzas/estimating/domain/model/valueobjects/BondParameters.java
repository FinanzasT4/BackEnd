package org.grupo1.finanzas.estimating.domain.model.valueobjects;

import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondValuationCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.UpdateBondValuationCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record BondParameters(
        Money faceValue,
        Money marketPrice,
        LocalDate issueDate,
        LocalDate maturityDate,
        int totalPeriods,
        InterestRate couponRate,
        Frequency frequency,
        GracePeriod gracePeriod,
        InterestRate marketRate,
        BigDecimal issuerStructuringCost,
        BigDecimal issuerPlacementCost,
        BigDecimal issuerCavaliCost,
        BigDecimal investorSabCost,
        BigDecimal investorCavaliCost
) {

    public BondParameters(CreateBondValuationCommand command) {
        this(
                new Money(command.faceValue()),
                new Money(command.marketPrice()),
                command.issueDate(),
                command.maturityDate(),
                command.totalPeriods(),
                new InterestRate(command.rateValue(), command.rateType(), Optional.ofNullable(command.capitalization())),
                command.frequency(),
                new GracePeriod(command.graceType(), command.graceCapital(), command.graceInterest()),
                new InterestRate(command.marketRate(), RateType.EFFECTIVE, Optional.empty()),
                command.issuerStructuringCost(),
                command.issuerPlacementCost(),
                command.issuerCavaliCost(),
                command.investorSabCost(),
                command.investorCavaliCost()
        );
    }


    public BondParameters(UpdateBondValuationCommand command) {
        this(
                new Money(command.faceValue()),
                new Money(command.marketPrice()),
                command.issueDate(),
                command.maturityDate(),
                command.totalPeriods(),
                new InterestRate(command.rateValue(), command.rateType(), Optional.ofNullable(command.capitalization())),
                command.frequency(),
                new GracePeriod(command.graceType(), command.graceCapital(), command.graceInterest()),
                new InterestRate(command.marketRate(), RateType.EFFECTIVE, Optional.empty()),
                command.issuerStructuringCost(),
                command.issuerPlacementCost(),
                command.issuerCavaliCost(),
                command.investorSabCost(),
                command.investorCavaliCost()
        );
    }
}