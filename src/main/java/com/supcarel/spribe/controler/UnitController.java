package com.supcarel.spribe.controler;

import com.supcarel.spribe.payload.request.UnitRequest;
import com.supcarel.spribe.payload.request.UnitSearchRequest;
import com.supcarel.spribe.payload.response.PageableResponse;
import com.supcarel.spribe.payload.response.UnitResponse;
import com.supcarel.spribe.payload.response.UnitTypeResponse;
import com.supcarel.spribe.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
@Tag(name = "Unit API", description = "API for unit management")
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    @Operation(summary = "Create unit")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Unit created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<UnitResponse> create(
            @RequestBody UnitRequest unitRequest,
            @RequestHeader("User-Id") UUID userId) {
        log.info("Create unit request user: {}", userId);
        UnitResponse response = unitService.createUnit(unitRequest, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{unitId}")
    @Operation(summary = "Get unit by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unit found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Unit not found")
    })
    public ResponseEntity<UnitResponse> get(@PathVariable UUID unitId) {
        log.info("Get unit by id request: {}", unitId);
        UnitResponse response = unitService.getUnitById(unitId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Update unit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unit updated"),
            @ApiResponse(responseCode = "201", description = "Unit created"),
            @ApiResponse(responseCode = "404", description = "Unit not found")
    })
    public ResponseEntity<UnitResponse> update(@RequestBody UnitRequest unitRequest, @RequestHeader("User-Id") UUID userId) {
        log.info("Update unit id: {}", unitRequest.getId());
        UnitResponse response = unitService.updateUnit(unitRequest, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{unitId}")
    @Operation(summary = "Delete unit by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Unit deleted"),
            @ApiResponse(responseCode = "403", description = "Unit not owned by user"),
            @ApiResponse(responseCode = "404", description = "Unit not found")
    })
    public ResponseEntity<UnitResponse> delete(@PathVariable UUID unitId, @RequestHeader("User-Id") UUID userId) {
        unitService.deleteUnitById(unitId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @Operation(summary = "Find units by search request")
    public ResponseEntity<PageableResponse<UnitResponse>> searchUnits(
            @RequestBody UnitSearchRequest searchRequest,
            Pageable pageable) {
        log.info("Search units request: {}", searchRequest);
        PageableResponse<UnitResponse> response = unitService.searchUnits(searchRequest, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    @Operation(summary = "Get all unit types")
    public ResponseEntity<List<UnitTypeResponse>> getAllTypes() {
        return ResponseEntity.ok(unitService.getAllUnitTypes());
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get unit statistics")
    public ResponseEntity<Map<String, Object>> getUnitStatistics() {
        Map<String, Object> statistics = unitService.getUnitStatistics();
        return ResponseEntity.ok(statistics);
    }
}
