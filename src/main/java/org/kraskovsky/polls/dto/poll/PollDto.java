package org.kraskovsky.polls.dto.poll;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kraskovsky.polls.model.Poll;

import java.util.Date;

@Data
@AllArgsConstructor
public class PollDto {

    private Long id;
    private Date date;
    private Integer answersCount;

    public static PollDto fromPoll(Poll poll) {
        return new PollDto(
                poll.getId(),
                poll.getCreatedDate(),
                poll.getAnswersCount()
        );
    }
}
