package com.api.xpress.customer.data.repositories;

import com.api.xpress.auth_config.user.data.models.User;
import com.api.xpress.customer.data.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerByUserEmailAddress(String emailAddress);
}
