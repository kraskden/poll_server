package org.kraskovsky.polls.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "field_answers")
@Data
public class FieldAnswer extends BaseEntity {

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="field_id")
    private Field field;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name="poll_answer_id")
    private PollAnswer pollAnswer;

    @NotBlank
    private String answer;

}
