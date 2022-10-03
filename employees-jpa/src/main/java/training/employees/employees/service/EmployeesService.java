package training.employees.employees.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import training.employees.employees.dto.CreateEmployeeCommand;
import training.employees.employees.dto.EmployeeDetailsDto;
import training.employees.employees.dto.EmployeeDto;
import training.employees.employees.dto.UpdateEmployeeCommand;
import training.employees.employees.repository.EmployeeNotFoundException;
import training.employees.employees.repository.EmployeesRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeesService {

    private EmployeesRepository repository;

    private EmployeeMapper employeeMapper;

    public List<EmployeeDto> listEmployees(Optional<String> prefix) {
        // TODO prefix paraméter használata
        return employeeMapper.toDto(repository.findAll());
    }

    public EmployeeDetailsDto findEmployeeById(long id) {
        return employeeMapper.toDto(repository
                .findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id)));
    }

    public EmployeeDetailsDto createEmployee(CreateEmployeeCommand command) {
        var employee = employeeMapper.toEntity(command);
        repository.save(employee);
        return employeeMapper.toDto(employee);
    }

    @Transactional
    public EmployeeDetailsDto updateEmployee(long id, UpdateEmployeeCommand command) {
        var employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        employee.setYearOfBirth(command.getYearOfBirth());
        return employeeMapper.toDto(employee);
    }

    public void deleteEmployee(long id) {
        repository.deleteById(id);
    }
}
