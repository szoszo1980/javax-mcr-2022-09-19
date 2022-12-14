package training.employees.employees.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import training.employees.employees.dto.*;
import training.employees.employees.repository.AddressesRepository;
import training.employees.employees.repository.EmployeeNotFoundException;
import training.employees.employees.repository.EmployeesRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeesService {

    private EmployeesRepository repository;

    private AddressesRepository addressesRepository;

    private EmployeeMapper employeeMapper;

    private MeterRegistry meterRegistry;

    @PostConstruct
    public void initCounter() {
        Counter.builder("employees.created")
                .baseUnit("employees")
                .description("Number of created employees")
                .register(meterRegistry);

    }

    public List<EmployeeDto> listEmployees(Optional<String> prefix) {
        if (prefix.isEmpty()) {
//            return employeeMapper.toDto(repository.findAll());
            return repository.findEmployeeDtos();
        }
        else {
//            return employeeMapper.toDto(repository.findEmployeesByNameLike(prefix.get().toLowerCase() + "%"));
            return repository.findEmployeeDtosByNameLike(prefix.get().toLowerCase() + "%");
        }
    }

    public EmployeeDetailsDto findEmployeeById(long id) {
        return employeeMapper.toDto(repository
                .findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id)));
    }

    public EmployeeDetailsDto createEmployee(CreateEmployeeCommand command) {
        var employee = employeeMapper.toEntity(command);
        repository.save(employee);

        meterRegistry.counter("employees.created").increment();

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

    @Transactional
    public AddressDto createAddress(long employeeId, CreateAddressCommand command) {
        var address = employeeMapper.toEntity(command);

        var employee = repository.getReferenceById(employeeId);
        address.setEmployee(employee);

        addressesRepository.save(address);
//        var employee = repository.findById(employeeId)
//                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
//        employee.addAddress(address);
        return employeeMapper.toDto(address);
    }
    public List<AddressDto> listAddresses(long employeeId) {
        log.info("Load employee");
//        var employee = repository.findEmployeeWithAddresses(employeeId)
//                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
//        log.info("Load addresses");
//        var addresses = employee.getAddresses();

        var addresses = addressesRepository.findAddressByEmployeeId(employeeId);

        return employeeMapper.toAddressDto(addresses);
    }

    public void listEmployeesWithAddresses() {
        var employees = repository.findEmployeesWithAddresses();
        for (var employee: employees) {
            for (var address: employee.getAddresses()) {
                log.info("address: {}", address);
            }
        }
    }

}
