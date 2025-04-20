package org.grupo1.finanzas.estimating.interfaces.rest.transform;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Result;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.PeriodResource;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.ResultResource;
import java.util.stream.Collectors;

public class ResultResourceFromEntityAssembler {
    public static ResultResource toResourceFromEntity(Result result) {
        var periodResources = result.getPeriods().stream().map(period -> new PeriodResource(
                period.getNumber(),
                period.getTea(),
                period.getTes(),
                period.getGracia(),
                period.getSaldoInicial(),
                period.getInteres(),
                period.getCuota(),
                period.getAmortizacion(),
                period.getSaldoFinal()
        )).collect(Collectors.toList());

        return new ResultResource(
                result.getId(),
                result.getBondId(),
                result.getTcea(),
                result.getTrea(),
                result.getDuration(),
                result.getDurationMod(),
                result.getConvexity(),
                result.getMaxMarketPrice(),
                periodResources
        );
    }
}
