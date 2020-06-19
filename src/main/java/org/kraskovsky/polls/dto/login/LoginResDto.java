package org.kraskovsky.polls.dto.login;

import lombok.Data;

@Data
public class LoginResDto {
    private String email;
    private String token;

    public LoginResDto(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
