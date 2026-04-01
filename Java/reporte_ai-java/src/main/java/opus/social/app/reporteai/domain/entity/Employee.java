package opus.social.app.reporteai.domain.entity;

import java.time.LocalDateTime;

/**
 * Entidade de Domínio: Funcionário
 * Representa um funcionário da organização
 */
public class Employee {

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

    // Construtores
    public Employee() {
    }

    public Employee(String name, String email, String cpf, String position, Double salary, String department) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.position = position;
        this.salary = salary;
        this.department = department;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", department='" + department + '\'' +
                '}';
    }

    // Builder Pattern Support
    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }
}