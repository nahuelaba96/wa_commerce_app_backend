package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CreateCustomerRequest;
import com.example.backend.dto.CustomerResponse;
import com.example.backend.entity.Customer;
import com.example.backend.mapper.CustomerMapper;
import com.example.backend.repository.MerchantRepository;
import com.example.backend.repository.CustomerRepository;

@Service
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final MerchantRepository merchantRepository;

    public CustomerService(CustomerRepository repository, CustomerMapper mapper, MerchantRepository merchantRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.merchantRepository = merchantRepository;
    }

    public List<CustomerResponse> getCustomers(Long merchantId) {
        return repository.findAllByMerchant_Id(merchantId).stream().map(mapper::toResponse).toList();
    }

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = mapper.toEntity(request);
        customer.setMerchant(merchantRepository.findById(request.merchantId())
                .orElseThrow(() -> new RuntimeException("No se encontró el comercio con ID: " + request.merchantId())));
        return mapper.toResponse(repository.save(customer));
    }
}
