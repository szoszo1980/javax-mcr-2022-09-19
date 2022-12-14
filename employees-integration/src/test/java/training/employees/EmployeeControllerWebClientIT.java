package training.employees;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import training.employees.employees.dto.CreateEmployeeCommand;
import training.employees.employees.dto.EmployeeDetailsDto;
import training.employees.employees.dto.EmployeeDto;
import training.employees.employees.gateway.AddressesGateway;
import training.employees.employees.gateway.ExternalAddressDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerWebClientIT {

    @Autowired
    WebTestClient webClient;

//    @Autowired
//    EmployeesRepository repository;

//    @BeforeEach
//    void deleteAll() {
//        repository.deleteAll();
//    }

    @MockBean
    AddressesGateway addressesGateway;

    @Test
    void testCreateEmployee() {
        when(addressesGateway.getAddressToEmployee(anyString()))
                .thenReturn(new ExternalAddressDto("Budapest", "Fő út 30."));

        var result = webClient
                .post()
                .uri("/api/employees")
                .bodyValue(new CreateEmployeeCommand("Amber Doe", 1970, 0))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EmployeeDetailsDto.class).value(e -> assertEquals("Amber Doe", e.getName()))
                .returnResult().getResponseBody();
        ;

        long id = result.getId();

        webClient
                .get()
                .uri("/api/employees")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .value(employees -> {
                    System.out.println(employees);
                    assertTrue(employees.stream()
                        .anyMatch(e -> e.getName().equals("Amber Doe")));});

        webClient
                .get()
                .uri("/api/employees/{id}", id)
                .exchange()
                .expectBody(EmployeeDetailsDto.class)
                .value(e -> assertEquals("Amber Doe", e.getName()));
    }

    @Test
    void testCreateInvalidEmployee() {
        webClient
                .post()
                .uri("/api/employees")
                .bodyValue(new CreateEmployeeCommand("John Doe", 2001, 0))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testNotFound() {
        webClient
                .get()
                .uri("/api/employees/{id}", Integer.MAX_VALUE)
                .exchange()
                .expectStatus().isNotFound();
    }
}
