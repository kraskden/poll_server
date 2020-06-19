package org.kraskovsky.polls.dto.field;

import lombok.Data;

import java.util.List;

@Data
public class GetResDto {
    private List<FieldDto> fields;
}
