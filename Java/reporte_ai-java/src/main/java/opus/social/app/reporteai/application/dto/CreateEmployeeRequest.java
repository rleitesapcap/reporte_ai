package opus.social.app.reporteai.application.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para requisição de criação de funcionário
 */
public class CreateEmployeeRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    @NotBlank(message = "Cargo é obrigatório")
    private String position;

    @NotNull(message = "Salário é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salário deve ser maior que zero")
    private Double salary;

    @NotBlank(message = "Departamento é obrigatório")
    private String department;

    // Construtores
    public CreateEmployeeRequest() {
    }

    public CreateEmployeeRequest(String name, String email, String cpf, String position, Double salary, String department) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.position = position;
        this.salary = salary;
        this.department = department;
    }

    // Getters e Setters
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
}