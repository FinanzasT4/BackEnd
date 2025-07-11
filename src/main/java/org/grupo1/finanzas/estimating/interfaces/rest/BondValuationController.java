package org.grupo1.finanzas.estimating.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.grupo1.finanzas.estimating.domain.model.queries.GetAllValuationsByUserIdQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetValuationByIdQuery;
import org.grupo1.finanzas.estimating.domain.services.commandservices.BondValuationCommandService;
import org.grupo1.finanzas.estimating.domain.services.queryservices.BondValuationQueryService;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.CreateValuationResource;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.ValuationResource;
import org.grupo1.finanzas.estimating.interfaces.rest.transform.ValuationCommandFromResourceAssembler;
import org.grupo1.finanzas.estimating.interfaces.rest.transform.ValuationResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/valuations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Bond Valuations", description = "Endpoints for creating and managing bond valuations")
@RequiredArgsConstructor
public class BondValuationController {

    private final BondValuationCommandService commandService;
    private final BondValuationQueryService queryService;
    // ELIMINAMOS LA INYECCIÓN DE LOS ASSEMBLERS

    @PostMapping
    public ResponseEntity<ValuationResource> createValuation(@Valid @RequestBody CreateValuationResource resource) {
        var command = ValuationCommandFromResourceAssembler.toCommandFromResource(resource);

        // 2. Enviar el comando al servicio de aplicación
        var valuationId = commandService.handle(command)
                .orElseThrow(() -> new RuntimeException("Error while creating valuation"));

        // 3. Obtener los datos para la respuesta
        var valuationDto = queryService.handle(new GetValuationByIdQuery(valuationId))
                .orElseThrow(() -> new RuntimeException("Could not retrieve created valuation"));

        var valuationResource = ValuationResourceAssembler.toResourceFromDto(valuationDto);

        // 5. Devolver 201 Created
        return new ResponseEntity<>(valuationResource, HttpStatus.CREATED);
    }

    @GetMapping("/{valuationId}")
    public ResponseEntity<ValuationResource> getValuationById(@PathVariable Long valuationId) {
        return queryService.handle(new GetValuationByIdQuery(valuationId))
                .map(ValuationResourceAssembler::toResourceFromDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ValuationResource>> getValuationsByUserId(@PathVariable Long userId) {
        var dtoList = queryService.handle(new GetAllValuationsByUserIdQuery(userId));

        var resourceList = dtoList.stream()
                .map(ValuationResourceAssembler::toResourceFromDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resourceList);
    }
}