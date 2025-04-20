package org.grupo1.finanzas.estimating.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.*;
import org.grupo1.finanzas.estimating.domain.model.commands.CreateResultCommand;
import org.grupo1.finanzas.estimating.domain.model.entities.Period;
import org.grupo1.finanzas.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Result extends AuditableAbstractAggregateRoot<Result> {

    @Column(nullable = false)
    private Long bondId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "result_id")
    private List<Period> periods;

    private BigDecimal tcea;
    private BigDecimal trea;
    private BigDecimal duration;
    private BigDecimal durationMod;
    private BigDecimal convexity;
    private BigDecimal maxMarketPrice;

    public Result(CreateResultCommand command) {
        this.bondId = command.bondId();
    }

    public void initializeWith(List<Period> periods,
                               BigDecimal tcea,
                               BigDecimal trea,
                               BigDecimal duration,
                               BigDecimal durationMod,
                               BigDecimal convexity,
                               BigDecimal maxMarketPrice) {
        this.periods = periods;
        this.tcea = tcea;
        this.trea = trea;
        this.duration = duration;
        this.durationMod = durationMod;
        this.convexity = convexity;
        this.maxMarketPrice = maxMarketPrice;
    }
}