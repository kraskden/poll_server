package org.kraskovsky.polls.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "questions")
@Data
public class Field extends BaseEntity {

    @NotBlank
    private String name;

    @NotBlank
    private Boolean isRequired;

    @NotBlank
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @NotBlank
    private FieldType fieldType;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    List<FieldProperty> properties;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "poll_id")
    private Poll poll;

    public Field() {}

    public Field(String name, Boolean isActive, Boolean isRequired, FieldType fieldType) {
        this.name = name;
        this.isActive = isActive;
        this.isRequired = isRequired;
        this.fieldType = fieldType;
    }


}
