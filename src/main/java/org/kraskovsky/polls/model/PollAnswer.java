package org.kraskovsky.polls.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "poll_answers")
@Data
public class PollAnswer extends BaseEntity {

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name ="poll_id")
    private Poll poll;

    @OneToMany(mappedBy = "pollAnswer", cascade = CascadeType.ALL)
    private List<FieldAnswer> answers;
}
