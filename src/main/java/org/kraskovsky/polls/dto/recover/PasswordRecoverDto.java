package org.kraskovsky.polls.dto.recover;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class PasswordRecoverDto {

    @Email
    private String email;
}
