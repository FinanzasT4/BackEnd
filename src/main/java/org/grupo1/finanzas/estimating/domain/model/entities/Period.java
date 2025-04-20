package org.grupo1.finanzas.estimating.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;
    private String tea;
    private String tes;
    private String gracia;

    private BigDecimal saldoInicial;
    private BigDecimal interes;
    private BigDecimal cuota;
    private BigDecimal amortizacion;
    private BigDecimal saldoFinal;
}
