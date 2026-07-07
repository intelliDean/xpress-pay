package com.api.xpress.customer.services;

import com.api.xpress.airtime.data.dtos.AirtimePurchaseResponse;
import com.api.xpress.airtime.data.dtos.PurchaseAirtimeRequestDTO;
import com.api.xpress.airtime.service.AirtimePurchaseService;
import com.api.xpress.auth_config.security.auth_utils.JwtService;
import com.api.xpress.auth_config.security.auth_utils.XpressAuthToken;
import com.api.xpress.auth_config.security.user_services.AuthenticatedUser;
import com.api.xpress.auth_config.user.data.enums.Role;
import com.api.xpress.auth_config.user.data.models.User;
import com.api.xpress.auth_config.user.data.models.XpressToken;
import com.api.xpress.auth_config.user.services.UserService;
import com.api.xpress.auth_config.user.services.XpressTokenService;
import com.api.xpress.customer.data.dtos.CustomerRegisterRequest;
import com.api.xpress.customer.data.dtos.CustomerRegistrationResponse;
import com.api.xpress.customer.data.dtos.CustomerResponse;
import com.api.xpress.customer.data.dtos.MyAirtimeRequestDTO;
import com.api.xpress.customer.data.models.Customer;
import com.api.xpress.customer.data.repositories.CustomerRepository;
import com.api.xpress.notification.mail.MailService;
import com.api.xpress.notification.mail.dto.EmailRequest;
import com.api.xpress.notification.mail.dto.MailInfo;
import com.api.xpress.verification_token.model.VerificationToken;
import com.api.xpress.verification_token.service.VerificationTokenService;
import com.api.xpress.xceptions.UserNotFoundException;
import com.api.xpress.xceptions.XpressException;
import com.api.xpress.xpress_utils.XpressUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final VerificationTokenService verificationTokenService;
    private final XpressTokenService xpressTokenService;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final AirtimePurchaseService airtimePurchaseService;
    private final MailService mailService;
    private final JwtService jwtService;

    @Override    //the method that register user
    public CustomerResponse signUp(CustomerRegisterRequest request) {
        //customer object is created and initialized with its field parameters
        Customer savedCustomer = customerRepository.save(
                Customer.builder()
                        .user(
                                User.builder()
                                        .fullName(request.fullName())
                                        .emailAddress(request.emailAddress())
                                        .password(passwordEncoder.encode(request.password()))
                                        .roles(Collections.singleton(Role.CUSTOMER))
                                        .build())
                        .phoneNumber(request.phoneNumber())
                        .balance(BigDecimal.ZERO)
                        .build());
        sendVerificationMail(savedCustomer);    //verification mail is sent to user after registration

        return CustomerResponse.builder()
                .message("Successful! Check your mail to verify")
                .build();
    }

    @Override    //this method verifies the user email after sign up
    public CustomerRegistrationResponse verifyCustomerMail(String token, String email) {
        //the verification token saved in the database is retrieved, using email and string token
        VerificationToken verificationToken = verificationTokenService.findByTokenAndEmail(token, email);
        //if the token object is found, the email is also used to find user
        Customer customer = getCustomerByEmail(email);
        //if both user and verification token are found, then the user account is activated
        if (verificationToken != null) {
            customer.getUser().setEnabled(true);    //the user account is activated
            verificationToken.setRevoked(true);        //the verification token is revoked
            //xpress token is created and saved.
            // user is also saved automatically because of the cascade relationship between them
            XpressToken xpressToken = saveXpressToken(customer);
            return CustomerRegistrationResponse.builder()
                    .message("Registration successful")
                    .xpressAuthToken(
                            XpressAuthToken.builder()
                                    .accessToken(xpressToken.getAccessToken())
                                    .refreshToken(xpressToken.getRefreshToken())
                                    .build()
                    ).build();
        }
        throw new XpressException("Verification failed");
    }

    @Override
    public AirtimePurchaseResponse buyAirtime(PurchaseAirtimeRequestDTO requestDTO) {
        //this method is called when user wants to purchase airtime
        return airtimePurchaseService.buyAirtime(requestDTO);
    }

    @Override
    public AirtimePurchaseResponse buyMyselfAirtime(MyAirtimeRequestDTO requestDTO, AuthenticatedUser currentUser) {
        Customer customer = currentCustomer(currentUser);
        return airtimePurchaseService.buyAirtime(
                PurchaseAirtimeRequestDTO.builder()
                        .amount(requestDTO.amount())
                        .phoneNumber(customer.getPhoneNumber())
                        .userId(customer.getUser().getId())
                        .build()
        );
    }

    @Override
    public Customer currentCustomer(AuthenticatedUser currentUser) {
        return customerRepository.findCustomerByUserEmailAddress(
                currentUser.getUsername()
        ).orElseThrow(UserNotFoundException::new);
    }


    private Customer getCustomerByEmail(String email) {    //customer retrieves by email
        return customerRepository.findCustomerByUserEmailAddress(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private XpressToken saveXpressToken(Customer customer) {        //xpress token is created and saved
        final User user = customer.getUser();
        //this generates the access token
        final String accessToken = jwtService.generateAccessToken(user);
        //this generates the refresh token
        final String refreshToken = jwtService.generateRefreshToken(user);
        XpressToken xpressToken = XpressToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        xpressTokenService.saveToken(xpressToken);        //token is saved
        return xpressToken;
    }

    private void sendVerificationMail(Customer customer) {
        //this sends a verification email to user using thymeleaf template
        final String fullName = customer.getUser().getFullName();
        final String url = generateUrl(customer);    //this generates the url for the verification endpoint

        final Context context = new Context();
        context.setVariables(    //these are the placeholders in the email template
                Map.of(
                        "fullName", fullName,
                        "verifyUrl", url,
                        "token", url
                )
        );
        final String content = templateEngine.process("verify_mail", context);
        EmailRequest request = EmailRequest.builder()
                .to(List.of(new MailInfo(fullName, customer.getUser().getEmailAddress())))
                .subject("Email verification")
                .htmlContent(content)
                .build();
        mailService.sendMail(request);
    }

    private String generateUrl(Customer customer) {
        final String token = XpressUtils.generateToken(10);
        final String email = customer.getUser().getEmailAddress();

        verificationTokenService.saveToken(VerificationToken.builder()
                        .token(token)
                        .email(email)
                        .expireAt(LocalDateTime.now().plusHours(3))
                        .build()
        );
        return XpressUtils.getUrl(token, email);
    }
}
