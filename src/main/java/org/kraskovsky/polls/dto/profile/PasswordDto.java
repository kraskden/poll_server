package org.kraskovsky.polls.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class PasswordDto {

    @NotBlank
    private String current;

    @NotBlank
    private String updated;
}
