package com.example.reactivetesting.controller;

import com.example.reactivetesting.dto.EmployeeDto;
import com.example.reactivetesting.entity.Employee;
import com.example.reactivetesting.mapper.EmployeeMapper;
import com.example.reactivetesting.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

//extend with
@WebFluxTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setup(){
        employee = Employee
                .builder()
                .id("122we67y")
                .firstName("Test")
                .lastName("User")
                .email("etest@gmail.com")
                .build();
    }


     @Test
     @DisplayName("Test save Employee")
     public void givenEmployee_whenCreateEmployee_thenReturnEmployee(){
         //given - precondition / setup
         EmployeeDto employeeDto = EmployeeMapper.mapToEmployeeDto(employee);
         BDDMockito.given(employeeService.saveEmployee(ArgumentMatchers.any(Employee.class)))
                   .willReturn(Mono.just(employeeDto));
         //when - action or behaviour that we are testing
         WebTestClient.ResponseSpec responseSpec = webTestClient.post()
                 .uri("/api/employees")
                 .contentType(MediaType.APPLICATION_JSON)
                 .accept(MediaType.APPLICATION_JSON)
                 .body(Mono.just(employeeDto), Employee.class)
                 .exchange();

         //then - verify the output
         responseSpec.expectStatus().isCreated()
                 //.expectBody(EmployeeDto.class)
                 .expectBody()
                 .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName());

     }


      @Test
      @DisplayName("Name to Display")
      public void givenEmployee_whenGetEmployeeById_thenReturnEmployee(){
          //given - precondition / setup
          String employeeId = "122we67y";
          BDDMockito.given(employeeService.getEmployeeById(ArgumentMatchers.anyString()))
                  .willReturn(Mono.just(EmployeeMapper.mapToEmployeeDto(employee)));
          //when - action or behaviour that we are testing
          WebTestClient.ResponseSpec responseSpec= webTestClient.get()
                  .uri("/api/employees/{id}", Collections.singletonMap("id", employeeId))
                  .exchange();

          //then - verify the output
          responseSpec.expectStatus().isOk()
                  .expectBody()
                  .consumeWith(System.out::println)
                  .jsonPath("$").isNotEmpty()
                  .jsonPath("$.id").isEqualTo(employeeId);
      }


       @Test
       @DisplayName("Get All Employees")
       public void givenEmployeeList_whenGetAllEmployees_thenReturnEmployeeList(){
           //given
           EmployeeDto employeeDto = EmployeeMapper.mapToEmployeeDto(employee);
           employee.setId("dfe123");
           EmployeeDto employeeDto1 = new EmployeeDto();
           List<EmployeeDto> employeeDtoList = List.of(employeeDto1, employeeDto);
           BDDMockito.given(employeeService.getAllEmployees()).willReturn(Flux.fromIterable(employeeDtoList));
           //when
           WebTestClient.ResponseSpec responseSpec = webTestClient.get()
                   .uri("/api/employees")
                   .exchange();
           //then - verify the output
           responseSpec.expectStatus().isOk()
                   .expectBody()
                   .jsonPath("$.size()").isEqualTo(employeeDtoList.size());
       }


        @Test
        @DisplayName("Test Delete Employee")
        public void givenEmployeeId_whenDeleteEmployee_thenDeleteEmployee(){
            //give
            Mono<Void> voidMono = Mono.empty();
            Mockito.when(employeeService.deleteEmployeeById(ArgumentMatchers.anyString()))
                    .thenReturn(voidMono);

            //when
            WebTestClient.ResponseSpec responseSpec = webTestClient.delete()
                    .uri("/api/employees/{id}", Collections.singletonMap("id", employee.getId()))
                    .exchange();

            //then
            responseSpec.expectStatus().isNoContent()
                    .expectBody().isEmpty();
        }


}
