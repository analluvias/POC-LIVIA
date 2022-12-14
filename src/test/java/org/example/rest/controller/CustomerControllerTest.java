package org.example.rest.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.domain.entity.Address;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponseWithAdresses;
import org.example.rest.exception.exceptions.DocumentInUseException;
import org.example.rest.exception.exceptions.ObjectNotFoundException;
import org.example.rest.exception.exceptions.TooManyAddressesException;
import org.example.service.impl.AddressServiceImpl;
import org.example.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc
class CustomerControllerTest {

    static String CUSTOMER_API = "/api/customers";

    @Autowired
    MockMvc mvc;

    @MockBean
    CustomerServiceImpl customerServiceImpl;

    @MockBean
    AddressServiceImpl addressServiceImpl;

    @Test
    @DisplayName("Shoud create a customer")
    void createCustomerTest() throws Exception {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana livia")
                .email("emailteste@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        CustomerDtoResponse customerDtoResponse = CustomerDtoResponse.builder()
                .customerType(CustomerType.FISICA)
                .email("emailteste@gmail.com")
                .name("ana livia")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        BDDMockito.when( customerServiceImpl.existsCustomersByDocument(customerDtoResponse.getDocument()) )
                        .thenReturn(false);

        BDDMockito.given( customerServiceImpl.save( dto ) ).willReturn( customerDtoResponse );

        String json = new ObjectMapper().writeValueAsString( dto );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isCreated() )
                .andExpect( jsonPath("customerType")
                        .value( dto.getCustomerType().toString() ) )
                .andExpect( jsonPath("email").
                        value( dto.getEmail() ))
                .andExpect( jsonPath("name").
                        value( dto.getName() ))
                .andExpect(  jsonPath("phoneNumber").
                        value( dto.getPhoneNumber() ))
                .andExpect( jsonPath("document").
                        value( dto.getDocument() ));


    }

    @Test
    @DisplayName("must not create a customer with many addresse")
    void dontCreateCustomerTest() throws Exception {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        AddressDtoRequest addressDtoRequest3 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest4 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        AddressDtoRequest addressDtoRequest5 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest6 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);
        addressDtoRequests.add(addressDtoRequest3);
        addressDtoRequests.add(addressDtoRequest4);
        addressDtoRequests.add(addressDtoRequest5);
        addressDtoRequests.add(addressDtoRequest6);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana livia")
                .email("emailteste@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        CustomerDtoResponse customerDtoResponse = CustomerDtoResponse.builder()
                .customerType(CustomerType.FISICA)
                .email("emailteste@gmail.com")
                .name("ana livia")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        BDDMockito.given( customerServiceImpl.save( dto ) )
                .willThrow( new TooManyAddressesException());

        String json = new ObjectMapper().writeValueAsString( dto );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isBadRequest() );

    }

    @Test
    @DisplayName("Should throw validation error when email is empty")
    void createInvalidCustomerTestWithoutEmail() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisi????o HTTP para o meu m??todo verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("message").value("Email cannot be empty"));
    }

    @Test
    @DisplayName("Should throw validation error when name is empty")
    void createInvalidCustomerTestWithoutName() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisi????o HTTP para o meu m??todo verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("message").value("Name cannot be empty"));
    }

    @Test
    @DisplayName("Should throw validation error when phone number is empty")
    void createInvalidCustomerTestWithoutPhoneNumber() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana")
                .email("ana@gmail.com")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisi????o HTTP para o meu m??todo verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("message").value("Phone number cannot be empty"));
    }

    @Test
    @DisplayName("Should throw validation error when document is empty")
    void createInvalidCustomerTestWithoutDocument() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                //.document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisi????o HTTP para o meu m??todo verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("message").value("document cannot be empty"));
    }

    @Test
    @DisplayName("Should throw validation error when document is already in use")
    void createInvalidCustomerTestDocumentInUse() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        BDDMockito.given( customerServiceImpl.save( Mockito.any(CustomerDtoRequest.class) ) )
                .willThrow( new DocumentInUseException());

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisi????o HTTP para o meu m??todo verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isConflict() )
                .andExpect(jsonPath("message").value("This document is already in use."));
    }

    @Test
    @DisplayName("should get a customer by its id")
    void shouldGetACustomerById() throws Exception {

        UUID id = UUID.randomUUID();

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();

        AddressDtoResponse addressDtoResponse = AddressDtoResponse.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addressDtoResponses.add(addressDtoResponse);

        CustomerDtoResponseWithAdresses customer = CustomerDtoResponseWithAdresses.builder()
        .email("ana@gmail.com")
        .id(id)
        .customerType(  CustomerType.FISICA  )
        .document("160.917.000-81")
        .addresses(  addressDtoResponses  )
        .build();

        BDDMockito.given( customerServiceImpl.getCustomerById(id) ).willReturn( customer );


        //execu????o (when)

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc.perform( request )
                .andExpect( status().isOk() );
    }

    @Test
    @DisplayName("should return error when the customer does not exist")
    void ErrorCustomerDoesNotExist() throws Exception {

        UUID id = UUID.randomUUID();

        BDDMockito.given( customerServiceImpl.getCustomerById(id) )
                .willThrow( new ObjectNotFoundException("Customer not found."));


        //execu????o (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc.perform( request )
                .andExpect( status().isBadRequest() );
    }

    @Test
    @DisplayName("Should search a customer by properties ")
    void shouldSearchACustomer() throws Exception {

        UUID id = UUID.randomUUID();

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();

        AddressDtoResponse addressDtoResponse = AddressDtoResponse.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addressDtoResponses.add(addressDtoResponse);

        CustomerDtoResponse customer = CustomerDtoResponse.builder()
                .name("Ana L??via Meira")
                .email("ana@gmail.com")
                .id(id)
                .customerType(  CustomerType.FISICA  )
                .document("160.917.000-81")
                .build();

        Page<CustomerDtoResponse> page = new PageImpl<>(List.of(customer), Pageable.ofSize(20), 0);


        BDDMockito.given( customerServiceImpl.searchCustomers(null, "ana", Pageable.ofSize(10),
                        null, null, null) )
                .willReturn(page);

        //execu????o (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMER_API.concat("/searchcustomers?name=a"))
                .accept(MediaType.APPLICATION_JSON);


        // verifica????o
        mvc.perform( request )
                .andExpect( status().isOk() );
    }

    @Test
    @DisplayName("should delete a customer and their addresses")
    void shouldDeleteACustomerAndHisAddresses() throws Exception {

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                            .id(uuid)
                            .customerType(CustomerType.FISICA)
                            .name("ana")
                            .document("160.917.000-81")
                            .email("ana@gmail.com")
                            .phoneNumber("83999999999")
                            .build();

        BDDMockito.given( customerServiceImpl.getCustomer(uuid) )
                .willReturn(Optional.ofNullable(customer));

        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        doNothing().when(customerServiceImpl).delete(uuid);

        //execu????o (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(CUSTOMER_API + "/" + uuid)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should throw error when try to delete a nonexistent customer")
    void shouldNotDeleteACustomerInexistentAndHisAddresses() throws Exception {

        UUID uuid = UUID.randomUUID();


        doThrow( new ObjectNotFoundException("the customer you tried to delete does not exist.") )
                .when( customerServiceImpl ).delete(uuid);


        //execu????o (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(CUSTOMER_API + "/" + uuid)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Shoud update a customer")
    void updateCustomerTest() throws Exception {

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.135-000")
                .district("Jo??o Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("para??ba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana livia")
                .email("emailteste@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        CustomerDtoResponse customerDtoResponse = CustomerDtoResponse.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .email("emailteste@gmail.com")
                .name("ana livia")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        BDDMockito.given( customerServiceImpl.update(uuid, dto ) ).willReturn( customerDtoResponse );

        String json = new ObjectMapper().writeValueAsString( dto );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(CUSTOMER_API + "/" + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isOk() )
                .andExpect(jsonPath("id").hasJsonPath());


    }

}
