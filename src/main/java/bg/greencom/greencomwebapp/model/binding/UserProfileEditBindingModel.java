package bg.greencom.greencomwebapp.model.binding;

import bg.greencom.greencomwebapp.validation.annotation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserProfileEditBindingModel {

    private String firstName;
    private String lastName;
    private String email;

    public UserProfileEditBindingModel() {
    }

    @NotBlank(message = "Please enter your first name.")
    @Size(min = 2, max = 10, message = "First name must be between 2 and 10 letters.")
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    @NotBlank(message = "Please enter your last name.")
    @Size(min = 2, max = 10, message = "Last name must be between 2 and 10 letters")
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @UniqueEmail
    @Email(message = "Enter a valid email address.")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
