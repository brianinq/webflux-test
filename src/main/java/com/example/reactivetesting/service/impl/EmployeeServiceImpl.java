package com.example.reactivetesting.service.impl;

import com.example.reactivetesting.dto.EmployeeDto;
import com.example.reactivetesting.entity.Employee;
import com.example.reactivetesting.mapper.EmployeeMapper;
import com.example.reactivetesting.repository.EmployeeRepository;
import com.example.reactivetesting.service.EmployeeService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Mono<EmployeeDto> saveEmployee(Employee employee) {
        Mono<Employee> savedEntry = employeeRepository.save(employee);
        return savedEntry.map(EmployeeMapper::mapToEmployeeDto);
    }

    @Override
    public Mono<EmployeeDto> getEmployeeById(String id) {
        return employeeRepository.findById(id)
                .map(EmployeeMapper::mapToEmployeeDto)
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .map(EmployeeMapper::mapToEmployeeDto)
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<Void> deleteEmployeeById(String id) {
        return employeeRepository.deleteById(id);
    }
}
