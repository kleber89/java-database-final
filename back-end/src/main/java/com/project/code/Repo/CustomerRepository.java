package com.project.code.Repo;

import com.project.code.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find a customer by email
    Customer findByEmail(String email);

    // Convenience method (note: JpaRepository already provides findById returning
    // Optional<Customer>)
    Customer findById(Long id);

    // Example additional query: find by name
    List<Customer> findByName(String name);

}
