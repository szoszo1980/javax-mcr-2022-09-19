package training.employees.employees.jsf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import training.employees.employees.dto.EmployeeDto;
import training.employees.employees.service.EmployeesService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Component
@RequestScope
@AllArgsConstructor
public class EmployeesBackingBean {

    private EmployeesService service;

    @Getter
    private List<EmployeeDto> employees;

    @PostConstruct
    public void initEmployees() {
        employees = service.listEmployees(Optional.empty());
    }
}
