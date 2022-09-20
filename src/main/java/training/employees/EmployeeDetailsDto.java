package training.employees;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDetailsDto {

    private Long id;

    private String name;

    private int yearOfBirth;
}
