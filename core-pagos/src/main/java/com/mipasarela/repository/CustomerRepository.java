package com.mipasarela.repository;

import com.mipasarela.domain.Customer;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    Optional<Customer> findByNumeroDni(String numeroDni);
}