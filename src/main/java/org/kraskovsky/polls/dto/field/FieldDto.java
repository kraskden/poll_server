package org.kraskovsky.polls.dto.field;

import lombok.Data;
import org.kraskovsky.polls.model.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FieldDto {

    @NotEmpty
    private String name;

    @NotEmpty
    private Boolean isRequired;

    @NotEmpty
    private Boolean isEnabled;

    @NotEmpty
    private FieldType fieldType;

    @NotEmpty
    private List<String> properties;

}
