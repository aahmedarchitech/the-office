package ca.architech.the_office.service;

import ca.architech.the_office.exception.ResourceNotFoundException;
import ca.architech.the_office.model.Employee;
import ca.architech.the_office.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    public Employee createEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee){
        Employee existingEmployee = getEmployeeById(id);
        existingEmployee.setName(updatedEmployee.getName());
        existingEmployee.setEmail(updatedEmployee.getEmail());
        existingEmployee.setAge(updatedEmployee.getAge());
        return employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(Long id){
        Employee emp = getEmployeeById(id);
        employeeRepository.delete(emp);
    }
}
