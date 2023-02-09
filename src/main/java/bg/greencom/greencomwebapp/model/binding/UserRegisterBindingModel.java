package bg.greencom.greencomwebapp.model.binding;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRegisterBindingModel {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;



    @NotBlank(message = "Please enter your first name.")
    @Size(min = 2, max = 10, message = "First name must be between 2 and 10 letters.")
    public String getFirstName() {
        return firstName;
    }

    public UserRegisterBindingModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @NotBlank(message = "Please enter your last name.")
    @Size(min = 2, max = 10, message = "Last name must be between 2 and 10 letters")
    public String getLastName() {
        return lastName;
    }

    public UserRegisterBindingModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @NotBlank(message = "Please enter a username.")
    @Size(min = 3, max = 8, message = "Username must be between 3 and 8 characters.")
    public String getUsername() {
        return username;
    }

    public UserRegisterBindingModel setUsername(String username) {
        this.username = username;
        return this;
    }

    @Email
    public String getEmail() {
        return email;
    }

    public UserRegisterBindingModel setEmail(String email) {
        this.email = email;
        return this;
    }

    @NotBlank(message = "Please enter a password.")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
    public String getPassword() {
        return password;
    }

    public UserRegisterBindingModel setPassword(String password) {
        this.password = password;
        return this;
    }

    @NotBlank(message = "Please enter a password.")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public UserRegisterBindingModel setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }
}
