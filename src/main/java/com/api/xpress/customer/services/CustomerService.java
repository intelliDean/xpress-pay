package com.api.xpress.customer.services;

import com.api.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.api.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.api.xpress.auth_config.security.user_services.AuthenticatedUser;
import com.api.xpress.customer.data.dtos.CustomerRegisterRequest;
import com.api.xpress.customer.data.dtos.CustomerRegistrationResponse;
import com.api.xpress.customer.data.dtos.CustomerResponse;
import com.api.xpress.customer.data.dtos.MyAirtimeRequestDTO;
import com.api.xpress.customer.data.models.Customer;

import java.io.IOException;

public interface CustomerService {

    CustomerResponse signUp(CustomerRegisterRequest request);

    CustomerRegistrationResponse verifyCustomerMail(String email, String token);

    AirtimePurchaseResponse buyAirtime(PurchaseAirtimeRequestDTO requestDTO) throws IOException;

    AirtimePurchaseResponse buyMyselfAirtime(MyAirtimeRequestDTO requestDTO, AuthenticatedUser currentUser) throws IOException;

    Customer currentCustomer(AuthenticatedUser currentUser);
}
