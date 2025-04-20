package org.grupo1.finanzas.estimating.interfaces.rest;

import org.grupo1.finanzas.estimating.domain.model.commands.DeleteBondByIdCommand;
import org.grupo1.finanzas.estimating.domain.model.queries.GetAllBondsQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetBondByIdQuery;
import org.grupo1.finanzas.estimating.domain.services.BondCommandService;
import org.grupo1.finanzas.estimating.domain.services.BondQueryService;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.BondResource;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.CreateBondResource;
import org.grupo1.finanzas.estimating.interfaces.rest.transform.BondResourceFromEntityAssembler;
import org.grupo1.finanzas.estimating.interfaces.rest.transform.CreateBondCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/bonds", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Bonds", description = "Bond Management Endpoints")
public class BondController {
    private final BondCommandService bondCommandService;
    private final BondQueryService bondQueryService;

    public BondController(BondCommandService bondCommandService, BondQueryService bondQueryService) {
        this.bondCommandService = bondCommandService;
        this.bondQueryService = bondQueryService;
    }

    @GetMapping
    public ResponseEntity<List<BondResource>> getAllBonds() {
        var query = new GetAllBondsQuery();
        var bonds = bondQueryService.handle(query);
        var bondResources = bonds.stream().map(BondResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(bondResources);
    }

    @GetMapping("/{bondId}")
    public ResponseEntity<BondResource> getBondById(@PathVariable Long bondId) {
        var query = new GetBondByIdQuery(bondId);
        var bond = bondQueryService.handle(query);
        if (bond.isEmpty()) return ResponseEntity.badRequest().build();
        var resource = BondResourceFromEntityAssembler.toResourceFromEntity(bond.get());
        return ResponseEntity.ok(resource);
    }

    @PostMapping
    public ResponseEntity<BondResource> createBond(@RequestBody CreateBondResource resource) {
        var command = CreateBondCommandFromResourceAssembler.toCommandFromResource(resource);
        var bond = bondCommandService.handle(command);
        if (bond.isEmpty()) return ResponseEntity.badRequest().build();
        var bondResource = BondResourceFromEntityAssembler.toResourceFromEntity(bond.get());
        return new ResponseEntity<>(bondResource, HttpStatus.CREATED);
    }

    @DeleteMapping("/{bondId}")
    public ResponseEntity<String> deleteBond(@PathVariable Long bondId) {
        bondCommandService.handle(new DeleteBondByIdCommand(bondId));
        return ResponseEntity.ok("Bond with given ID successfully deleted");
    }
}
