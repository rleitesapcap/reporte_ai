package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;

public class EmployeeBuilder {
    private Long id;
    private String name;
    private String email;
    private String cpf;
    private String position;
    private Double salary;
    private String department;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;

    public EmployeeBuilder() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public EmployeeBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public EmployeeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public EmployeeBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public EmployeeBuilder withCpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public EmployeeBuilder withPosition(String position) {
        this.position = position;
        return this;
    }

    public EmployeeBuilder withSalary(Double salary) {
        this.salary = salary;
        return this;
    }

    public EmployeeBuilder withDepartment(String department) {
        this.department = department;
        return this;
    }

    public EmployeeBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public EmployeeBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public EmployeeBuilder withActive(Boolean active) {
        this.active = active;
        return this;
    }

    public EmployeeBuilder inactive() {
        this.active = false;
        return this;
    }

    public Employee build() {
        validateRequiredFields();

        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setCpf(cpf);
        employee.setPosition(position);
        employee.setSalary(salary);
        employee.setDepartment(department);
        employee.setCreatedAt(createdAt);
        employee.setUpdatedAt(updatedAt);
        employee.setActive(active);

        return employee;
    }

    private void validateRequiredFields() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Posição é obrigatória");
        }
        if (salary == null || salary <= 0) {
            throw new IllegalArgumentException("Salário deve ser maior que zero");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Departamento é obrigatório");
        }
    }

    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }
}