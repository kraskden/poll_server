package org.kraskovsky.polls.dto.login;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginReqDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
