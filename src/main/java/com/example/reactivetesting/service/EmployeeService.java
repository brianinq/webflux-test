package com.example.reactivetesting.service;

import com.example.reactivetesting.dto.EmployeeDto;
import com.example.reactivetesting.entity.Employee;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {
    Mono<EmployeeDto> saveEmployee(Employee employee);
    Mono<EmployeeDto> getEmployeeById(String id);
    Flux<EmployeeDto> getAllEmployees();
    Mono<Void> deleteEmployeeById(String id);
}
