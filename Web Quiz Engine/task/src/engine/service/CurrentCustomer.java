package engine.service;

import engine.domain.Customer;
import engine.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentCustomer {

    @Autowired
    CustomerRepository customerRepository;

    public Customer get() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(username).get();
    }
}
