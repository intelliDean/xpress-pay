package com.api.xpress.customer.controllers;

import com.api.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.api.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.api.xpress.auth_config.security.user_services.AuthenticatedUser;
import com.api.xpress.auth_config.user.annotation.CurrentUser;
import com.api.xpress.customer.data.dtos.CustomerRegisterRequest;
import com.api.xpress.customer.data.dtos.CustomerResponse;
import com.api.xpress.customer.data.dtos.MyAirtimeRequestDTO;
import com.api.xpress.customer.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
@Tag(name = "Customer Controller")
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("register")
    @Operation(summary = "New customer registers")
    public ResponseEntity<CustomerResponse> register(@RequestBody CustomerRegisterRequest request) {
        return new ResponseEntity<>(customerService.signUp(request), HttpStatus.CREATED);
    }

    @PostMapping("buy-airtime")
    @Operation(summary = "Buy others airtime")
    public ResponseEntity<AirtimePurchaseResponse> buyAirtime(
            @RequestBody PurchaseAirtimeRequestDTO requestDTO) throws IOException {
        return ResponseEntity.ok(
                customerService.buyAirtime(requestDTO)
        );
    }

    @PostMapping("my-airtime")
    @Operation(summary = "Buy myself airtime")
    public ResponseEntity<AirtimePurchaseResponse> buyMyselfAirtime(
            @RequestBody MyAirtimeRequestDTO requestDTO, @CurrentUser AuthenticatedUser currentUser) throws IOException {
        return ResponseEntity.ok(
                customerService.buyMyselfAirtime(requestDTO, currentUser)
        );
    }
}