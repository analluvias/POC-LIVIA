package org.example.rest.controller;

import java.awt.print.Book;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponseWithAdresses;
import org.example.service.impl.CustomerServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDtoResponse save(@RequestBody @Valid CustomerDtoRequest request){

        return service.save(  request  );

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDtoResponseWithAdresses getCustomerById(@PathVariable("id") UUID id){

        return service.getCustomerById(id);

    }

    @GetMapping("/searchcustomers")
    @ResponseStatus(HttpStatus.OK)
    public Page<CustomerDtoResponse> searchCustomers(
            Pageable pageable,
            @RequestParam(name = "customerType", required = false) String customerType,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(name = "document", required = false) String document)

    {
        return service.searchCustomers(customerType, name,
                pageable, email, phoneNumber, document);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete (@PathVariable("id") UUID uuid){

        service.delete(uuid);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDtoResponse putCustomer(@PathVariable("id") UUID uuid,
                            @RequestBody @Valid CustomerDtoRequest request){

        return service.update(uuid, request);
    }

}
