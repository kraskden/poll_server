package org.kraskovsky.polls.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kraskovsky.polls.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class ProfileDto {

    @NotBlank
    @Email
    private String email;

    private String firstName;
    private String lastName;
    private String phone;

    public User toUser() {
        User user = new User();
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setFirstName(this.firstName);
        user.setSecondName(this.lastName);

        return user;
    }
}
