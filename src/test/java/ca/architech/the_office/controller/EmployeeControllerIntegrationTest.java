package ca.architech.the_office.controller;

import ca.architech.the_office.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp(){
        baseUrl = "http://localhost:" + port + "/api/employees";
    }

    @Test
    void testCreateAndGetEmployee() {
        Employee employee = Employee.builder()
                .name("John Doe")
                .email("john@example.com")
                .age(30)
                .build();

        ResponseEntity<Employee> response = restTemplate.postForEntity(baseUrl, employee, Employee.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());

        Long createdId = response.getBody().getId();

        // Get the employee by ID
        ResponseEntity<Employee> getResponse = restTemplate.getForEntity(baseUrl + "/" + createdId, Employee.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("John Doe", getResponse.getBody().getName());
    }

    @Test
    void testGetAllEmployees() {
        // Create one employee
        Employee emp1 = Employee.builder().name("Alice").email("alice@example.com").age(25).build();
        restTemplate.postForEntity(baseUrl, emp1, Employee.class);

        // Create another employee
        Employee emp2 = Employee.builder().name("Bob").email("bob@example.com").age(28).build();
        restTemplate.postForEntity(baseUrl, emp2, Employee.class);

        // Get all
        ResponseEntity<Employee[]> response = restTemplate.getForEntity(baseUrl, Employee[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Employee> employees = Arrays.asList(response.getBody());
        assertTrue(employees.size() >= 2);
    }

    @Test
    void testUpdateEmployee() {
        Employee emp = Employee.builder().name("Old Name").email("old@example.com").age(40).build();
        Employee created = restTemplate.postForEntity(baseUrl, emp, Employee.class).getBody();

        Employee updated = Employee.builder().name("New Name").email("new@example.com").age(41).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> requestEntity = new HttpEntity<>(updated, headers);
        ResponseEntity<Employee> response = restTemplate.exchange(baseUrl + "/" + created.getId(),
                HttpMethod.PUT, requestEntity, Employee.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Name", response.getBody().getName());
    }

    @Test
    void testDeleteEmployee() {
        Employee emp = Employee.builder().name("To Delete").email("delete@example.com").age(50).build();
        Employee created = restTemplate.postForEntity(baseUrl, emp, Employee.class).getBody();

        restTemplate.delete(baseUrl + "/" + created.getId());

        // try to get deleted employee
        ResponseEntity<Employee> response = restTemplate.getForEntity(baseUrl + "/" + created.getId(), Employee.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
