package org.kraskovsky.polls.model;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        })
})
@lombok.Data
public class User extends BaseEntity {
    @NaturalId
    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "username")
    private String email;

    @NotBlank
    private String password;

    private String firstName;
    private String secondName;

    private String phone;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Poll> polls;

    public User(String email, String password, String firstName, String secondName, String phone) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
        this.phone = phone;
        this.polls = new ArrayList<>();
    }

    public User() {}
}
