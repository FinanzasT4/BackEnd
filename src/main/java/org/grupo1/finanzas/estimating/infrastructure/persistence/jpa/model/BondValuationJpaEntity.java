package org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Capitalization;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Frequency;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.GraceType;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.RateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

/**
 * Entidad de persistencia para el Aggregate Root BondValuation.
 * Esta clase está diseñada para ser mapeada a una tabla en la base de datos
 * y está fuertemente acoplada a JPA.
 * NOTA: Esta versión es autocontenida y no hereda de ninguna clase base.
 */
@Entity
@Table(name = "bond_valuations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BondValuationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date updatedAt;

    @Column(nullable = false, length = 50)
    private String valuationName;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 18, scale = 4) private BigDecimal faceValueAmount;
    @Column(nullable = false, length = 3) private Currency faceValueCurrency;

    @Column(nullable = false, precision = 18, scale = 4) private BigDecimal issuePriceAmount;
    @Column(nullable = false, length = 3) private Currency issuePriceCurrency;

    @Column(nullable = false, precision = 18, scale = 4) private BigDecimal purchasePriceAmount;
    @Column(nullable = false, length = 3) private Currency purchasePriceCurrency;

    @Column(nullable = false) private LocalDate issueDate;
    @Column(nullable = false) private LocalDate maturityDate;
    @Column(nullable = false) private int totalPeriods;

    @Column(nullable = false, precision = 18, scale = 10) private BigDecimal couponRateValue;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private RateType couponRateType;
    @Enumerated(EnumType.STRING) @Column(length = 20) private Capitalization couponRateCapitalization;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Frequency frequency;

    @Enumerated(EnumType.STRING) @Column(nullable =false, length = 20) private GraceType graceType;
    @Column(nullable = false) private int graceCapitalPeriods;
    @Column(nullable = false) private int graceInterestPeriods;

    @Column(nullable = false, precision = 8, scale = 5) private BigDecimal commission;

    @Column(nullable = false, precision = 18, scale = 10) private BigDecimal marketRateValue;

    @Column(precision = 18, scale = 10) private BigDecimal tcea;
    @Column(precision = 18, scale = 10) private BigDecimal trea;
    @Column(precision = 18, scale = 10) private BigDecimal macaulayDuration;
    @Column(precision = 18, scale = 10) private BigDecimal modifiedDuration;
    @Column(precision = 18, scale = 10) private BigDecimal convexity;

    @Column(precision = 18, scale = 4) private BigDecimal dirtyPriceAmount;
    @Column(length = 3) private Currency dirtyPriceCurrency;

    @Column(precision = 18, scale = 4) private BigDecimal cleanPriceAmount;
    @Column(length = 3) private Currency cleanPriceCurrency;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "valuation_id")
    private List<CashFlowPeriodJpaEntity> cashFlow;

    @PrePersist
    protected void onCreate() {
        createdAt = new java.util.Date();
        updatedAt = new java.util.Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new java.util.Date();
    }
}