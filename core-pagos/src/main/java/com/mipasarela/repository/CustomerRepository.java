package com.mipasarela.repository;

import com.mipasarela.domain.Customer;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
    Optional<Customer> findByDniNumber(String dniNumber);
    Optional<Customer> findByEmail(String email);
}