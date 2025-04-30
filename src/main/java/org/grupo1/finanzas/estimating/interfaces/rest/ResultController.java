package org.grupo1.finanzas.estimating.interfaces.rest;

import org.grupo1.finanzas.estimating.domain.model.commands.CreateResultCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.DeleteResultByIdCommand;
import org.grupo1.finanzas.estimating.domain.model.queries.GetAllResultsQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetResultByBondIdQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetResultByIdQuery;
import org.grupo1.finanzas.estimating.domain.services.ResultCommandService;
import org.grupo1.finanzas.estimating.domain.services.ResultQueryService;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.ResultResource;
import org.grupo1.finanzas.estimating.interfaces.rest.transform.ResultResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/results", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Results", description = "Results Management Endpoints")
public class ResultController {

    private final ResultQueryService resultQueryService;
    private final ResultCommandService resultCommandService;

    public ResultController(ResultQueryService resultQueryService, ResultCommandService resultCommandService) {
        this.resultQueryService = resultQueryService;
        this.resultCommandService = resultCommandService;
    }

    @GetMapping
    public ResponseEntity<List<ResultResource>> getAllResults() {
        var query = new GetAllResultsQuery();
        var results = resultQueryService.handle(query);
        var resultResources = results.stream().map(ResultResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(resultResources);
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<ResultResource> getResultById(@PathVariable Long resultId) {
        var query = new GetResultByIdQuery(resultId);
        var result = resultQueryService.handle(query);
        if (result.isEmpty()) return ResponseEntity.notFound().build();
        var resource = ResultResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return ResponseEntity.ok(resource);
    }

    @PostMapping
    public ResponseEntity<ResultResource> createResult(@RequestBody CreateResultCommand command) {
        var result = resultCommandService.handle(command);
        if (result.isEmpty()) return ResponseEntity.badRequest().build();
        var resource = ResultResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @GetMapping("/by-bond/{bondId}")
    public ResponseEntity<ResultResource> getResultByBondId(@PathVariable Long bondId) {
        var query = new GetResultByBondIdQuery(bondId);
        var result = resultQueryService.handle(query);
        if (result.isEmpty()) return ResponseEntity.notFound().build();
        var resource = ResultResourceFromEntityAssembler.toResourceFromEntity(result.get());
        return ResponseEntity.ok(resource);
    }


    @DeleteMapping("/{resultId}")
    public ResponseEntity<?> deleteResult(@PathVariable Long resultId) {
        resultCommandService.handle(new DeleteResultByIdCommand(resultId));
        return ResponseEntity.ok("Result (and its associated Bond) successfully deleted.");
    }
}
