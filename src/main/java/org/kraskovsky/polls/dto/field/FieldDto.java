package org.kraskovsky.polls.dto.field;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.FieldProperty;
import org.kraskovsky.polls.model.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class FieldDto {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Boolean isRequired;

    @NonNull
    private Boolean isEnabled;

    @NotNull
    private FieldType fieldType;

    @NotNull
    private List<String> properties;

    public FieldDto() {}
    
    public Field toField() {
        Field ret = new Field();

        ret.setName(this.getName());
        ret.setIsActive(this.getIsEnabled());
        ret.setIsRequired(this.getIsRequired());
        ret.setFieldType(this.getFieldType());

        List<FieldProperty> properties = this.getProperties().stream()
                .map(propName -> {
                    FieldProperty prop = new FieldProperty();
                    prop.setField(ret);
                    prop.setName(propName);
                    return prop;
                }).collect(Collectors.toList());
        ret.setProperties(properties);
        return ret;
    }

    public static FieldDto fromField(Field f) {
        FieldDto dto = new FieldDto();

        dto.setId(f.getId());
        dto.setName(f.getName());
        dto.setFieldType(f.getFieldType());
        dto.setIsEnabled(f.getIsActive());
        dto.setIsRequired(f.getIsRequired());

        List<String> props = f.getProperties().stream()
                .map(FieldProperty::getName).collect(Collectors.toList());

        dto.setProperties(props);

        return dto;
    }

}
