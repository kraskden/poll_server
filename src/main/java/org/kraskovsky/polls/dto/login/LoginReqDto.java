package org.kraskovsky.polls.dto.login;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginReqDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
