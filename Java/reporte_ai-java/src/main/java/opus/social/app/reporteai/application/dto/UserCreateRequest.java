package opus.social.app.reporteai.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public class UserCreateRequest {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String name;

    @Email(message = "Email must be valid")
    private String email;

    public UserCreateRequest() {
    }

    public UserCreateRequest(String phoneNumber, String name, String email) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
}
