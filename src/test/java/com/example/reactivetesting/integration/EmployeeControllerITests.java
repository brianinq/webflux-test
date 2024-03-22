package com.example.reactivetesting.integration;

import com.example.reactivetesting.dto.EmployeeDto;
import com.example.reactivetesting.entity.Employee;
import com.example.reactivetesting.mapper.EmployeeMapper;
import com.example.reactivetesting.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerITests {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Employee employee;

    @BeforeEach
    void setup() {
        //employeeRepository.deleteAll().subscribe();
        employee = Employee
                .builder()
                .firstName("Test")
                .lastName("User")
                .email("etest@gmail.com")
                .build();
    }


    @Test
    @DisplayName("Name to Display")
    public void givenEmployee_whenSaveEmployee_thenReturnEmployee() {
        //given
        EmployeeDto employeeDto = EmployeeMapper.mapToEmployeeDto(employee);

        //when
        webTestClient.post()
                     .uri("/api/employees")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(employeeDto), EmployeeDto.class)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectBody()
                     .jsonPath("$").isNotEmpty()
                     .jsonPath("$.id").isNotEmpty()
                     .jsonPath("$.firstName").isEqualTo(employee.getFirstName())
                     .jsonPath("$.lastName").isEqualTo(employee.getLastName())
                     .consumeWith(System.out::println);

        //then - verify the output
    }


    @Test
    @DisplayName("Test Get Employee By Id")
    public void givenEmployee_whenFindById_thenReturnEmployee() {
        //given - precondition / setup
        Employee savedEmployee = employeeRepository.save(employee).block();
        assert savedEmployee != null;

        //when
        webTestClient.get()
                     .uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$").isNotEmpty()
                     .jsonPath("$.email").isEqualTo(employee.getEmail())
                     .consumeWith(System.out::println);
        //then - verify the output
    }

    @Test
    @DisplayName("Get All Employees")
    public void givenEmployeeList_whenGetAllEmployees_thenReturnEmployeeList() {
        //given
        Employee employee1 = Employee
                .builder()
                .firstName("Benard")
                .lastName("Koli")
                .email("eloki@g.com")
                .build();
        employeeRepository.saveAll(List.of(employee1, employee));
        webTestClient
                .get()
                .uri("/api/employees")
                .exchange().expectStatus().isOk()
                .expectBodyList(EmployeeDto.class);
    }


    @Test
    @DisplayName("Test Delete Employee")
    public void givenEmployeeId_whenDeleteEmployee_thenDeleteEmployee() {
        Employee savedEmployee = employeeRepository.save(employee).block();
        assert savedEmployee != null;

        webTestClient
                .delete()
                .uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
                .exchange().expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

}
