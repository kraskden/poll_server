package org.kraskovsky.polls.dto.poll;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kraskovsky.polls.model.BaseEntity;
import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.FieldType;
import org.kraskovsky.polls.model.Poll;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
class FieldInfo {
    private String name;
    private FieldType type;
}

@Data
@AllArgsConstructor
public class PollDto {

    private Long id;
    private Date date;
    private Integer answersCount;

    private List<FieldInfo> fields;

    public static PollDto fromPoll(Poll poll) {
        List<FieldInfo> pollFields = poll.getFields().stream()
                .sorted(Comparator.comparing(BaseEntity::getId))
                .map(f -> new FieldInfo(f.getName(), f.getFieldType())).collect(Collectors.toList());
        return new PollDto(
                poll.getId(),
                poll.getCreatedDate(),
                poll.getAnswersCount(),
                pollFields
        );
    }
}
