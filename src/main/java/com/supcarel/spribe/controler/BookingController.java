package com.supcarel.spribe.controler;

import com.supcarel.spribe.payload.request.BookingRequest;
import com.supcarel.spribe.payload.response.BookingResponse;
import com.supcarel.spribe.payload.response.ErrorResponse;
import com.supcarel.spribe.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking API", description = "API for booking management")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create booking")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unit or user not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Unit is not available for booking", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest bookingRequest,
            @RequestHeader("User-Id") UUID userId) {
        log.info("Query to create booking: {}, userId: {}", bookingRequest, userId);
        BookingResponse response = bookingService.createBooking(bookingRequest, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking canseled"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Booking does not belong to user", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable UUID bookingId,
            @RequestHeader("User-Id") UUID userId) {
        log.info("Query to cancel booking: {}, userId: {}", bookingId, userId);
        BookingResponse response = bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok(response);
    }
}
