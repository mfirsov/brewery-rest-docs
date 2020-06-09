package guru.springframework.msscbrewery.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbrewery.services.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseBodySnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@WebMvcTest({CustomerController.class})
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureRestDocs
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @Test
    public void testGetCustomer() throws Exception {
        given(customerService.getCustomerById(any(UUID.class))).willReturn(CustomerDto.builder().name("Test").id(UUID.randomUUID()).build());
        mockMvc.perform(get("/api/v1/customer/{customerId}", UUID.randomUUID().toString()).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("v1/customer",
                        pathParameters(
                                parameterWithName("customerId").description("Id of the Customer")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of the Customer").type(JsonFieldType.STRING),
                                fieldWithPath("name").description("Name of the Customer").type(JsonFieldType.STRING)
                        )
                ));
    }

    @Test
    public void testHandlePost() throws Exception {
        CustomerDto customerDto = CustomerDto.builder().name("Test").id(UUID.randomUUID()).build();
        String serializedCustomerDto = objectMapper.writeValueAsString(customerDto);
        given(customerService.saveNewCustomer(any(CustomerDto.class))).willReturn(customerDto);
        mockMvc.perform(post("/api/v1/customer").contentType(MediaType.APPLICATION_JSON_UTF8).content(serializedCustomerDto))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andDo(document("v1/customer",
                        requestFields(
                                fieldWithPath("id").description("Id of the Customer").type(JsonFieldType.STRING),
                                fieldWithPath("name").description("Name of the Customer").type(JsonFieldType.STRING)
                        )
                ));
    }

    @Test
    public void testHandleUpdate() throws Exception {
        CustomerDto customerDto = CustomerDto.builder().name("Test").id(UUID.randomUUID()).build();
        String serializedCustomerDto = objectMapper.writeValueAsString(customerDto);
        mockMvc.perform(put("/api/v1/customer/{customerId}", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(serializedCustomerDto))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(document("v1/customer",
                        pathParameters(
                                parameterWithName("customerId").description("Id of the Customer")
                        ),
                        requestFields(
                                fieldWithPath("id").description("Id of the Customer").type(JsonFieldType.STRING),
                                fieldWithPath("name").description("Name of the Customer").type(JsonFieldType.STRING)
                        )
                ));
    }

    @Test
    public void testDeleteById() throws Exception {
        mockMvc.perform(delete("/api/v1/customer/{customerId}", UUID.randomUUID().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("v1/customer",
                        pathParameters(
                                parameterWithName("customerId").description("Id of the Customer")
                        )
                ));
    }
}