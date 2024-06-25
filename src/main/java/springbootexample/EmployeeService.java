package springbootexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public EmployeeTaxResponse calculateTax(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
        return calculateTaxForEmployee(employee);
    }

    private EmployeeTaxResponse calculateTaxForEmployee(Employee employee) {
        LocalDate doj = employee.getDoj();
        LocalDate currentDate = LocalDate.now();
        int monthsWorked = calculateMonthsWorked(doj, currentDate);

        double totalSalary = employee.getSalary() * monthsWorked;
        double yearlySalary = totalSalary;

        double tax = 0;
        double cess = 0;

        if (yearlySalary > 250000) {
            if (yearlySalary <= 500000) {
                tax = (yearlySalary - 250000) * 0.05;
            } else if (yearlySalary <= 1000000) {
                tax = 250000 * 0.05 + (yearlySalary - 500000) * 0.10;
            } else {
                tax = 250000 * 0.05 + 500000 * 0.10 + (yearlySalary - 1000000) * 0.20;
            }
        }

        if (yearlySalary > 2500000) {
            cess = (yearlySalary - 2500000) * 0.02;
        }

        return new EmployeeTaxResponse(employee.getId(), employee.getFirstName(), employee.getLastName(), yearlySalary, tax, cess);
    }

    private int calculateMonthsWorked(LocalDate doj, LocalDate currentDate) {
        if (doj.isAfter(currentDate.withDayOfMonth(1))) {
            return 0;
        }
        LocalDate start = doj.withDayOfMonth(1);
        LocalDate end = currentDate.withDayOfMonth(1);
        return (int) ChronoUnit.MONTHS.between(start, end);
    }
}
