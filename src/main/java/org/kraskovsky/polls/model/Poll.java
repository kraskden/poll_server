package org.kraskovsky.polls.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "polls")
@Data
public class Poll extends BaseEntity {

    @CreationTimestamp
    private Date createdDate;

    @NotNull
    @Column(name="answers_count", columnDefinition = "integer default 0")
    private Integer answersCount;

    @OneToMany(mappedBy = "poll", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Field> fields;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    private List<PollAnswer> answers;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name="user_id")
    private User user;

    public Poll() {}

    public Poll(User u) {
        this.user = u;
        this.answersCount = 0;
        this.fields = new ArrayList<>();
        this.answers = new ArrayList<>();
    }

}
