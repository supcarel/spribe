package com.supcarel.spribe.controler;

import com.supcarel.spribe.payload.request.PaymentRequest;
import com.supcarel.spribe.payload.request.PaymentStatusRequest;
import com.supcarel.spribe.payload.response.ErrorResponse;
import com.supcarel.spribe.payload.response.PaymentResponse;
import com.supcarel.spribe.service.PaymentService;
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

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment API", description = "API for payment management")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment created"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User does not have permission to pay for this booking", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest, @RequestHeader("User-Id") UUID userId) {
        PaymentResponse response = paymentService.createPayment(paymentRequest, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/process")
    @Operation(summary = "Emulate payment processing")
    public ResponseEntity<Void> processPayment(@RequestBody PaymentStatusRequest paymentRequest) {
        log.info("Query to process payment: {}", paymentRequest);
        paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by id")
    public ResponseEntity<String> getPaymentById(@PathVariable String paymentId) {
        //TODO return payment
        return ResponseEntity.ok("");
    }

    @GetMapping("/{paymentId}/status")
    @Operation(summary = "Check payment status")
    public ResponseEntity<String> checkPaymentStatus(@PathVariable String paymentId) {
        //TODO return payment status
        return ResponseEntity.ok("");
    }
}
