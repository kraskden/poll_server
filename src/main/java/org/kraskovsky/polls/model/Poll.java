package org.kraskovsky.polls.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "polls")
@Data
public class Poll extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
