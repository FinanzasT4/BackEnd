package org.grupo1.finanzas.estimating.domain.model.aggregates;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondCommand;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.*;
import org.grupo1.finanzas.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bond extends AuditableAbstractAggregateRoot<Bond> {

    @NotBlank
    @Column(unique = true)
    private String bondName;

    @NotNull private BigDecimal faceValue;
    @NotNull private BigDecimal issuePrice;
    @NotNull private BigDecimal purchasePrice;
    @NotNull private LocalDate issueDate;
    @NotNull private LocalDate maturityDate;
    @Min(1) private int totalPeriods;

    @NotNull private RateType rateType;
    @NotNull private BigDecimal rateValue;
    private Capitalization capitalization;
    @NotNull private Frequency frequency;

    @NotNull private GraceType graceType;
    @Min(0) private int graceCapital;
    @Min(0) private int graceInterest;

    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal commission;

    @NotNull private BigDecimal marketRate;

    public Bond(CreateBondCommand command) {
        this.bondName = command.bondName();
        this.faceValue = command.faceValue();
        this.issuePrice = command.issuePrice();
        this.purchasePrice = command.purchasePrice();
        this.issueDate = command.issueDate();
        this.maturityDate = command.maturityDate();
        this.totalPeriods = command.totalPeriods();
        this.rateType = command.rateType();
        this.rateValue = command.rateValue();
        this.capitalization = command.capitalization();
        this.frequency = command.frequency();
        this.graceType = command.graceType();
        this.graceCapital = command.graceCapital();
        this.graceInterest = command.graceInterest();
        this.commission = command.commission();
        this.marketRate = command.marketRate();
    }
}
