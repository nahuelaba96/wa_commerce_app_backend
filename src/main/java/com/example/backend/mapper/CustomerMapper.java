package com.example.backend.mapper;

import org.springframework.stereotype.Component;

import com.example.backend.dto.CreateCustomerRequest;
import com.example.backend.dto.CustomerResponse;
import com.example.backend.entity.Customer;

@Component
public class CustomerMapper {
    public Customer toEntity(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
        return customer;
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getPhone(),
            customer.getEmail(),
            customer.getMerchant() == null ? null : customer.getMerchant().getId());
    }
}
